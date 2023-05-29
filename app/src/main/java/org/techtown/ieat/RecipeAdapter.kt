package org.techtown.ieat

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val itemList: ArrayList<RecipeData>, private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<RecipeAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipe_id = itemView.findViewById<TextView>(R.id.rcp_Id)
        val recipe_ex = itemView.findViewById<TextView>(R.id.rcp_Ex)
        val repImg = itemView.findViewById<ImageView>(R.id.rcp_Img)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.recipe_id.text = itemList[position].recipeId
        holder.recipe_ex.text = itemList[position].recipeEx
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
