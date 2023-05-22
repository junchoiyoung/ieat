package org.techtown.ieat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(private val itemList: ArrayList<RecipeData>) :
    RecyclerView.Adapter<RecipeAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.count()
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
    //약간의 여유를 가지고~
}