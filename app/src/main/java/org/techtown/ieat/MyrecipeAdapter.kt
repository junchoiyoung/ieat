package org.techtown.ieat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MyrecipeAdapter(var food: Array<food_data>, var con: Context) :
    RecyclerView.Adapter<MyrecipeAdapter.ViewHolder>() {
    var TAG = "MyrecipeAdapter"

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var foodimage: ImageView
        lateinit var foodname: TextView
        lateinit var foodrecipe: TextView

        init {
            foodimage = itemView.findViewById(R.id.food_image)
            foodname = itemView.findViewById(R.id.food_name)
            foodrecipe = itemView.findViewById(R.id.food_recipe)

            itemView.setOnClickListener{
                AlertDialog.Builder(con).apply {
                    var position = adapterPosition
                    var foodData = food[position]
                    setTitle(foodData.foodname)
                    setMessage(foodData.foodrecipe)
                    setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(con, "OK BUTTON CLICK", Toast.LENGTH_SHORT).show()
                    })
                    show()
                }

            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val con = parent.context
        val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.recipe_recycler_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person: food_data = food[position]
        //[수정요함] 이미지 작업의 경우 glide를 사용해 server의 image를 불러올 것
        //holder.iv_person_phone_book_list_item
        holder.foodname.text = person.foodname
        holder.foodrecipe.text = person.foodrecipe
    }

    override fun getItemCount(): Int {
        return food.size
    }
}