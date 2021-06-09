package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_other_profile.*

class OtherProfile : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    lateinit var RQownerID: String //Uid ของผู้ใช้อื่นคนอื่น

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var uuid = intent.getStringExtra("uuid")!!
        Log.d("host",intent.getStringExtra("uuid")!!)
        Log.d("in0",intent.getStringExtra("owner_mail").toString())
        db.collection("Member").document(uuid).get()
                .addOnSuccessListener { document ->
                    if (document != null){

                        Log.d("exist", "DocumentSnapshot data: ${document.id}")


                        Picasso.with(this).load(document.data?.get("ImageURI").toString()).into(pic_profile)

                        nameBar.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString())
                        addressText.setText(document.data?.get(Member.Address.db).toString())
                        count_tread.setText(document.data?.get(Member.TradingCount.db).toString())
                        count_item.setText(document.data?.get(Member.ItemCount.db).toString())
                        count_comment.setText(document.data?.get(Member.ScorePoint.db).toString())

                        RQownerID = document.data?.get(Member.ID.db).toString()



                    }   else {
                        Log.d("exist", "No such document")
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show()

                    }
                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }


        reportBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                val tort = Intent(this@OtherProfile,ReportUser::class.java)

                tort.putExtra("owner",intent.getStringExtra("owner_mail").toString())
                tort.putExtra("pdMail",uuid)
                startActivity(tort)
            }

        })




        //กดคลังสินค้า

        layout_stock.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var EMAIL: String
                if (intent.getStringExtra("owner_mail") == null) {

                    Log.d("in1","Yey")
                    EMAIL = intent.getStringExtra("EmailBack").toString()
                    Log.d("chon", EMAIL)

                    intent.putExtra("other", "1")
                    intent.putExtra("EMAIL", EMAIL)
                    intent.putExtra("pdID", uuid)

                    startActivity(intent)
                } else {
                    EMAIL = intent.getStringExtra("owner_mail").toString()
                    Log.d("in2",intent.getStringExtra("owner_mail").toString())
                    val intent = Intent(this@OtherProfile, StockActivity::class.java)
                    intent.putExtra("other", "1")
                    intent.putExtra("EMAIL", EMAIL)
                    intent.putExtra("pdID", uuid)

                    startActivity(intent)
                }
            }
        })

        stock_other.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var EMAIL: String
                if (intent.getStringExtra("owner_mail") == null) {

                    Log.d("in1","Yey")
                    EMAIL = intent.getStringExtra("EmailBack").toString()
                    Log.d("chon", EMAIL)

                    intent.putExtra("other", "1")
                    intent.putExtra("EMAIL", EMAIL)
                    intent.putExtra("pdID", uuid)

                    startActivity(intent)
                } else {
                    EMAIL = intent.getStringExtra("owner_mail").toString()
                    Log.d("in2",intent.getStringExtra("owner_mail").toString())
                    val intent = Intent(this@OtherProfile, StockActivity::class.java)
                    intent.putExtra("other", "1")
                    intent.putExtra("EMAIL", EMAIL)
                    intent.putExtra("pdID", uuid)

                    startActivity(intent)
                }
            }
        })

        //กดคลังcomment
        layout_comment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@OtherProfile, ViewFeedback::class.java)
                intent.putExtra("EMAIL", uuid)
                intent.putExtra("UID", RQownerID)
                startActivity(intent)
            }
        })

        stock_comment.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@OtherProfile, ViewFeedback::class.java)
                intent.putExtra("EMAIL", uuid)
                intent.putExtra("UID", RQownerID)
//                Log.d("RQownerID","RQownerID------- $RQownerID")
                startActivity(intent)
            }

        })



    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}