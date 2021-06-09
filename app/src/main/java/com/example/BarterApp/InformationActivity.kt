package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_update_product.*
import kotlinx.android.synthetic.main.dialog_addproduct.view.*
import kotlinx.android.synthetic.main.dialog_infor.view.*
import kotlinx.android.synthetic.main.dialog_showstate.view.*


class InformationActivity : AppCompatActivity() {
    lateinit var text: String
    lateinit var dataRef: DatabaseReference
    var firebaseDatabase = Firebase.database.reference
    var nameTv: TextView? = null
    var categoryTv: TextView? = null
    var typeTv: TextView? = null
    var percenTv: TextView? = null
    var timeTv: TextView? = null
    lateinit var favoriteBtn: ImageButton
    private val db = FirebaseFirestore.getInstance()
    private lateinit var userid : String
    private lateinit var prodID : String
    lateinit var Fav:String
    lateinit var UID:String

    /*----ใช้ใน realtime ----*/
    lateinit var imgPd:String
    lateinit var namePd:String
    lateinit var catePd:String
    lateinit var handPd:String
    lateinit var qualityPd:String
    lateinit var timePd:String
    lateinit var detailPd:String
    lateinit var ownerProduct:String
    private lateinit var productStatus :String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_information)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        nameTv = findViewById(R.id.name_product) as TextView
        categoryTv= findViewById(R.id.categoryText) as TextView
        typeTv = findViewById(R.id.typeText) as TextView
        percenTv = findViewById(R.id.perText) as TextView
        timeTv = findViewById(R.id.timeText) as TextView
        favoriteBtn = findViewById(R.id.favBtn)


        dataRef = FirebaseDatabase.getInstance().getReference().child("Product")


