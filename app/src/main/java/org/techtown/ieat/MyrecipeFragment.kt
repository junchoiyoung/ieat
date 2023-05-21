package org.techtown.ieat

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyrecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyrecipeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var Myrecipeview: RecyclerView
    lateinit var MyrecipeAdapter: MyrecipeAdapter
    lateinit var food:Array<food_data>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_myrecipe, container, false)

        Myrecipeview = view.findViewById(R.id.Myrecipeview)
        food = temdfood()
        setAdapter()
        return view
    }

    fun temdfood(): Array<food_data> {
        return arrayOf(
            food_data(R.drawable.eggplantcook,"가지볶음","1.가지는 어슷하게 썰어서 약간의 소금으로 간을 한다. 돼지고기는 곱게 다져서 간장, 설탕, 후춧가루, 맛술, 깨소금, 참기름으로 양념한다.\n2.①의 가지에 물기가 생기면 깨끗한 거즈로 닦아낸 후 밀가루를 살짝 묻힌다.\n3.①의 가지에 물기가 생기면 깨끗한 거즈로 닦아낸 후 밀가루를 살짝 묻힌다.\\n4.③의 가지에 ②의 돼지고기를 얇게 바르고 다른 가지로 샌드위치처럼 겹쳐준다.\n5.④의 가지에 밀가루, 계란, 빵가루 순으로 묻힌 후 180℃ 온도에서 튀겨낸다.\n6.머스터드 1/2큰술 , 마요네즈 2큰술, 설탕 1/4큰술, 소금 약간으로 드레싱을 만들어 곁들인다."),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구"),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구"),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구"),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구"),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구"),
            food_data(R.drawable.eggplantcook,"가지볶음","어쩌구저쩌구")
        )

    }

    fun setAdapter(){
        //리사이클러뷰에 리사이클러뷰 어댑터 부착
        Myrecipeview.layoutManager = LinearLayoutManager(requireContext())
        MyrecipeAdapter = MyrecipeAdapter(food, requireContext())
        Myrecipeview.adapter = MyrecipeAdapter
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyrecipeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyrecipeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}