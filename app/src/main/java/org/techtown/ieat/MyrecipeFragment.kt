package org.techtown.ieat

import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

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

    private lateinit var values : ArrayList<String>
    var ingred = mutableListOf<String>()
    var recipeId = mutableListOf<String>()
    private var Tag = "양띵: "
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyrecipeAdapter
    private val itemList = ArrayList<Myrecipe_data>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_myrecipe, container, false)
        recyclerView = view.findViewById(R.id.allView_my)
        adapter = MyrecipeAdapter(itemList,recyclerView,requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val dbHelper = MyDataBaseHelper(requireContext())
        refresh(dbHelper)
        return view
    }

    fun ingredient(dbHelper: MyDataBaseHelper){

        val database = dbHelper.readableDatabase

        val ingredient_table = "ingred"
        val ingredient_columns = arrayOf("INGREDIENT")

        val cursor = database.query(ingredient_table, ingredient_columns, null, null, null, null, null)

        while (cursor.moveToNext()) {
            val recipe = cursor.getString(cursor.getColumnIndexOrThrow("INGREDIENT"))
            ingred.add(recipe)
            Log.d(Tag, recipe)
        }

        cursor.close()
    }

    fun recipID(dbHelper: MyDataBaseHelper){
        val database = dbHelper.readableDatabase

        values = ArrayList(ingred)

        val ingredient_table = "recipe_ingredient"
        val ingredient_columns = arrayOf("RECIPE_ID")
        var selection = "IRDNT_NM IN (${values.map { "?" }.joinToString()})"
        Log.d(Tag, values.toString())
        Log.d(Tag, selection)
        var selectionsArgs = values.toTypedArray()

        val cursor = database.query(ingredient_table, ingredient_columns, selection, selectionsArgs, null, null, null)

        while (cursor.moveToNext()) {
            val recipe = cursor.getString(cursor.getColumnIndexOrThrow("RECIPE_ID"))
            recipeId.add(recipe)
            Log.d(Tag, recipe)
        }

        cursor.close()
    }

    fun recipe(dbHelper: MyDataBaseHelper){
        val database = dbHelper.readableDatabase

        val tableName = "recipe_basic"
        val columns = arrayOf("RECIPE_NM_KO", "SUMRY", "IMG_URL", "RECIPE_ID")
        var selection = "RECIPE_ID IN (${recipeId.map { "?" }.joinToString()})"
        var selectionsArgs = recipeId.toTypedArray()
        val main_cursor = database.query(tableName, columns, selection, selectionsArgs, null, null, null)

        while (main_cursor.moveToNext()) {
            val recipeName = main_cursor.getString(main_cursor.getColumnIndexOrThrow("RECIPE_NM_KO"))
            val summary = main_cursor.getString(main_cursor.getColumnIndexOrThrow("SUMRY"))
            val recipeNumber = main_cursor.getString(main_cursor.getColumnIndexOrThrow("RECIPE_ID"))
            val imgUrl = main_cursor.getString(main_cursor.getColumnIndexOrThrow("IMG_URL"))
            Log.d(Tag, recipeName+ summary+ imgUrl)
            itemList.add(Myrecipe_data(recipeName, summary,recipeNumber, imgUrl))
        }

        main_cursor.close()
    }

    fun refresh(dbHelper: MyDataBaseHelper){
        itemList.clear()
        ingredient(dbHelper)
        recipID(dbHelper)
        recipe(dbHelper)
        adapter.notifyDataSetChanged()
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