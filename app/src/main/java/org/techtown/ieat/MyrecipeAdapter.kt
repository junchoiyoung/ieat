package org.techtown.ieat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MyrecipeAdapter(private val itemList: ArrayList<Myrecipe_data>, private val recyclerView: RecyclerView,var con: Context) :
    RecyclerView.Adapter<MyrecipeAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recipe_id = itemView.findViewById<TextView>(R.id.rcp_Id)
        var recipe_ex = itemView.findViewById<TextView>(R.id.rcp_Ex)
        var recipe_no = itemView.findViewById<TextView>(R.id.rcp_No)
        var repImg = itemView.findViewById<ImageView>(R.id.rcp_Img)

        //클릭이벤트
        init {
            repImg = itemView.findViewById(R.id.rcp_Img)
            recipe_id = itemView.findViewById(R.id.rcp_Id)
            recipe_ex = itemView.findViewById(R.id.rcp_Ex)

            itemView.setOnClickListener{
                AlertDialog.Builder(con).apply {
                    val position = adapterPosition
                    val recipeData = itemList[position]
                    val recipeNo = recipeData.recipeNo
                    val recipeId = recipeData.recipeId

                    val dbHelper = DataBaseHelper(con)
                    val basicDataCursor = dbHelper.getRecipeBasicData(recipeNo)
                    val ingredientDataCursor = dbHelper.getRecipeIngredientData(recipeNo)
                    val processDataCursor = dbHelper.getRecipeProcessData(recipeNo)
                    var basicData = ""
                    var ingredientData = ""
                    var processData = ""

                    // recipe_ingredient 테이블 데이터 출력
                    if (ingredientDataCursor != null && ingredientDataCursor.moveToFirst()) {
                        val nameIndex = ingredientDataCursor.getColumnIndex("IRDNT_NM")
                        val capacityIndex = ingredientDataCursor.getColumnIndex("IRDNT_CPCTY")
                        while (!ingredientDataCursor.isAfterLast) {
                            ingredientData += "${ingredientDataCursor.getString(nameIndex)} "
                            ingredientData += "${ingredientDataCursor.getString(capacityIndex)}, "
                            ingredientDataCursor.moveToNext()
                        }
                    }

                    // recipe_process 테이블 데이터 출력
                    if (processDataCursor != null && processDataCursor.moveToFirst()) {
                        val noIndex = processDataCursor.getColumnIndex("COOKING_NO")
                        val descIndex = processDataCursor.getColumnIndex("COOKING_DC")
                        while (!processDataCursor.isAfterLast) {
                            processData += "${processDataCursor.getString(noIndex)}. "
                            processData += "${processDataCursor.getString(descIndex)}\n"
                            processDataCursor.moveToNext()
                        }
                    }
                    setTitle(recipeId)

                    setMessage(" 재료\n$ingredientData \n 조리방법\n$processData")
                    setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    })
                    show()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.recipe_id.text = itemList[position].recipeId
        holder.recipe_ex.text = itemList[position].recipeEx
        holder.recipe_no.text = itemList[position].recipeNo
        Glide.with(holder.itemView)
            .load(itemList[position].imgUrl)
            .error(R.drawable.eat_icon)
            .into(holder.repImg)

        println(holder.recipe_id.text)
        println(holder.recipe_ex.text)
        println(itemList[position].imgUrl)
    }

    init {
        // 구분선(Divider)의 Drawable 가져오기
        val divider: Drawable? = recyclerView.context.getDrawable(R.drawable.divider)

        // DividerItemDecoration 생성 및 Drawable 설정
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context, DividerItemDecoration.VERTICAL)
        if (divider != null) {
            dividerItemDecoration.setDrawable(divider)
        }

        // 실제 RecyclerView에 DividerItemDecoration 추가
        recyclerView.addItemDecoration(dividerItemDecoration)
    }
}