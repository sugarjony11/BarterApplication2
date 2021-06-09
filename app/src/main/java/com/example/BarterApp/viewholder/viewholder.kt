package com.example.login.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.R

class viewholder(itemView: View):RecyclerView.ViewHolder(itemView) {

    var imgid = itemView.findViewById<ImageView>(R.id.img_product)
    var name = itemView.findViewById<TextView>(R.id.name_product)
   // var type = itemView.findViewById<TextView>(R.id.type_product)
    var percent = itemView.findViewById<TextView>(R.id.percent_product)
    var time = itemView.findViewById<TextView>(R.id.time_product)





    init {
        itemView.setOnClickListener{
           // Toast.makeText(itemView.context,name.text,Toast.LENGTH_LONG).show()

        }
    }

   




}