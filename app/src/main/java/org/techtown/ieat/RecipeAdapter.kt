package org.techtown.ieat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecipeAdapter(val itemList: ArrayList<RecipeData>) :
    RecyclerView.Adapter<RecipeAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        holder.Recipe_id.text = itemList[position].recipeId
        holder.Recipe_ex.text = itemList[position].recipeEx
    }

    override fun getItemCount(): Int {
        return itemList.count()
    }


    inner class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val Recipe_id = itemView.findViewById<TextView>(R.id.rcp_Id)
        val Recipe_ex = itemView.findViewById<TextView>(R.id.rcp_Ex)
    }
}