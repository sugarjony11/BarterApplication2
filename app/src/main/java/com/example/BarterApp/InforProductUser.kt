package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_infor_productuser.*
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_information.categoryText
import kotlinx.android.synthetic.main.activity_information.detailText
import kotlinx.android.synthetic.main.activity_information.name_ownerproduct
import kotlinx.android.synthetic.main.activity_information.name_product
import kotlinx.android.synthetic.main.activity_information.perText
import kotlinx.android.synthetic.main.activity_information.product_img
import kotlinx.android.synthetic.main.activity_information.profile_ownerproduct
import kotlinx.android.synthetic.main.activity_information.timeText
import kotlinx.android.synthetic.main.activity_information.typeText
import kotlinx.android.synthetic.main.activity_update_product.*

class InforProductUser : AppCompatActivity() {
    lateinit var text: String
    lateinit var dataRef: DatabaseReference
    var nameTv: TextView? = null
    var categoryTv: TextView? = null
    var typeTv: TextView? = null
    var percenTv: TextView? = null
    var timeTv: TextView? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var productStatus :String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infor_productuser)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var img:String? = null
        var name:String? = null
        var category:String? = null
        var hand:String? = null
        var quality:String? = null
        var time:String? = null
        var detail:String? = null


        nameTv = findViewById(R.id.name_product) as TextView
        categoryTv= findViewById(R.id.categoryText) as TextView
        typeTv = findViewById(R.id.typeText) as TextView
        percenTv = findViewById(R.id.perText) as TextView
        timeTv = findViewById(R.id.timeText) as TextView



        dataRef = FirebaseDatabase.getInstance().getReference().child("Product")

        val ProductKey = getIntent().getStringExtra("NAME")

        dataRef.child(ProductKey!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    productStatus = snapshot.child("productState").getValue().toString()
                    var ImgUrl = snapshot.child("productImage").getValue().toString()
                    var productName = snapshot.child("productName").getValue().toString()
                    var productCategory= snapshot.child("productType").getValue().toString()
                    var productHand = snapshot.child("productHand").getValue().toString()
                    var productQuality = snapshot.child("productQuality").getValue().toString()
                    var productTime = snapshot.child("productTime").getValue().toString()

                    var productdetail =snapshot.child("productDetail").getValue().toString()

                    Picasso.with(this@InforProductUser).load(ImgUrl).into(product_img)

                    var uuid = snapshot.child("userID").getValue().toString()


                    db.collection("Member").document(uuid).get()
                            .addOnSuccessListener { document ->
                                if (document != null){

                                    Log.d("exist", "DocumentSnapshot data: ${document.id}")


                                    name_ownerproduct.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString()).toString()
                                    Picasso.with(this@InforProductUser).load(document.data?.get("ImageURI").toString()).into(profile_ownerproduct)
                                }   else {
                                    Log.d("exist", "No such document")
                                    Toast.makeText(this@InforProductUser, "not found", Toast.LENGTH_SHORT).show()

                                }
                            }.addOnFailureListener { exception ->
                                Log.w("exist", "Error getting documents: ", exception)
                            }



                    name_product.setText(productName)
                    categoryText.setText("ประเภทสินค้า : $productCategory")
                    typeText.setText("ลักษณะสินค้า : มือ $productHand")
                    perText.setText("สภาพสินค้า : $productQuality ")
                    timeText.setText("ระยะเวลาการใช้งาน : $productTime")
                    detailText.setText(productdetail)



                    name = productName
                    category=productCategory
                    hand=productHand
                    quality=productQuality
                    time=productTime
                    detail=productdetail
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

/*----------------------------กดแก้ไขสินค้า----------------------------*/

        EditProductBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (productStatus == "2") {
                    Toast.makeText(this@InforProductUser, "สินค้าอยู่ระหว่าการแลกเปลี่ยน ไม่สามารถแก้ไขข้อมูลได้", Toast.LENGTH_LONG).show()
                } else {
                    var EMAIL: String
                    var PRODUCTID: String

                    if (intent.getStringExtra("Email") == null) {
                        EMAIL = intent.getStringExtra("EmailBack").toString()
                        PRODUCTID = intent.getStringExtra("PRODUCTID").toString()
                        val intent = Intent(this@InforProductUser, UpdateProductActivity::class.java)
                        intent.putExtra("EMAIL", EMAIL)
                        intent.putExtra("PRODUCTID", PRODUCTID)
                        startActivity(intent)
                    } else {

                        EMAIL = intent.getStringExtra("Email").toString()
                        PRODUCTID = intent.getStringExtra("PRODUCTID").toString()

                        val intent = Intent(this@InforProductUser, UpdateProductActivity::class.java)
                        intent.putExtra("EMAIL", EMAIL)
                        intent.putExtra("PRODUCTID", PRODUCTID)
                        startActivity(intent)
                    }
                }
            }


        })
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }



}