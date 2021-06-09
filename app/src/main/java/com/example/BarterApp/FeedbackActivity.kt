package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.BarterApp.model.Feedback
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var firebaseDatabase = Firebase.database.reference
    lateinit var UID_PD:String
    lateinit var opposite:String //ผู้ใช้ตรงกันข้าม
    lateinit var img : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var owner_mail = intent.getStringExtra("owner_mail")
        var tradID = intent.getStringExtra("tradeID")
        var offerMail = intent.getStringExtra("offerMail")//mailคนอื่น หรือ RQmail
        var postMail = intent.getStringExtra("postMail")//mailเจ้าของ หรือ PDmail


        var itemCountPD = 0
        var itemCountRQ = 0
//        Log.d("owner_mail","id--- $owner_mail")
//        Log.d("TradID","id--- $tradID")
//        Log.d("offMail","id--- $offerMail")
//        Log.d("posMail","id--- $postMail")

        val pdID = intent.getStringArrayExtra("productArray")


        /******************** เปลี่ยนสถานะสินค้าเป็นแลกไปแล้ว *************************/

        val productState : HashMap<String, Any?> = hashMapOf(

                "productState" to "2"

        )
        for (i in 0..4) {
            if (owner_mail.equals(postMail) && pdID!![i] != "") {
                itemCountPD = itemCountPD + 1
                firebaseDatabase.child("Product").child(pdID!![i]).updateChildren(productState).addOnSuccessListener {
                    db.collection("Product").document(owner_mail!!).collection("inventory").document(pdID!![i]).update("ProductState","2")
                    Log.d("Status $i", "Status change")
                }.addOnCanceledListener {
                    Log.d("Status $i", "Status not change")
                }
            }else if (owner_mail.equals(offerMail) && pdID!![i+5] != ""){
                itemCountRQ = itemCountRQ + 1
                firebaseDatabase.child("Product").child(pdID!![i+5]).updateChildren(productState).addOnSuccessListener {
                    db.collection("Product").document(owner_mail!!).collection("inventory").document(pdID!![i+5]).update("ProductState","2")
                    Log.d("Status $i", "Status change")
                }.addOnCanceledListener {
                    Log.d("Status $i", "Status not change")
                }
            }
        }






        if(offerMail.equals(owner_mail)){
            db.collection("Member").document(postMail!!).get() .addOnSuccessListener { document ->
                if (document != null) {
                    RQname.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString())
                    Picasso.with(this).load(document.data?.get("ImageURI").toString()).into(Rqpic_profile)
                    opposite = postMail

                    db.collection("Member").document(opposite!!).get()
                            .addOnSuccessListener {
                                UID_PD = it.data?.get("UID").toString()

                                Log.d("uidopposite $opposite",UID_PD )
                            }
                    db.collection("Member").document(owner_mail!!).get()
                            .addOnSuccessListener {
                                img = it.data?.get("ImageURI").toString()
                            }
                }
            }

        }
        else{
            db.collection("Member").document(offerMail!!).get() .addOnSuccessListener { document ->
                if (document != null) {
                    RQname.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString())
                    Picasso.with(this).load(document.data?.get("ImageURI").toString()).into(Rqpic_profile)
                    opposite = offerMail

                    db.collection("Member").document(opposite!!).get()
                            .addOnSuccessListener {
                                UID_PD = it.data?.get("UID").toString()

                                Log.d("uidopposite $opposite ",UID_PD )
                            }
                    db.collection("Member").document(owner_mail!!).get()
                            .addOnSuccessListener {
                                img = it.data?.get("ImageURI").toString()
                            }
                }
            }
        }



        feedbackBtn.setOnClickListener {

            if (owner_mail.equals(postMail) ) {
                updateStatus(owner_mail!!,"pd")
                removeItem(pdID!![0])
                removeItem(pdID!![1])
                removeItem(pdID!![2])
                removeItem(pdID!![3])
                removeItem(pdID!![4])
                updateItemCount(owner_mail,itemCountPD)
            }else if (owner_mail.equals(offerMail)){
                updateStatus(owner_mail!!,"rq")
                removeItem(pdID!![5])
                removeItem(pdID!![6])
                removeItem(pdID!![7])
                removeItem(pdID!![8])
                removeItem(pdID!![9])
                updateItemCount(owner_mail,itemCountRQ)
            }

            var ratingBar = findViewById(R.id.ratingBar) as RatingBar
            var ratingValue = ratingBar.rating




            var comment = commentText.text.toString()
            if(ratingValue.toInt()==0 ||comment.toString().isEmpty() ){
                if(ratingValue.toInt()==0) {//rating bar = 0
                    Toast.makeText(this, "กรุณาให้คะแนนดาวการแลกเปลี่ยนในครั้งนี้", Toast.LENGTH_LONG).show()
                }
                if(comment.toString().isEmpty() ){//comment is null
                    Toast.makeText(this,"กรุณากรอกความคิดเห็น",Toast.LENGTH_LONG).show()
                }
            }
            else{



                // Toast.makeText(this,"Rating: $value Comment: "+comment.text,Toast.LENGTH_LONG).show()
                Toast.makeText(this,"ประเมินเรียบร้อย",Toast.LENGTH_LONG).show()
                println("ประเมินเรียบร้อย")

                if(offerMail.equals(owner_mail)){
                    firebaseDatabase.child("Feedback").child(UID_PD).child(tradID!!)
                            .setValue(Feedback(owner_mail,tradID,postMail,ratingValue.toInt(),comment.toString(),img))
                }else{
                    firebaseDatabase.child("Feedback").child(UID_PD).child(tradID!!)
                            .setValue(Feedback(owner_mail,tradID,offerMail,ratingValue.toInt(),comment.toString(),img))

                }



                /*******************เก็บคะแนน Score*******************************************/

                Score(opposite,ratingValue.toInt())

                /******************* goto finishfeedback (intent CalculateScore )*******************************************/

                Log.d("Lastmail",owner_mail!!)
                val tocal = Intent(this,CalculateScore::class.java)

                tocal.putExtra("PDproduct",pdID!![0])
                        tocal.putExtra("tradeID",tradID)
                        tocal.putExtra("owner_mail",owner_mail)
                        tocal.putExtra("opposite",opposite)
                        startActivity(tocal)





            }



        }


    }


    /************************** บันทึกคะแนนดาว *************************************/
    private fun Score(RQmail: String,point: Int){

        db.collection("Score").document(RQmail).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        var result: StringBuffer = StringBuffer()
                        result.append(document.data?.get("CountTrad"))
                        db.collection("Score").document(RQmail).update("CountTrad", result.toString().toInt() + 1)
//                        db.collection("Score").document(RQmail).update("ScoreSum", 0.0)

                    } else {
                        Log.d("exist", "No such document")

                    }
                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }

        db.collection("Score").document(RQmail).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        var result: StringBuffer = StringBuffer()
                        //  db.collection("Score").document(opposite).update("ScoreSum",0)
                        if (point==1){
                            result.append(document.data?.get("CountScorePoint1"))
                            db.collection("Score").document(RQmail).update("CountScorePoint1", result.toString().toInt() + 1)
                        }
                        else if(point==2){
                            result.append(document.data?.get("CountScorePoint2"))
                            db.collection("Score").document(RQmail).update("CountScorePoint2", result.toString().toInt() + 1)
                        }
                        else if(point==3){
                            result.append(document.data?.get("CountScorePoint3"))
                            db.collection("Score").document(RQmail).update("CountScorePoint3", result.toString().toInt() + 1)
                        }
                        else if(point==4){
                            result.append(document.data?.get("CountScorePoint4"))
                            db.collection("Score").document(RQmail).update("CountScorePoint4", result.toString().toInt() + 1)
                        }
                        else if(point==5){
                            result.append(document.data?.get("CountScorePoint5"))
                            db.collection("Score").document(RQmail).update("CountScorePoint5", result.toString().toInt() + 1)
                        }
                    } else {
                        Log.d("exist", "No such document")

                    }
                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }



    }



    private fun removeItem(productID : String){
        if (productID != "") {
            firebaseDatabase.child("Product").child(productID).removeValue().addOnSuccessListener {
                Log.d("remove", "Remove success")
            }.addOnFailureListener {
                Log.d("remove", "Item has not remove")
            }
        }
    }

    private fun updateStatus(usermail : String,role : String){

        val userStatus : HashMap<String, Any?> = hashMapOf(

                role+"_Status" to "TR"

        )
        firebaseDatabase.child("Trading").child(intent.getStringExtra("tradeID")!!).updateChildren(userStatus).addOnSuccessListener {

            Log.d("Status $role", "Status change")
        }.addOnCanceledListener {
            Log.d("Status $role", "Status not change")
        }

    }
    private fun updateItemCount(usermail : String,itemCount : Int){

        db.collection("Member").document(usermail).get().addOnSuccessListener {
//           var i = it.data?.get("ItemCount").toString().toInt()-itemCount
            db.collection("Member").document(usermail).update("ItemCount",it.data?.get("ItemCount").toString().toInt()-itemCount )
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}