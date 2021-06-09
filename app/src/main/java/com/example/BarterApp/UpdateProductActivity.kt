package com.example.BarterApp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_update_product.*
import kotlinx.android.synthetic.main.activity_update_product.category_spinner
import kotlinx.android.synthetic.main.activity_update_product.firsthand
import kotlinx.android.synthetic.main.activity_update_product.product_name
import kotlinx.android.synthetic.main.activity_update_product.product_quality
import kotlinx.android.synthetic.main.activity_update_product.product_time
import kotlinx.android.synthetic.main.activity_update_product.secondhand
import kotlinx.android.synthetic.main.dialog_deleteproduct.*
import kotlinx.android.synthetic.main.dialog_deleteproduct.view.*
import kotlin.properties.Delegates

class UpdateProductActivity : AppCompatActivity() {
    lateinit var mail:String
    lateinit var productid:String
    lateinit var productID:String
    lateinit var imgProduct:ImageView
    lateinit var nameProduct: EditText
    lateinit var categoryProduct: Spinner
    lateinit var handProduct: RadioGroup
    lateinit var qualityProduct:EditText
    lateinit var timeProduct:EditText
    lateinit var detailProduct: EditText
    lateinit var updateBtn: CircularProgressButton
    private val db = FirebaseFirestore.getInstance()
    var firebaseDatabase = Firebase.database.reference
    lateinit var hand: String
    lateinit var cate: String


    private var radioCheck by Delegates.notNull<Int>()
    private lateinit var product_Type: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_product)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var EMAIL = intent.getStringExtra("EMAIL")
        mail = EMAIL.toString()
        var PRODUCTID = intent.getStringExtra("PRODUCTID")
        productID = PRODUCTID.toString()
        productid = PRODUCTID.toString()


        imgProduct = findViewById(R.id.imageView)
        nameProduct = findViewById(R.id.product_name)
        categoryProduct = findViewById(R.id.category_spinner)
        handProduct = findViewById(R.id.hand_radioBtn)
        qualityProduct = findViewById(R.id.product_quality)
        timeProduct = findViewById(R.id.product_time)
        detailProduct = findViewById(R.id.product_detail)
        updateBtn = findViewById(R.id.updateBtn)

//        Log.d("Email", "mail: $EMAIL")
//        Log.d("Product", "product: $PRODUCTID")

        val groupNames = arrayOf("แฟชั่น", "เฟอร์นิเจอร์", "เครื่องใช้ไฟฟ้า", "อุปกรณ์อิเล็กทรอนิกส์", "อุปกรณ์เครื่องเขียน", "ของเล่น")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        category_spinner.adapter = arrayAdapter


        categoryProduct.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                product_Type = groupNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }




        db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
