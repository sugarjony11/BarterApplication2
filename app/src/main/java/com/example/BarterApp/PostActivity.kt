package com.example.BarterApp

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.BarterApp.model.Product
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_regis.*
import java.util.*
import kotlin.properties.Delegates

class PostActivity : AppCompatActivity() {
    var selected: Uri? = null
    var firebaseDatabase = Firebase.database.reference
    var myRef: DatabaseReference? = null
    var mStorageRef: StorageReference? = null
    val storageRef = Firebase.storage.reference
    private lateinit var product_Type: String
    lateinit var imageName : String
    private var radioCheck by Delegates.notNull<Int>()
    lateinit var imageURI : String
    lateinit var tester : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//          Toast.makeText(this, intent.getStringExtra("Email_Path").toString(), Toast.LENGTH_SHORT).show()




        upload_button.setOnClickListener {
            upload()
        }


        val groupNames = arrayOf("เลือกประเภทสินค้า","แฟชั่น", "เฟอร์นิเจอร์", "เครื่องใช้ไฟฟ้า", "อุปกรณ์อิเล็กทรอนิกส์", "อุปกรณ์เครื่องเขียน", "ของเล่น")
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, groupNames)
        category_spinner.adapter = arrayAdapter
        category_spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                product_Type = groupNames[position]
                //          Toast.makeText(this@PostActivity,groupNames[position],Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }


    fun upload() {
        if (selected != null) { //เช็คถ้ารูปใส่แล้ว
            if (product_name.text.trim().isEmpty() || (firsthand.isChecked == false && secondhand.isChecked == false) || product_quality.text.trim().isEmpty() || product_time.text.trim().isEmpty() || (product_Type == "เลือกประเภทสินค้า")) {
                if (product_name.text.trim().isEmpty()) {
                    product_name.error = "กรุณาระบุชื่อสินค้า"
                }
                if (firsthand.isChecked == false && secondhand.isChecked == false) {
                    Toast.makeText(this@PostActivity, "กรุณาเลือกลักษณะสินค้า", Toast.LENGTH_SHORT).show()
                }
                if (product_quality.text.trim().isEmpty()) {
                    product_quality.error = "กรุณาระบุคุณภาพสินค้า"
                }
                if (product_time.text.trim().isEmpty()) {

                    product_time.error = "กรุณาระบุระยะเวลาสินค้า"
                }
                if (product_Type == "เลือกประเภทสินค้า") {
                    Toast.makeText(this,  "กรุณาเลือกประเภทสินค้า", Toast.LENGTH_SHORT).show()
                }
            } else {

                var progreess = ProgressDialog(this@PostActivity)
                progreess.setTitle("กำลังอัพโหลด")
                progreess.show()

                val EMAIL: String
                val uuid: String = UUID.randomUUID().toString()

                    EMAIL = intent.getStringExtra("Email_Path")!!


                imageName = "$uuid.jpg"
                val pathFile: String = EMAIL + "_images/stock/" + imageName

                var imageRef = FirebaseStorage.getInstance().reference.child(pathFile)
                imageRef.putFile(selected!!).addOnSuccessListener {

                    FirebaseFirestore.getInstance().collection("Member").document(EMAIL).get()
                            .addOnSuccessListener { document ->

                                photoDetailUpload(imageName, EMAIL!!, document.data?.get("ScorePoint").toString().toDouble())
                                progreess.dismiss()
                            }
                        Toast.makeText(this@PostActivity, "อัพโหลดเสร็จสิ้น", Toast.LENGTH_SHORT).show()
//
//                    if(intent.getStringExtra("form_offer").equals("1")){
//                        val intent = Intent(this, InformationActivity::class.java)
//                        intent.putExtra("EmailBack", EMAIL)
//                        intent.putExtra("NameBack", getIntent().getStringExtra("NAME"))
//                        startActivity(intent)
//                    }else {

                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("EmailBack", EMAIL)
                        startActivity(intent)
//                    }
                }.addOnFailureListener { p0 ->
                    progreess.dismiss()
                    Toast.makeText(this@PostActivity, p0.message, Toast.LENGTH_LONG).show()
                }.addOnProgressListener { p0 ->
                    var pgs: Double = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                    progreess.setMessage("อัพโหลด : ${pgs.toInt()}%")

                }

            }


        } //เช็คถ้ารูปใส่แล้ว
        else
            if (selected == null) { //เช็คถ้ารูปใม่ได้ใส่
                Toast.makeText(this@PostActivity, "กรุณาเพิ่มรูปภาพ", Toast.LENGTH_SHORT).show()
            }
        if (product_name.text.trim().isEmpty() || (firsthand.isChecked == false && secondhand.isChecked == false) || product_quality.text.trim().isEmpty() || product_time.text.trim().isEmpty() || (product_Type == "เลือกประเภทสินค้า")) {
            if (product_name.text.trim().isEmpty()) {
                product_name.error = "กรุณาระบุชื่อสินค้า"
            }
            if (firsthand.isChecked == false && secondhand.isChecked == false) {
                Toast.makeText(this@PostActivity, "กรุณาเลือกลักษณะสินค้า", Toast.LENGTH_LONG).show()
            }
            if (product_quality.text.trim().isEmpty()) {
                product_quality.error = "กรุณาระบุคุณภาพสินค้า"
            }
            if (product_time.text.trim().isEmpty()) {

                product_time.error = "กรุณาระบุระยะเวลาสินค้า"
            }
            if (product_Type == "เลือกประเภทสินค้า") {
                Toast.makeText(this,  "กรุณาเลือกประเภทสินค้า", Toast.LENGTH_SHORT).show()
            }
        } //เช็คถ้ารูปใม่ได้ใส่
    }


    fun selectImage(view: View) {
        if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 2)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //    val imagev : ImageView = findViewById(R.id.imageView)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selected = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selected)
                imageView.setImageBitmap(bitmap)


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun onRadioButtonClick(view: View) {
        if (view is RadioButton) {
            val check = view.isChecked

            when (view.getId()) {
                R.id.firsthand ->
                    if (check) {
                        radioCheck = 1

                    }
                R.id.secondhand ->
                    if (check) {
                        radioCheck = 2
                    }
            }
        }
    }

    private fun photoDetailUpload(imageName: String, EMAIL: String,score : Double) {


        var imgDB =  storageRef.child(EMAIL!!+"_images/stock/"+imageName)

        Log.d("in","ready")


        /************************** Firestore *************************************/


        val db = FirebaseFirestore.getInstance()

       var uid = intent.getStringExtra("UID")!!

        val product : HashMap<String, Any?> = hashMapOf(

                "ProductDetail" to product_detail.text.toString(),
                "ProductImage" to imageName,
                "ProductName" to product_name.text.toString(),
                "ProductQuality" to product_quality.text.toString(),
                "ProductState" to "1",
                "ProductHand" to radioCheck,
                "ProductTime" to product_time.text.toString(),
                "ProductType" to product_Type,
                "UserID" to uid

                )

        db.collection("Product").document(EMAIL!!).collection("inventory")
                .add(product)
                .addOnSuccessListener {}
                .addOnFailureListener { }


        db.collection("Member").document(EMAIL!!).get()
                .addOnSuccessListener { document ->

                    if (document != null) {
                        var result: StringBuffer = StringBuffer()

                        Log.d("exist", "DocumentSnapshot data: ${document.data}")
                        result.append(document.data?.get("ItemCount"))
                     
                        db.collection("Member").document(EMAIL).update("ItemCount", result.toString().toInt() + 1)
                                .addOnSuccessListener { Log.d("exist", "DocumentSnapshot successfully updated!") }
                                .addOnFailureListener { e -> Log.w("exist", "Error updating document", e) }

                    } else {
                        Log.d("exist", "No such document")

                    }
                }.addOnFailureListener { exception ->
                    Log.w("exist", "Error getting documents: ", exception)
                }
        /***************************   Realtime   ****************************/

val dbase = db.collection("Product").document(EMAIL!!).collection("inventory")

        dbase.whereEqualTo("ProductImage", imageName).get().addOnSuccessListener { doc ->
            for (document in doc) {


                imgDB.downloadUrl.addOnSuccessListener { uri ->
                    uri.toString()
                    dbase.document(document.id).update("ProductImage", uri.toString())

                    Log.d("chonyun", uri.toString())
                    val productRDB = Product(document.id, EMAIL, product_name.text.toString(), product_Type, "1", product_detail.text.toString(), uri.toString(), product_quality.text.toString() + "%", radioCheck, product_time.text.toString(),0-score)
                    firebaseDatabase.child("Product").child(document.id).setValue(productRDB)


                }





             //   firebaseDatabase.child("Product").child(product_Type).child(document.id).setValue(productRDB)
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }


}


