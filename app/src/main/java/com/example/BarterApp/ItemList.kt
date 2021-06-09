package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.model.Product
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_item_list.*
import kotlinx.android.synthetic.main.rv_row.view.*


class ItemList : AppCompatActivity() {

    lateinit var mSearch: EditText
    lateinit var mSearchBtn: ImageView
    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_item_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Product")
//        mDatabase = FirebaseDatabase.getInstance().getReference().child("Product").child(intent.getStringExtra("Type")!!)
        categoreName.setText("หมวดหมู่ : "+ intent.getStringExtra("Type"))


        mSearch = findViewById(R.id.searchText)
        mSearchBtn = findViewById(R.id.search_btn)
        mRecyclerView = findViewById(R.id.recyclerViewCategory)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        showRecyclerView()

        mSearchBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val searchText = mSearch.getText().toString()
                if(searchText.isEmpty()){
                    showRecyclerView()
                }
                else{
                    firebaseUserSearch(searchText)
                }

            }

        })

//        search_clear.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                mSearch.setText("")
//                showRecyclerView()
//            }
//
//        })



    }


    private fun showRecyclerView(){

        //  val query: Query = mDatabase.orderByChild("productName").startAt(searchText).endAt(searchText+"\uf8ff")
        var query: Query = com.example.BarterApp.bottomnavibar.Fragment.mDatabase.orderByChild("productType").equalTo(intent.getStringExtra("Type"))
        val options = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
                var status: String? = null
                if (model.ProductState.toString() == "1"|| model.ProductState.toString() == "11") {
                    status = "มีของ"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.selected))
                } else if (model.ProductState.toString() == "2" ) {
                    status = "แลกเปลี่ยน"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.yellowApp1))
                } else if (model.ProductState.toString() == "0") {
                    status = "ไม่มีของ"
                    holder.itemView.state_product.setText(status)
                }

                holder.itemView.name_product.setText(model.ProductName)
                holder.itemView.hand_product.setText("มือ "+model.ProductHand.toString())
                holder.itemView.percent_product.setText(model.ProductQuality.toString())
                holder.itemView.time_product.setText(model.ProductTime)
                holder.itemView.state_product.setText(status)


                Picasso.with(this@ItemList).load(model.ProductImage).into(holder.itemView.img_product)

                holder.itemView.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(v: View?) {

                        var TYPE : String = intent.getStringExtra("Type")!!
                        var email =  intent.getStringExtra("Owner_EMAIL")
                        //   Log.d("Test",intent.getStringExtra("Owner_EMAIL").toString())

                        if (model.USerID == intent.getStringExtra("Owner_EMAIL")) {
                            val intent = Intent(this@ItemList,InforProductUser::class.java)

                            intent.putExtra("Email",email)
                            intent.putExtra("NAME",getRef(position).key)
                            intent.putExtra("PRODUCTID",model.ProductID)
                            intent.putExtra("PDType",TYPE)
                            startActivity(intent)

                        }else{
                            val intent = Intent(this@ItemList,InformationActivity::class.java)
                            intent.putExtra("UserID",model.USerID)
                            intent.putExtra("Owner_EMAIL",email)
                            intent.putExtra("NAME",getRef(position).key)
                            intent.putExtra("PDType",TYPE)

                            startActivity(intent)
                        }

                    }

                })
            }


        }
        mRecyclerView.adapter = FirebaseRecyclerAdapter


    }


    private fun firebaseUserSearch(searchText: String) {
        var query: Query = mDatabase.orderByChild("productName").startAt(searchText).endAt(searchText + "\uf8ff")
        val options = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false))
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
                var status: String? = null
                if (model.ProductState.toString() == "1"|| model.ProductState.toString() == "11") {
                    status = "มีของ"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.selected))
                } else if (model.ProductState.toString() == "2" ) {
                    status = "แลกเปลี่ยน"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.yellowApp1))
                } else if (model.ProductState.toString() == "0") {
                    status = "ไม่มีของ"
                    holder.itemView.state_product.setText(status)
                }

                holder.itemView.name_product.setText(model.ProductName)
                holder.itemView.hand_product.setText("มือ"+model.ProductHand.toString())
                holder.itemView.percent_product.setText(model.ProductQuality.toString())
                holder.itemView.time_product.setText(model.ProductTime)
                holder.itemView.state_product.setText(status)


                Picasso.with(this@ItemList).load(model.ProductImage).into(holder.itemView.img_product)

                holder.itemView.setOnClickListener(object : View.OnClickListener{
                    override fun onClick(v: View?) {
                        var TYPE : String = intent.getStringExtra("Type")!!
                        var email =  intent.getStringExtra("Owner_EMAIL")
                        // Log.d("test",email!!)

                        if (model.USerID == intent.getStringExtra("Owner_EMAIL")) {
                            val intent = Intent(this@ItemList,InforProductUser::class.java)
                            intent.putExtra("Email",email)
                            intent.putExtra("NAME",getRef(position).key)
                            intent.putExtra("PRODUCTID",model.ProductID)
                            intent.putExtra("PDType",TYPE)
                            startActivity(intent)

                        }else{
                            val intent = Intent(this@ItemList,InformationActivity::class.java)
                            intent.putExtra("Owner_EMAIL",email)
                            intent.putExtra("NAME",getRef(position).key)
                            intent.putExtra("PDType",TYPE)

                            startActivity(intent)
                        }


                    }

                })
            }


        }
        mRecyclerView.adapter = FirebaseRecyclerAdapter

    }


    class ProductViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}