//                    Log.d("exist", "DocumentSnapshot data: ${document.data}")
                        val pic = document.data?.get("ProductImage").toString()
                        Log.d("exist", pic)
                        Picasso.with(this).load(pic).into(imgProduct)
                        nameProduct.setText(document.data?.get("ProductName").toString())
                        qualityProduct.setText(document.data?.get("ProductQuality").toString())
                        timeProduct.setText(document.data?.get("ProductTime").toString())
                        detailProduct.setText(document.data?.get("ProductDetail").toString())

                        hand = document.data?.get("ProductHand").toString()

                        if (hand.equals("1")){
                            handProduct.check(firsthand.id)
                            radioCheck = 1
                        }
                        else{
                            handProduct.check(secondhand.id)
                            radioCheck = 2
                        }

                        cate = document.data?.get("ProductType").toString()
                        if (cate.equals("แฟชั่น")){
                            categoryProduct.setSelection(0)
                        }
                        else if (cate.equals("เฟอร์นิเจอร์")){
                            categoryProduct.setSelection(1)
                        }
                        else if (cate.equals("เครื่องใช้ไฟฟ้า")){
                            categoryProduct.setSelection(2)
                        }
                        else if (cate.equals("อุปกรณ์อิเล็กทรอนิกส์")){
                            categoryProduct.setSelection(3)
                        }
                        else if (cate.equals("อุปกรณ์เครื่องเขียน")){
                            categoryProduct.setSelection(4)
                        }
                        else if (cate.equals("ของเล่น")){
                            categoryProduct.setSelection(5)
                        }




                    }

                    else {
                        Log.d("exist", "No such document")
                        Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show()

                    }

                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }


        deleteProductBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val activity = this@UpdateProductActivity as Activity
                if (!activity.isFinishing()) {
                val mDialogView = LayoutInflater.from(this@UpdateProductActivity).inflate(R.layout.dialog_deleteproduct, null)
                val mBuilder = AlertDialog.Builder(this@UpdateProductActivity).setView(mDialogView)
                val mAlertDialog = mBuilder.show()

                mAlertDialog.deleteBtn.setOnClickListener {
                    //เซตใน Firestore
                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductState","0") // เซตให้ไม่มีของ

                    //เซตค่าสินค้าในคลัง Firestore

                    db.collection("Member").document(EMAIL!!).get()
                            .addOnSuccessListener { document ->

                                if (document != null) {
                                    var result: StringBuffer = StringBuffer()
//                                    Log.d("exist", "DocumentSnapshot data: ${document.data}")
                                    result.append(document.data?.get("ItemCount"))

                                    db.collection("Member").document(EMAIL).update("ItemCount", result.toString().toInt() - 1)
                                            .addOnSuccessListener { Log.d("exist", "DocumentSnapshot successfully updated!") }
                                            .addOnFailureListener { e -> Log.w("exist", "Error updating document", e) }

                                } else {
                                    Log.d("exist", "No such document")

                                }
                            }.addOnFailureListener { exception ->
                                Log.w("exist", "Error getting documents: ", exception)
                            }


                    //ลบออกจาก realtime
                    firebaseDatabase.child("Product").child(PRODUCTID!!).removeValue().addOnSuccessListener {


                        Toast.makeText(this@UpdateProductActivity, "ลบสืนค้าสำเร็จ", Toast.LENGTH_SHORT).show()
                        finish()
                        val intent = Intent(this@UpdateProductActivity, MainActivity::class.java)
                        intent.putExtra("Email", EMAIL)
                        startActivity(intent)

                    }.addOnCanceledListener {
                        Toast.makeText(this@UpdateProductActivity, "ลบสืนค้าไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                    }



                    /*     val intent = Intent(this@InformationActivity, PostActivity::class.java)
                         intent.putExtra("Email_Path", EMAIL)
                         intent.putExtra("UID", document.data?.get("UID")!!.toString())
                         intent.putExtra("NAME",getIntent().getStringExtra("NAME"))
                         intent.putExtra("form_offer", "1")
                         startActivity(intent)
                         mAlertDialog.dismiss()*/



                }

                mDialogView.close_dialog.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
}
        })

        updateBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if (product_name.text.trim().isEmpty() || (firsthand.isChecked == false && secondhand.isChecked == false)
                        || product_quality.text.trim().isEmpty() || product_time.text.trim().isEmpty()) {
                    if (nameProduct.text.trim().isEmpty()) {
                        nameProduct.error = "กรุณาระบุชื่อสินค้า"
                    }
                    if (firsthand.isChecked == false && secondhand.isChecked == false) {
                        Toast.makeText(this@UpdateProductActivity, "กรุณาเลือกลักษณะสินค้า", Toast.LENGTH_LONG).show()
                    }
                    if (qualityProduct.text.trim().isEmpty()) {
                        qualityProduct.error = "กรุณาระบุคุณภาพสินค้า"
                    }
                    if (timeProduct.text.trim().isEmpty()) {
                        timeProduct.error = "กรุณาระบุระยะเวลาสินค้า"
                    }
                    if (detailProduct.text.trim().isEmpty()) {
                        detailProduct.error = "กรุณาระบุระยะเวลาสินค้า"
                    }
                }
                else{//ป้อนข้อมูลครบถ้วน
                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductName",nameProduct.text.toString())
                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductQuality",qualityProduct.text.toString())
                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductTime",timeProduct.text.toString())
                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductDetail",detailProduct.text.toString())

                    db.collection("Product").document(mail).collection("inventory").document(productid)
                            .update("ProductHand",radioCheck)

                    db.collection("Product").document(EMAIL!!).collection("inventory").document(PRODUCTID!!)
                            .update("ProductType",product_Type)


                    val product : HashMap<String, Any?> = hashMapOf(

                            "productName" to nameProduct.text.toString(),
                            "productType" to product_Type,
                            "productHand" to radioCheck,
                            "productQuality" to qualityProduct.text.toString()+" %",
                            "productTime" to timeProduct.text.toString(),
                            "productDetail" to detailProduct.text.toString(),

                            )
                    Log.d("Itim",PRODUCTID)

                    firebaseDatabase.child("Product").child(PRODUCTID!!).updateChildren(product).addOnSuccessListener {
                        Toast.makeText(this@UpdateProductActivity, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        finish()
                    }.addOnCanceledListener {
                        Toast.makeText(this@UpdateProductActivity, "ข้อมูลหลง", Toast.LENGTH_SHORT).show()
                    }



                }
            }

        })


    }


    fun onRadioBtnClick(view: View) {
        if (view is RadioButton) {
            val check = view.isChecked

            when (view.getId()) {
                R.id.firsthand ->
                    if (check) {
                        radioCheck = 1
//                        Toast.makeText(this,"1",Toast.LENGTH_LONG).show()


                    }
                R.id.secondhand ->
                    if (check) {
                        radioCheck = 2
//                        Toast.makeText(this,"2",Toast.LENGTH_LONG).show()33
                    }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}