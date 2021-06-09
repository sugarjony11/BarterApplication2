package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.model.Product
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_row.view.*
import kotlinx.android.synthetic.main.rv_row1.view.*

class StockActivity : AppCompatActivity() {
    private lateinit var db  : DatabaseReference
    lateinit var mRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        db  = FirebaseDatabase.getInstance().getReference().child("Product")
        mRecyclerView = findViewById(R.id.recyclerInventory)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        setupRecycleView()
    }


    fun setupRecycleView(){

        val query = db.orderByChild("userID").equalTo(intent.getStringExtra("pdID"))

        val firebaseRecycleOption = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).setLifecycleOwner(this).build()

        var FirestoreRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(firebaseRecycleOption) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {

                return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_row1, parent, false))
            }


            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
                if (model.ProductState.toString() == "1")
                    holder.itemView.state_product1.text = "มีของ"
                else if (model.ProductState.toString() == "2") {
                    holder.itemView.state_product1.text = "แลกเปลี่ยน"
                } else if (model.ProductState.toString() == "0") {
                    holder.itemView.state_product1.text = "ไม่มีของ"
                }

                holder.itemView.name_product1.text = model.ProductName
                holder.itemView.hand_product1.text = "มือ " + model.ProductHand.toString()
                holder.itemView.percent_product1.text = model.ProductQuality
                holder.itemView.time_product1.text = model.ProductTime

                Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.itemView.img_product1)

                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        var id = getRef(position).key

                        if (intent.getStringExtra("other") == "1") {
                            val toInfo = Intent(this@StockActivity, InformationActivity::class.java)
                            Log.d("mail",intent.getStringExtra("EMAIL")!!)
                            toInfo.putExtra("Owner_EMAIL", intent.getStringExtra("EMAIL"))
                            toInfo.putExtra("NAME", getRef(position).key)
                            toInfo.putExtra("PDType", model.ProductType)

                            startActivity(toInfo)
                        } else {

                            val intent = Intent(this@StockActivity, InforProductUser::class.java)
                            intent.putExtra("NAME", getRef(position).key)
                            intent.putExtra("Email", model.USerID)
                            intent.putExtra("PRODUCTID", model.ProductID)



                            startActivity(intent)
                        }
                    }
                })
            }
        }
        mRecyclerView.adapter = FirestoreRecyclerAdapter

//        Stock = StockAdapter(firebaseRecycleOption)
//        recyclerViewCategory.layoutManager = LinearLayoutManager(this)
//        recyclerViewCategory.adapter = Stock

    }
    class ProductViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}