package com.example.BarterApp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.model.Notification
import com.example.BarterApp.model.Product
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_offer__stock.*
import kotlinx.android.synthetic.main.activity_offer__stock.view.*
import kotlinx.android.synthetic.main.row_offer.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class offer_Stock : AppCompatActivity() {
    lateinit var mRecyclerView: RecyclerView
    lateinit var mDatabase: DatabaseReference
    lateinit var ownerPostMail: String
    lateinit var ownerPostProduct: String
    var firebaseDatabase = Firebase.database.reference
    private var dateFM = SimpleDateFormat("dd/MM/YYYY HH:mm:ss")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offer__stock)


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Product")

        mRecyclerView = findViewById(R.id.recyclerStock)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        if (intent.getStringExtra("form") == "OF") {
            Log.d("form", "Offer")
            showRecyclerView()
        }else if (intent.getStringExtra("form") == "TR") {
            Log.d("form","Trade")

            Log.d("position",intent.getStringExtra("position")!!)
            Log.d("Role",intent.getStringExtra("Role")!!)
            TradeRecyclerView(intent.getStringExtra("EMAIL")!!)
            selected.visibility = View.INVISIBLE
        }


    }


    private fun showRecyclerView(){
        txtDescp.setText("คุณสามารถเลือกสินค้าที่จะเสนอได้สูงสุดเพียง 3 ชิ้น")
        val query =  mDatabase.orderByChild("userID").equalTo(intent.getStringExtra("EMAIL"))
        val firebaseRecycleOption = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).setLifecycleOwner(this).build()
        val ADP = ArrayList<String>()
        ownerPostMail = intent.getStringExtra("OwnerPost").toString()
        ownerPostProduct = intent.getStringExtra("productPost").toString()

        var FirestoreRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(firebaseRecycleOption) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_offer, parent, false))
            }


            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {


                holder.itemView.name_product1.text = model.ProductName
                holder.itemView.hand_product1.text = "มือ " + model.ProductHand.toString()
                holder.itemView.percent_product1.text = model.ProductQuality
                holder.itemView.time_product1.text = model.ProductTime

                Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.itemView.img_product1)

                if (model.ProductState == "2") {
                    holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(applicationContext,R.color.textsoftblack))

                }else{
                    holder.itemView.productCard1.setCardBackgroundColor(Color.WHITE)
                }
                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    @SuppressLint("ResourceAsColor")