//        dataRef = FirebaseDatabase.getInstance().getReference().child("Product").child(intent.getStringExtra("PDType")!!)
        var ProductKey : String

        if (getIntent().getStringExtra("NAME") == null){
            ProductKey = getIntent().getStringExtra("NameBack").toString()
        }else{
            ProductKey = getIntent().getStringExtra("NAME").toString()
        }

        var EMAIL: String
        if (intent.getStringExtra("Owner_EMAIL") == null) {
            EMAIL = intent.getStringExtra("EmailBack").toString()
            Log.d("chon",EMAIL)
        } else {
            EMAIL = intent.getStringExtra("Owner_EMAIL").toString()
            Log.d("chon",EMAIL)
        }

        db.collection("Member").document(EMAIL).get()
                .addOnSuccessListener {
                    UID = it.data?.get("UID").toString()

                }




        //เช็คปุ่ม favorite
        db.collection("Favorite").document("Fav").collection(EMAIL).document(ProductKey).get()
                .addOnSuccessListener { document ->
                    if (document != null) {

                        Fav = document.data?.get("Fav").toString()

                        if (Fav.equals("1")){
                            favBtn.setSelected(true)
                            favBtn.setBackgroundResource(R.drawable.ic_favorite_red)
                        }
                        else{
                            favBtn.setBackgroundResource(R.drawable.ic_favorite)
                        }

                    }

                    else {
                        Log.d("exist", "No such document")
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show()

                    }

                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }




        dataRef.child(ProductKey).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    productStatus = snapshot.child("productState").getValue().toString()
                    var ImgUrl = snapshot.child("productImage").getValue().toString()
                    var productName = snapshot.child("productName").getValue().toString()
                    var productCategory= snapshot.child("productType").getValue().toString()
                    var productHand = snapshot.child("productHand").getValue().toString()
                    var productQuality = snapshot.child("productQuality").getValue().toString()
                    var productTime = snapshot.child("productTime").getValue().toString()
                    var productDetail = snapshot.child("productDetail").getValue().toString()


                    detailText.setText(snapshot.child("productDetail").getValue().toString())

                    Picasso.with(this@InformationActivity).load(ImgUrl).into(product_img)

                    userid  = snapshot.child("userID").getValue().toString()
                    prodID = snapshot.child("productID").getValue().toString()



                    db.collection("Member").document(userid).get()
                            .addOnSuccessListener { document ->
                                if (document != null){

                                    Log.d("exist", "DocumentSnapshot data: ${document.id}")


                                    name_ownerproduct.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString()).toString()
                                    ownerProduct = document.data?.get(Member.ID.db).toString()

                                    Picasso.with(this@InformationActivity).load(document.data?.get("ImageURI").toString()).into(profile_ownerproduct)
                                }   else {
                                    Log.d("exist", "No such document")
                                    Toast.makeText(this@InformationActivity, "not found", Toast.LENGTH_SHORT).show()

                                }
                            }.addOnFailureListener { exception ->
                                Log.w("exist", "Error getting documents: ", exception)
                            }


                    name_product.setText(productName)
                    categoryText.setText("ประเภทสินค้า : $productCategory")
                    typeText.setText("ลักษณะสินค้า : มือ $productHand")
                    perText.setText("สภาพสินค้า : $productQuality ")
                    timeText.setText("ระยะเวลาการใช้งาน : $productTime")

                    /*---ใช้ใน realtime----*/
                    imgPd = ImgUrl
                    namePd = productName
                    catePd = productCategory
                    handPd = productHand
                    qualityPd = productQuality
                    timePd = productTime
                    detailPd = productDetail

                }
                if (productStatus == "2") {
                    offerBtn.setText("สินค้ากำลังแลกเปลี่ยน ")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        ownerproductLayoyt.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                var EMAIL: String
                if (intent.getStringExtra("Owner_EMAIL") == null) {

                    EMAIL = intent.getStringExtra("EmailBack").toString()
                    Log.d("chon",EMAIL)
                    val intent = Intent(this@InformationActivity, OtherProfile::class.java)
                    intent.putExtra("uuid", userid)
                    intent.putExtra("owner_mail", EMAIL)
                    startActivity(intent)

                } else {
                    EMAIL = intent.getStringExtra("Owner_EMAIL").toString()
                    Log.d("chon2",EMAIL)

                    val intent = Intent(this@InformationActivity, OtherProfile::class.java)
                    intent.putExtra("EmailBack",EMAIL)
                    intent.putExtra("uuid", userid)
                    intent.putExtra("owner_mail", EMAIL)
                    startActivity(intent)
                }

            }

        })

        offerBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (intent.getStringExtra("fav")=="1"){
                    favBtn.setSelected(true)
                    favBtn.setBackgroundResource(R.drawable.ic_favorite_red)
                }

                if (productStatus == "2") {
                    offerBtn.setText("สินค้ากำลังแลกเปลี่ยน")
                    Toast.makeText(this@InformationActivity, "สินค้าอยู่ระหว่างการแลกเปลี่ยน", Toast.LENGTH_SHORT).show()
//                    val mDialogView = LayoutInflater.from(this@InformationActivity).inflate(R.layout.dialog_showstate, null)
//                    val mBuilder = AlertDialog.Builder(this@InformationActivity).setView(mDialogView)
//                    val  mAlertDialog = mBuilder.show()
//
//                    mDialogView.OKBtn.setOnClickListener(){
//                        mAlertDialog.dismiss()
//                    }
                } else {
                    var EMAIL: String
                    if (intent.getStringExtra("Owner_EMAIL") == null) {
                        EMAIL = intent.getStringExtra("EmailBack").toString()
                        Log.d("chon", EMAIL)
                    } else {
                        EMAIL = intent.getStringExtra("Owner_EMAIL").toString()
                        Log.d("chon", EMAIL)
                    }
                    db.collection("Member").document(EMAIL!!).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {
                                    if (document.data?.get(Member.ItemCount.db)!!.toString() != "0") {
                                        val intent = Intent(this@InformationActivity, offer_Stock::class.java)
                                        intent.putExtra("form", "OF")
                                        intent.putExtra("EMAIL", EMAIL)
                                        intent.putExtra("NAME", getIntent().getStringExtra("NAME"))
                                        intent.putExtra("UID", document.data?.get("UID")!!.toString())
                                        intent.putExtra("OwnerPost", userid)
                                        intent.putExtra("productPost", prodID)
                                        startActivity(intent)


                                    } else {
                                        //Inflate the dialog with custom view
                                        val mDialogView = LayoutInflater.from(this@InformationActivity).inflate(R.layout.dialog_addproduct, null)
                                        //AlertDialogBuilder
                                        val mBuilder = AlertDialog.Builder(this@InformationActivity).setView(mDialogView)
                                        //show dialog
                                        val mAlertDialog = mBuilder.show()

                                        mDialogView.addProductBtn.setOnClickListener {
                                            val intent = Intent(this@InformationActivity, PostActivity::class.java)
                                            intent.putExtra("Email_Path", EMAIL)
                                            intent.putExtra("UID", document.data?.get("UID")!!.toString())
                                            intent.putExtra("NAME", getIntent().getStringExtra("NAME"))
                                            intent.putExtra("form_offer", "1")
                                            startActivity(intent)
                                            mAlertDialog.dismiss()
                                        }
                                        //cancel button click of custom layout
                                        mDialogView.cancelBtn.setOnClickListener {
                                            //dismiss dialog
                                            mAlertDialog.dismiss()
                                        }
                                    }

                                } else {
                                    Log.d("exist", "No such document")
                                    Toast.makeText(this@InformationActivity, "not found", Toast.LENGTH_SHORT).show()
                                }
                            }.addOnFailureListener { exception ->
                                Log.w("exist", "Error getting documents: ", exception)
                            }

                }
            }
        })




