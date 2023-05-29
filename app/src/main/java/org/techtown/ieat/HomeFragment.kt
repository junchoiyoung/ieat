package org.techtown.ieat


import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

private lateinit var bitmap: Bitmap
private lateinit var imageView: ImageView
private lateinit var textView: TextView

private val cameraPermissionRequestCode = 1001
private val cameraRequestCode = 1002
private const val STORAGE_CODE = 99
private val OPENGALLERY = 1

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null

    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
        }

        // TensorFlow Lite 모델 초기화
        interpreter = Interpreter(FileUtil.loadMappedFile(requireContext(), "best-fp16.tflite"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        imageView = view.findViewById(R.id.avatars)
        textView = view.findViewById(R.id.recog_real)
        val picBtn = view.findViewById<ImageButton>(R.id.btn_camera)
        val galleryBtn = view.findViewById<ImageButton>(R.id.btn_gallery)
        val btnReset = view.findViewById<ImageButton>(R.id.btn_reset)

        picBtn.setOnClickListener {
            requestCameraPermission()
        }

        galleryBtn.setOnClickListener {
            openGallery()
        }

        btnReset.setOnClickListener {
            imageView.setImageBitmap(null)
            textView.text = ""
            Toast.makeText(requireContext(), "초기화 되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                cameraPermissionRequestCode
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(takePictureIntent, cameraRequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            val extras = data.extras
            val capturedImage = extras?.get("data") as Bitmap
            imageView.setImageBitmap(capturedImage)

            saveImageToGallery(capturedImage)
        } else if (requestCode == OPENGALLERY && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                loadImageFromGallery(imageUri)
            }
        }
    }

    private fun recognizeObject(image: Bitmap) {
        try {
            // 이미지 전처리
            val resizedImage = Bitmap.createScaledBitmap(image, 416, 416, true)
            val inputImageBuffer =
                TensorImage(DataType.FLOAT32).apply { load(resizedImage) }
            val outputProbabilityBuffer =
                TensorBuffer.createFixedSize(intArrayOf(1, 416, 416, 3), DataType.FLOAT32)

            // 객체 감지
            interpreter.run(inputImageBuffer.buffer, outputProbabilityBuffer.buffer.rewind())

            // 결과 분석
            val results = outputProbabilityBuffer.floatArray
            val label = "Detected Object: " + analyzeResults(results)

            // 결과 표시
            textView.text = label
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "객체 감지에 실패하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun analyzeResults(results: FloatArray): String {
        // TODO: 결과 분석 및 처리 로직 구현

        // 여기서는 단순히 가장 높은 확률을 가진 라벨을 반환하는 예시입니다.
        val labels = arrayOf("Label A", "Label B", "Label C") // 라벨 목록
        val maxProbIndex = results.indexOfFirst { it == results.maxOrNull() }
        return labels[maxProbIndex]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "카메라 권한이 거부되었습니다.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, OPENGALLERY)
    }

    private fun saveImageToGallery(bitmap: Bitmap) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
        val displayName = "IMG_$timeStamp.jpg"

        val imageCollection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val imageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        try {
            val contentResolver = requireContext().contentResolver
            val imageUri = contentResolver.insert(imageCollection, imageDetails)
            if (imageUri != null) {
                val outputStream = contentResolver.openOutputStream(imageUri)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream?.close()
                Toast.makeText(requireContext(), "사진이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()
                loadImageFromGallery(imageUri) // 이미지 저장 후에 loadImageFromGallery 호출
            } else {
                Toast.makeText(requireContext(), "사진 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(requireContext(), "사진 저장에 실패하였습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImageFromGallery(imageUri: Uri) {
        try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            imageView.setImageBitmap(bitmap)
            recognizeObject(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(
                requireContext(),
                "갤러리에서 이미지를 불러오는데 실패하였습니다.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                    putString(ARG_PARAM3, param3)
                }
            }
    }
}