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
import com.example.BarterApp.model.Favorite
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_row.view.*

class FavoriteActivity : AppCompatActivity() {
    private lateinit var dbRef  : DatabaseReference
    lateinit var mRecyclerView: RecyclerView
   // private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mRecyclerView = findViewById(R.id.recyclerFavorite)
        mRecyclerView.layoutManager = LinearLayoutManager(this)


        var EMAIL = intent.getStringExtra("EMAIL")
        Log.d("mail", EMAIL!!)

       var UID = intent.getStringExtra("UID")
        Log.d("UID", UID!!)

        dbRef  = FirebaseDatabase.getInstance().getReference().child("Favorite").child(intent.getStringExtra("UID").toString())

        setupRecycleView()

    }
    fun setupRecycleView(){

       // val query = dbRef.orderByChild("UID").equalTo(uid)


        val firebaseRecycleOption = FirebaseRecyclerOptions.Builder<Favorite>()
                .setQuery(dbRef,Favorite::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirestoreRecyclerAdapter = object : FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder>(firebaseRecycleOption) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

                return FavoriteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false))
            }


            override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int, model: Favorite) {

                FirebaseFirestore.getInstance().collection("Product").document(model.UserID!!).collection("inventory").document(model.ProductID!!).get().addOnSuccessListener{

                    if (it.data?.get("ProductState") == "0" ){
                        Log.d("remove","Product has been remove")
                        dbRef.child(model.ProductID!!).removeValue().addOnSuccessListener {

                        }
                    }else{



                        if (model.ProductState.toString() == "1")
                            holder.itemView.state_product.text = "มีของ"
                        else if (model.ProductState.toString() == "2") {
                            holder.itemView.state_product.text = "แลกเปลี่ยน"
                        } else if (model.ProductState.toString() == "0") {
                            holder.itemView.state_product.text = "ไม่มีของ"
                        }

                        holder.itemView.name_product.text = model.ProductName
                        holder.itemView.hand_product.text = "มือ " + model.ProductHand.toString()
                        holder.itemView.percent_product.text = model.ProductQuality
                        holder.itemView.time_product.text = model.ProductTime

                        Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.itemView.img_product)

                        holder.itemView.setOnClickListener(object : View.OnClickListener {
                            override fun onClick(v: View?) {

                                val intent = Intent(this@FavoriteActivity, InformationActivity::class.java)
                                intent.putExtra("fav","1")
                                intent.putExtra("NAME", getRef(position).key)
                                intent.putExtra("Owner_EMAIL",model.UserID)

                                startActivity(intent)
                            }

                        })




                    }

                }


            }
        }
        mRecyclerView.adapter = FirestoreRecyclerAdapter



    }
    class FavoriteViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}