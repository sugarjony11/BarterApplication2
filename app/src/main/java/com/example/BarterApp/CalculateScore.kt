package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.BarterApp.model.Notification
import com.example.BarterApp.model.Product
import com.example.BarterApp.model.TradingRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_calculate_score.*

class CalculateScore : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var scoreSum:Number = 0.0
    var firebaseDatabase = Firebase.database.reference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculate_score)

        removeTradeRoom(intent.getStringExtra("tradeID")!!)

        removeAlloffer(intent.getStringExtra("PDproduct")!!)
        var owner_mail = intent.getStringExtra("owner_mail")
        var opposite = intent.getStringExtra("opposite")
        Log.d("opposite", opposite!!)

        Log.d("Last",intent.getStringExtra("pdStatus").toString())





            letgoBtn.setOnClickListener {


                val intent = Intent(this,MainActivity::class.java)
                intent.putExtra("Email",owner_mail)



                /*********************คำนวณคะแนน******************************/


                db.collection("Score").document(opposite!!).get()
                    .addOnSuccessListener { document ->
                        var CountTrad = document.data?.get("CountTrad").toString().toInt()
                        var ScorePoint1 = document.data?.get("CountScorePoint1").toString().toInt()
                        var ScorePoint2 = document.data?.get("CountScorePoint2").toString().toInt()
                        var ScorePoint3 = document.data?.get("CountScorePoint3").toString().toInt()
                        var ScorePoint4 = document.data?.get("CountScorePoint4").toString().toInt()
                        var ScorePoint5 = document.data?.get("CountScorePoint5").toString().toInt()

//                        Log.d("CountTrad  ","CountTrad  --- $CountTrad ")
//                        Log.d("ScorePoint1 ","score --- $ScorePoint1")
//                        Log.d("ScorePoint2 ","score --- $ScorePoint2")
//                        Log.d("ScorePoint3 ","score --- $ScorePoint3")
//                        Log.d("ScorePoint4 ","score --- $ScorePoint4")
//                        Log.d("ScorePoint5 ","score --- $ScorePoint5")

                        scoreSum = ((((ScorePoint1*1)+(ScorePoint2*2)+(ScorePoint3*3)+(ScorePoint4*4)+(ScorePoint5*5)).toDouble())/CountTrad)
                        var sum = String.format("%.1f",scoreSum.toDouble())

                        Firebase.database.reference.child("Product").orderByChild("userID").equalTo(opposite).addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for
                                        (pd in snapshot.children){
                                    val upPoit : HashMap<String, Any?> = hashMapOf(

                                            "score" to 0-scoreSum.toDouble()

                                    )
                                    Firebase.database.reference.child("Product").child(pd.getValue(Product::class.java)?.ProductID.toString()).updateChildren(upPoit).addOnSuccessListener {
                                        Log.d("Score","Update Complete")
                                    }

                                        }
                            }


                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })

                        db.collection("Score").document(opposite).update("ScoreSum",sum)
                        db.collection("Member").document(opposite).update("ScorePoint",sum.toDouble())
                        db.collection("Member").document(opposite).update("TradingCount",CountTrad)

                    }

                startActivity(intent)
            }
    }


    private fun removeAlloffer(PDproduct : String){

        Firebase.database.reference.child("Offer").orderByChild("productID").equalTo(PDproduct).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for
                        (pd in snapshot.children){

                    Firebase.database.reference.child("Offer").child(pd.getValue(Notification::class.java)?.OfferID.toString()).removeValue().addOnSuccessListener {
                        Log.d("offer","Remove offer ")
                    }

                }
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun removeTradeRoom(tradID : String){

        firebaseDatabase.child("Trading").child(tradID!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(TradingRoom::class.java)
                Log.d("pdStatus" , product?.PD_Status.toString())

                if ( product?.PD_Status.toString() == "TR" && product?.RQ_Status.toString() == "TR"){
                    firebaseDatabase.child("Trading").child(tradID!!).removeValue().addOnSuccessListener {
                        val offer = intent.getStringExtra("tradeID")!!.split("TR_").toTypedArray()
                        firebaseDatabase.child("Offer").child(offer[1]).removeValue().addOnSuccessListener {
                            Log.d("Last","trade romm has gone")
                        }
                        Log.d("Trade Room Remove","Trade Room has remove")
                    }

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}