//                    override fun onClick(v: View?) {
//                        //         Toast.makeText(this@offer_Stock, ADP.size.toString(), Toast.LENGTH_SHORT).show()
//                        if  (ADP.size == 0) {
//                            holder.itemView.isSelected
//                            ADP.add(model.ProductID.toString()) //add ชิ้นแรก
//                            holder.itemView.productCard1.setCardBackgroundColor(Color.GRAY)
//
//
//                        } else if (ADP.size == 1) {
//                            if (ADP[0] == model.ProductID.toString()) {
//                                ADP.removeAt(0)
//                                holder.itemView.productCard1.setCardBackgroundColor(Color.WHITE)
//
//                            }else{
//                                ADP.add(model.ProductID.toString()) //แอดชิ้นที่สอง
//                                holder.itemView.productCard1.setCardBackgroundColor(Color.GRAY)
//
//                            }
//                        } else if(ADP.size == 2){
//
//                            if (ADP[1] == model.ProductID.toString()) {
//                                ADP.removeAt(1)
//                                holder.itemView.productCard1.setCardBackgroundColor(Color.WHITE)
//
//                            }else{
//                                ADP.add(model.ProductID.toString()) //แอดชิ้นที่สาม
//                                holder.itemView.productCard1.setCardBackgroundColor(Color.GRAY)
//
//                            }
//                        } else if (ADP.size == 3) {
//
//                            if (ADP[2] == model.ProductID.toString()) {
//                                ADP.removeAt(2)
//                                holder.itemView.productCard1.setCardBackgroundColor(Color.WHITE)
//
//                            }else{
//                                Toast.makeText(this@offer_Stock, "เลือกจำนวนสินค้าสูงสุดแล้ว", Toast.LENGTH_SHORT).show()
//                            }
//
//                        }
//
//
//
//
//
//                    }

                    override fun onClick(v: View?) {

                        if (model.ProductState == "2") {

                            Toast.makeText(this@offer_Stock, "สินค้าชิ้นนี้อยู่ระหว่างการแลกเปลี่ยน", Toast.LENGTH_SHORT).show()

                        }else{
                        if (ADP.size == 0) {
                            holder.itemView.isSelected
                            ADP.add(model.ProductID.toString()) //add ชิ้นแรก
                            holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.colorbg))
                            //   holder.itemView.productCard1.setCardBackgroundColor(R.color.purpleApp4)

                        } else if (ADP.size == 1) {
                            if (ADP[0] == model.ProductID.toString()) {
                                ADP.removeAt(0)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else {
                                ADP.add(model.ProductID.toString()) //แอดชิ้นที่สอง
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.colorbg))

                            }
                        } else if (ADP.size == 2) {

                            if (ADP[0] == model.ProductID.toString()) {
                                ADP.removeAt(0)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else if (ADP[1] == model.ProductID.toString()) {
                                ADP.removeAt(1)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else {
                                ADP.add(model.ProductID.toString()) //แอดชิ้นที่สาม
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.colorbg))

                            }
                        } else if (ADP.size == 3) {

                            if (ADP[0] == model.ProductID.toString()) {
                                ADP.removeAt(0)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else if (ADP[1] == model.ProductID.toString()) {
                                ADP.removeAt(1)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else if (ADP[2] == model.ProductID.toString()) {
                                ADP.removeAt(2)
                                holder.itemView.productCard1.setCardBackgroundColor(ContextCompat.getColor(this@offer_Stock, R.color.white))

                            } else {
                                Toast.makeText(this@offer_Stock, "เลือกจำนวนสินค้าสูงสุดแล้ว", Toast.LENGTH_SHORT).show()
                            }

                        }

                    }
                    }

                })

                selected.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        var date = Date()
                        var OfferID = "OF_"+UUID.randomUUID().toString()
                        if (ADP.size == 0) {
                            Toast.makeText(this@offer_Stock, "โปรดเลือกสินค้า", Toast.LENGTH_SHORT).show()
                        } else {
                            if (ADP.size == 1) {
                                val offer = Notification(intent.getStringExtra("productPost").toString(), intent.getStringExtra("OwnerPost").toString(), intent.getStringExtra("EMAIL"), OfferID, "2", ADP[0], "", "",dateFM.format(date))
                                FirebaseDatabase.getInstance().getReference().child("Offer").child(OfferID).setValue(offer)
                            } else if (ADP.size == 2) {
                                val offer = Notification(intent.getStringExtra("productPost").toString(), intent.getStringExtra("OwnerPost").toString(), intent.getStringExtra("EMAIL"), OfferID, "2", ADP[0], ADP[1], "",dateFM.format(date))
                                FirebaseDatabase.getInstance().getReference().child("Offer").child(OfferID).setValue(offer)
                            } else {
                                val offer = Notification(intent.getStringExtra("productPost").toString(), intent.getStringExtra("OwnerPost").toString(), intent.getStringExtra("EMAIL"), OfferID, "2", ADP[0], ADP[1], ADP[2],dateFM.format(date))
                                FirebaseDatabase.getInstance().getReference().child("Offer").child(OfferID).setValue(offer)
                            }
                            Toast.makeText(this@offer_Stock, "เสนอสินค้าเรียบร้อย", Toast.LENGTH_SHORT).show()
                            finish()
                        }

                    }


                })

            }


        }
        mRecyclerView.adapter = FirestoreRecyclerAdapter

    }


    private fun TradeRecyclerView(Email : String){
      txtDescp.setText("เลือกสินค้าที่จะทำการแลกเปลี่ยน")
        val query =  mDatabase.orderByChild("userID").equalTo(Email)
        val firebaseRecycleOption = FirebaseRecyclerOptions.Builder<Product>().setQuery(query, Product::class.java).setLifecycleOwner(this).build()

        ownerPostMail = intent.getStringExtra("OwnerPost").toString()
        ownerPostProduct = intent.getStringExtra("productPost").toString()

        var FirestoreRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(firebaseRecycleOption) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_offer, parent, false))
            }


            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {

                holder.itemView.name_product1.text = model.ProductName
                holder.itemView.hand_product1.text = "มือ " + model.ProductHand.toString()
                holder.itemView.percent_product1.text = model.ProductQuality
                holder.itemView.time_product1.text = model.ProductTime

                Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.itemView.img_product1)



                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    @SuppressLint("ResourceAsColor")
                    override fun onClick(v: View?) {

                        if (model.ProductState == "11" || model.ProductState == "2") {

//                            firebaseDatabase.child("Trading").child(intent.getStringExtra("tradeID")!!)

                            Toast.makeText(this@offer_Stock, "สินค้าชิ้นถูกเลือกไปแล้ว", Toast.LENGTH_SHORT).show()

                        } else {
                            var roleProduct = intent.getStringExtra("Role") + "_Item" + intent.getStringExtra("position")
                            Toast.makeText(this@offer_Stock, "${roleProduct} Yey you click ${model.ProductName}", Toast.LENGTH_SHORT).show()
                            val Role_Item: HashMap<String, Any?> = hashMapOf(
                                    roleProduct to model.ProductID
                            )
                            firebaseDatabase.child("Trading").child(intent.getStringExtra("tradeID")!!).updateChildren(Role_Item).addOnSuccessListener {
                                Log.d("Status", "Status Change!!")
                            }.addOnCanceledListener {
                                Log.d("Status", "Status not Change!!")
                            }
                            val productStatus : HashMap<String, Any?> = hashMapOf(
                                    "productState" to "11"

                            )
                            FirebaseDatabase.getInstance().getReference().child("Product").child(model.ProductID!!).updateChildren(productStatus).addOnSuccessListener {
                                Log.d("change","Status has change")
                                finish()
                            }.addOnCanceledListener {
                                Log.d("change","Status has not change")
                            }

                        }
                    }

                })


            }


        }
        mRecyclerView.adapter = FirestoreRecyclerAdapter

    }


    class ProductViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}