/*-------------------------- ClickFavoriteBtn --------------------------*/

        favoriteBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (favBtn.isSelected()) {
                    favBtn.setSelected(false)
                    favBtn.setBackgroundResource(R.drawable.ic_favorite)
                    db.collection("Favorite").document("Fav").collection(EMAIL).document(ProductKey)
                            .delete()

                    firebaseDatabase.child("Favorite").child(UID).child(ProductKey).removeValue()


                } else {
                    favBtn.setSelected(true)
                    favBtn.setBackgroundResource(R.drawable.ic_favorite_red)
                    fav(UID,ProductKey,intent.getStringExtra("userID")!!,ownerProduct,imgPd,namePd,catePd,handPd,qualityPd,timePd,detailPd)
                  //  val fav = Favorite(UID,EMAIL,ProductKey,imgPd,namePd,handPd,qualityPd,timePd,detailPd,"1")
//                    db.collection("Favorite").document("Fav").collection(EMAIL).document(ProductKey)
//                            .update("Fav", "1")
                }

            }


        })
    }
    private fun fav(uid: String,productKey:String, mail: String,
                    ownerPd: String,imgPd:String,namePd: String,
                    catePd: String, handPd: String,
                    qualityPd: String,timePd: String,
                    detailPd: String){

        /************************** Firestore *************************************/


        val product = hashMapOf(
                "UID" to uid,
                "ProductID" to productKey,
                "UserID" to mail,
                "ownerProduct" to ownerPd,//uid เจ้าของสินค้า
                "ProductImage" to imgPd,
                "ProductName" to namePd,
                "ProductType" to catePd,
                "ProductHand" to handPd,
                "ProductQuality" to qualityPd,
                "ProductTime" to timePd,
                "ProductDetail" to detailPd,
                "ProductState" to "1",
                "Fav" to "1"


        )
        db.collection("Favorite").document("Fav").collection(intent.getStringExtra("Owner_EMAIL").toString()).document(productKey)
                .set(product)
                .addOnSuccessListener {}
                .addOnFailureListener { }

        /***************************   Realtime   ****************************/
        firebaseDatabase.child("Favorite").child(uid).child(productKey).setValue(product)

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }



}