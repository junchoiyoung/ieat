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
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.techtown.ieat.ml.Best416
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer


class HomeFragment : Fragment() {
    private lateinit var selectBtn: Button
    private lateinit var predictBtn: Button
    private lateinit var resView: TextView
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap


    private var model: Best416? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        selectBtn = view.findViewById(R.id.selectBtn)
        predictBtn = view.findViewById(R.id.predictBtn)
        resView = view.findViewById(R.id.resView)
        imageView = view.findViewById(R.id.imageView)

        var labels = requireActivity().application.assets.open("best416_labels.txt").bufferedReader().readLines()

        var imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(416, 416, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        selectBtn.setOnClickListener {
            var intent = Intent()
            intent.setAction(Intent.ACTION_GET_CONTENT)
            intent.setType("image/*")
            startActivityForResult(intent, 100)
        }

        predictBtn.setOnClickListener {
            if (!::bitmap.isInitialized) {
                resView.setText("No image selected")
                return@setOnClickListener
            }

            val tensorImage = TensorImage(DataType.FLOAT32)
            tensorImage.load(bitmap)
            val processedImage = imageProcessor.process(tensorImage)

            val model = Best416.newInstance(requireContext())
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 416, 416, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(processedImage.buffer)

            val outputs = model.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer.floatArray

            // 임계값 설정
            val threshold = 0.5

            // 임계값 이상의 값에 해당하는 클래스 레이블 선택
            val selectedLabels = mutableListOf<String>()
            outputFeature0.forEachIndexed { index, fl ->
                if (fl >= threshold && index < labels.size) {  // 인덱스가 labels 리스트의 크기를 초과하지 않도록 체크
                    selectedLabels.add(labels[index])
                }
            }
            // 선택된 클래스 레이블을 resView에 표시
            resView.text = selectedLabels.joinToString(", ")

            model.close()
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            var uri = data?.data
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
            imageView.setImageBitmap(bitmap)
        }
    }
}