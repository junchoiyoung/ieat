package org.techtown.ieat


import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.tensorflow.lite.Interpreter
import org.w3c.dom.Text
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class HomeFragment : Fragment() {
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val CAMERA_REQUEST_CODE = 1002
    private val GALLERY_REQUEST_CODE = 1003

    private lateinit var interpreter: Interpreter
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    private val Tag = "양띵: "

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imageView = view.findViewById(R.id.imageView)
        val cameraButton = view.findViewById<ImageButton>(R.id.cameraButton)
        val galleryButton = view.findViewById<ImageButton>(R.id.galleryButton)
        val btnReset = view.findViewById<ImageButton>(R.id.btn_reset)
        textView = view.findViewById(R.id.resView)

        // TensorFlow Lite 모델 로드
        val assetManager = requireContext().assets
        val modelFilename = "best-fp162.tflite"
        val modelAssetFileDescriptor = assetManager.openFd(modelFilename)
        val modelFileChannel = FileInputStream(modelAssetFileDescriptor.fileDescriptor).channel
        val modelStartOffset = modelAssetFileDescriptor.startOffset
        val modelLength = modelAssetFileDescriptor.length
        val modelByteBuffer = modelFileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            modelStartOffset,
            modelLength
        )
        val interpreterOptions = Interpreter.Options()
        interpreter = Interpreter(modelByteBuffer, interpreterOptions)

        cameraButton.setOnClickListener {
            if (hasCameraPermission()) {
                openCamera()
            } else {
                requestCameraPermission()
            }
        }

        galleryButton.setOnClickListener {
            openGallery()
        }
        btnReset.setOnClickListener {
//            imageView.setImageBitmap(null)
            imageView.setImageResource(R.drawable.umseunglion)
            textView.text = "결과"
            Toast.makeText(requireContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    private fun openGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    imageBitmap?.let {
                        processImage(it)
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val imageUri = data?.data
                    imageUri?.let {
                        val imageBitmap = getBitmapFromUri(imageUri)
                        imageBitmap?.let {
                            processImage(it)
                        }
                    }
                }
            }
            imageView.setImageURI(data?.data)
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun processImage(bitmap: Bitmap) {
        // BoundingBox 클래스 정의
        data class BoundingBox(
            val label: String,
            val confidence: Float,
            val left: Float,
            val top: Float,
            val right: Float,
            val bottom: Float
        )

        // 클래스 라벨
        val classLabels = listOf(
            "콩나물", "쇠고기", "닭고기", "계란", "돼지고기", "마늘", "대파", "김치", "양파",
            "감자", "햄", "가지", "고추", "파프리카", "당근", "떡", "무",
            "소시지", "브로콜리", "시금치", "애호박", "양배추", "어묵", "오이", "참치"
        )

        // 신뢰도 임계값
        val CONFIDENCE_THRESHOLD = 0.5f

        // 이미지를 모델의 입력 크기에 맞게 조정
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 416, 416, true)

        // 입력 데이터를 담을 ByteBuffer 생성
        val inputBuffer = ByteBuffer.allocateDirect(1 * 416 * 416 * 3 * 4)
        inputBuffer.order(ByteOrder.nativeOrder())

        // Bitmap 이미지의 픽셀 값을 ByteBuffer로 변환
        val intValues = IntArray(416 * 416)
        resizedBitmap.getPixels(
            intValues,
            0,
            resizedBitmap.width,
            0,
            0,
            resizedBitmap.width,
            resizedBitmap.height
        )
        var pixel = 0
        for (i in 0 until 416) {
            for (j in 0 until 416) {
                val value = intValues[pixel++]
                inputBuffer.putFloat((value shr 16 and 0xFF) / 255f)  // Red 채널
                inputBuffer.putFloat((value shr 8 and 0xFF) / 255f)   // Green 채널
                inputBuffer.putFloat((value and 0xFF) / 255f)          // Blue 채널
            }
        }

        // 모델 실행
        val outputs = Array(1) { Array(10647) { FloatArray(30) } }
        interpreter.run(inputBuffer, outputs)

        // 출력에서 박스 정보 추출
        val outputBoxes = outputs[0]

        Log.d(Tag, "outputboexes_size" + outputBoxes.size.toString())

        // 객체 검출 결과 처리
//        val boundingBoxes = mutableListOf<BoundingBox>()
        var min = 0.0f
        var high_value = 0

        for (i in outputBoxes.indices) {
            val classProbabilities = outputBoxes[i]
            val classIndex =
                classProbabilities.slice(5 until 30).indices.maxByOrNull { classProbabilities[it + 5] }
            val classProbability = classProbabilities[classIndex!! + 5]

            if (classProbability > CONFIDENCE_THRESHOLD && min < classProbability) {
                high_value = classIndex
                min = classProbability
            }


//            if (classProbability > CONFIDENCE_THRESHOLD) {
//                val centerX = classProbabilities[0] * resizedBitmap.width
//                val centerY = classProbabilities[1] * resizedBitmap.height
//                val width = classProbabilities[2] * resizedBitmap.width
//                val height = classProbabilities[3] * resizedBitmap.height
//
//                val left = centerX - width / 2
//                val top = centerY - height / 2
//                val right = centerX + width / 2
//                val bottom = centerY + height / 2
//
//                val label = classLabels[classIndex]
//                val boundingBox = BoundingBox(label, classProbability, left, top, right, bottom)
//                boundingBoxes.add(boundingBox)
//            }
        }

        // 결과 출력
//        val labelText = StringBuilder()
//        for (boundingBox in boundingBoxes)
//            labelText.append("Label: ${boundingBox.label}\n")
//        val labels = boundingBoxes.joinToString("\n") { it.label }
        textView.text = classLabels[high_value]
    }
}