package com.example.BarterApp

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_regis.*
import java.util.*


class RegisActivity : AppCompatActivity() {
    var selected: Uri? = null
    var mAuth: FirebaseAuth? = null
    var mAuthListener: FirebaseAuth.AuthStateListener? = null
    private val db = FirebaseFirestore.getInstance()
    val storageRef = Firebase.storage.reference

    lateinit var imageName : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_regis)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        //    val mStorage: StorageReference = FirebaseStorage.getInstance().getReference().child("user_proto")

        //กดเลือกรูปโปรไฟล์
        pic_profile.setOnClickListener {
            selectImage()

        }

        backLogin.setOnClickListener {
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)

        }

        mAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener { }


        RegisterBtn.setOnClickListener {
            if(emailText.text.trim().isEmpty()|| passwordText.text.trim().isEmpty()
                    || nameText.text.trim().isEmpty() || surnameText.text.trim().isEmpty() || addressText.text.trim().isEmpty()){
                if (emailText.text.toString().trim().isEmpty()) {
                    emailText.error = "กรุณาป้อนอีเมล"
                    return@setOnClickListener
                }
                if (passwordText.text.toString().trim().isEmpty()) {
                    passwordText.error = "กรุณาป้อนรหัสผ่าน"
                    return@setOnClickListener
                }
                if (nameText.text.toString().trim().isEmpty()) {
                    nameText.error = "กรุณาป้อนชื่อ"
                    return@setOnClickListener
                }
                if (surnameText.text.toString().trim().isEmpty()) {
                    surnameText.error = "กรุณาป้อนนามสกุล"
                    return@setOnClickListener
                }
                if (addressText.text.toString().trim().isEmpty()) {
                    addressText.error = "กรุณาป้อนที่อยู่"
                    return@setOnClickListener
                }
            }
            else {

                if (selected == null) {
                    Toast.makeText(this, "กรุณาเลือกรูปภาพโปรไฟล์", Toast.LENGTH_SHORT).show()
                } else{
                    mAuth!!.createUserWithEmailAndPassword(emailText.text.toString(), passwordText.text.toString())
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    var progreess = ProgressDialog(this)
                                    progreess.setTitle("กำลังสร้างบัญชี")
                                    progreess.show()

                                    val uuid: String = UUID.randomUUID().toString()


                                    imageName = "$uuid.jpg"

                                    val pathFile: String = emailText.text.toString() + "_images/profileImage/" + imageName


                                    var imageRef = FirebaseStorage.getInstance().reference.child(pathFile)



                                    imageRef.putFile(selected!!).addOnSuccessListener {


                                        var imgDB = storageRef.child(pathFile)

                                        imgDB.downloadUrl.addOnSuccessListener { uri ->
                                            addData(uri.toString(), emailText.text.toString(), passwordText.text.toString())
                                            Score(emailText.text.toString())

                                        }

                                        progreess.dismiss()
                                        Toast.makeText(this, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_LONG).show()
                                        val intent = Intent(applicationContext, SignInActivity::class.java)
                                        startActivity(intent)

                                    }.addOnFailureListener { p0 ->
                                        progreess.dismiss()
                                        Toast.makeText(this, p0.message, Toast.LENGTH_LONG).show()
                                    }.addOnProgressListener { p0 ->
                                        var pgs: Double = (100.0 * p0.bytesTransferred) / p0.totalByteCount
                                        progreess.setMessage("กำลังสร้างบัญชี : ${pgs.toInt()}%")

                                    }


                                }
                            }.addOnFailureListener { exception ->
                                if (exception != null) {
                                    Toast.makeText(applicationContext, exception.localizedMessage, Toast.LENGTH_LONG).show()
                                }
                            }


            }

            }


        }




    }

     fun Score(Email: String){

        /************************** เก็บคะแนนดาว Firestore *************************************/

        val Score = hashMapOf(
                "CountTrad" to 0,
                "CountScorePoint1" to 0,
                "CountScorePoint2" to 0,
                "CountScorePoint3" to 0,
                "CountScorePoint4" to 0,
                "CountScorePoint5" to 0,
                "ScoreSum" to 0

        )
        db.collection("Score").document(Email)
                .set(Score)
                .addOnSuccessListener {}
                .addOnFailureListener { }

    }


    fun addData(imageURI:String , Email: String, Password: String) {

       var uid = UUID.randomUUID().toString()



        val user = hashMapOf(
                "ImgProfile" to imageName,
                "Name" to nameText.text.toString(),
                "Surname" to surnameText.text.toString().trim(),
                "Address" to addressText.text.toString().trim(),
                "Password" to Password,
                "ScorePoint" to 0.0,
                "TradingCount" to 0,
                "ItemCount" to 0,
                "UID" to uid,
                "ImageURI" to imageURI

        )

        db.collection("Member").document(Email).set(user)
                .addOnSuccessListener {
                    Toast.makeText(this@RegisActivity, "สมัครสมาชิกสำเร็จ", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this@RegisActivity, "สมัครสมาชิกไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
    }


    fun selectImage() {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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

        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            selected = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selected)
                pic_profile.setImageBitmap(bitmap)


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}