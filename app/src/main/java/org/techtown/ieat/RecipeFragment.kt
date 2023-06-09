package org.techtown.ieat

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
 * Use the [RecipeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecipeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private val itemList = ArrayList<RecipeData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_recipe, container, false)
        recyclerView = view.findViewById(R.id.allView)
        adapter = RecipeAdapter(itemList,recyclerView,requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val dbHelper = DataBaseHelper(requireContext())
        val database = dbHelper.readableDatabase

        val tableName = "recipe_basic"
        val columns = arrayOf("RECIPE_NM_KO", "SUMRY", "IMG_URL", "RECIPE_ID")
        val cursor = database.query(tableName, columns, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val recipeName = cursor.getString(cursor.getColumnIndexOrThrow("RECIPE_NM_KO"))
            val summary = cursor.getString(cursor.getColumnIndexOrThrow("SUMRY"))
            val recipeNumber = cursor.getString(cursor.getColumnIndexOrThrow("RECIPE_ID"))
            val imgUrl = cursor.getString(cursor.getColumnIndexOrThrow("IMG_URL"))
            itemList.add(RecipeData(recipeName, summary, recipeNumber,imgUrl))
        }

        cursor.close()
        dbHelper.close()
        adapter.notifyDataSetChanged()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecipeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecipeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}