package com.example.BarterApp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.R
import com.example.BarterApp.model.ProductPersonal
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_row.view.*

class StockAdapter(option: FirestoreRecyclerOptions<ProductPersonal>) :
    FirestoreRecyclerAdapter<ProductPersonal, StockAdapter.StockAdapterVH>(option) {


    class StockAdapterVH(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var productName = itemView.name_product
        var productHand = itemView.hand_product
        var productQuality = itemView.percent_product
        var productTime = itemView.time_product
        var productImage = itemView.img_product
        var productStatus = itemView.state_product

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockAdapterVH {
       return StockAdapterVH(LayoutInflater.from(parent.context).inflate(R.layout.rv_row,parent,false))
    }
    override fun onBindViewHolder(holder: StockAdapterVH, position: Int, model: ProductPersonal) {


        if (model.ProductState.toString() == "1")
            holder.productStatus.text = "มีของ"
        else if (model.ProductState.toString() == "2") {
            holder.productStatus.text = "แลกเปลี่ยน"
        } else if (model.ProductState.toString() == "0") {
            holder.productStatus.text = "ไม่มีของ"
        }


        holder.productName.text = model.ProductName
        holder.productHand.text = "มือ " + model.ProductHand.toString()
        holder.productQuality.text = model.ProductQuality
        holder.productTime.text = model.ProductTime

        Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.productImage)




    }

fun itemCount(){
return

}

}