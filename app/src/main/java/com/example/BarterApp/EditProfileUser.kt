package com.example.BarterApp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_information.*
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_regis.*
import java.util.*


class EditProfileUser : AppCompatActivity() {
    val TAG:String = "TAG"
    var selected: Uri? = null
    lateinit var nameProfile:EditText
    lateinit var surnameProfile:EditText
    lateinit var addressProfile:EditText
    lateinit var imgProfile:CircleImageView
    lateinit var changeBtn:CircularProgressButton
    val storageRef = Firebase.storage.reference
    private val db = FirebaseFirestore.getInstance()
    lateinit var imageName : String
   lateinit var mail:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profileuser)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var EMAIL = intent.getStringExtra("EMAIL")
        mail = EMAIL.toString()

        imgProfile = findViewById(R.id.pic_profile)
        nameProfile = findViewById(R.id.nameText)
        surnameProfile = findViewById(R.id.surnameText)
        addressProfile = findViewById(R.id.addressText)
        changeBtn = findViewById(R.id.EditProfileBtn)



        db.collection("Member").document(EMAIL!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {

                    Log.d("exist", "DocumentSnapshot data: ${document.id}")


                    nameProfile.setText(document.data?.get(Member.Name.db).toString())
                    surnameProfile.setText( document.data?.get(Member.Surname.db).toString())
                    addressProfile.setText( document.data?.get(Member.Address.db).toString())



                    val pic = document.data?.get("ImageURI").toString()
                    Log.d("exist", pic)
                    Picasso.with(this).load(pic).into(imgProfile)

                }

                else {
                    Log.d("exist", "No such document")
                    Toast.makeText(this@EditProfileUser, "not found", Toast.LENGTH_SHORT).show()

                }

            }.addOnFailureListener { exception ->
                Log.w("exist", "Error getting documents: ", exception)
            }

        val profileRef = storageRef.child("profileImage")
        profileRef.downloadUrl.addOnSuccessListener (OnSuccessListener {
            Picasso.with(this).load(it).into(imgProfile)
        })



        changeBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (nameProfile.text.toString().isEmpty() || surnameProfile.text.toString()
                        .isEmpty()
                    || addressProfile.text.toString().isEmpty()
                ) {
                    if (emailText.text.toString().trim().isEmpty()) {
                        emailText.error = "กรุณาป้อนอีเมล"
                        return
                    }
                    if (passwordText.text.toString().trim().isEmpty()) {
                        passwordText.error = "กรุณาป้อนรหัสผ่าน"
                        return
                    }
                    if (nameText.text.toString().trim().isEmpty()) {
                        nameText.error = "กรุณาป้อนชื่อ"
                        return
                    }
                    if (surnameText.text.toString().trim().isEmpty()) {
                        surnameText.error = "กรุณาป้อนนามสกุล"
                        return
                    }
                    if (addressText.text.toString().trim().isEmpty()) {
                        addressText.error = "กรุณาป้อนที่อยู่"
                        return
                    }
                } else {
                    db.collection("Member").document(EMAIL!!)
                        .update("Name", nameProfile.text.toString())
                    db.collection("Member").document(EMAIL!!)
                        .update("Surname", surnameProfile.text.toString())
                    db.collection("Member").document(EMAIL!!)
                        .update("Address", addressProfile.text.toString())
                    //  Toast.makeText(this@EditProfileUser, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()

                    if (selected == null){

                        Toast.makeText(this@EditProfileUser, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@EditProfileUser, MainActivity::class.java)
                        intent.putExtra("EmailBack", EMAIL)
                        startActivity(intent)
                        finish()

                    } else {

                        val uuid: String = UUID.randomUUID().toString()

                        imageName = "$uuid.jpg"
                        val pathFile: String = EMAIL + "_images/profileImage/" + imageName

                        var imageRef = FirebaseStorage.getInstance().reference.child(pathFile)
                        imageRef.putFile(selected!!).addOnSuccessListener {
                            db.collection("Member").document(EMAIL!!)
                                .update(Member.ImgProfile.db, imageName)
                            var imgDB = storageRef.child(pathFile)

                            imgDB.downloadUrl.addOnSuccessListener { uri ->
                                db.collection("Member").document(EMAIL!!)
                                    .update("ImageURI", uri.toString())

                            }
                        }.addOnFailureListener { exception ->
                            if (exception != null) {
                                Toast.makeText(
                                    applicationContext,
                                    exception.localizedMessage,
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }


                        Toast.makeText(this@EditProfileUser, "แก้ไขข้อมูลสำเร็จ", Toast.LENGTH_SHORT)
                            .show()

                        val intent = Intent(this@EditProfileUser, MainActivity::class.java)
                        intent.putExtra("EmailBack", EMAIL)
                        startActivity(intent)
                        finish()
                    }


                }

            }

        })





        /*---------------เลือกรูปภาพ-------------------*/
        imgProfile.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
                } else {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, 2)
                }
            }

        })



    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 2)
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            selected = data.data

            try {

                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selected)
                imgProfile.setImageBitmap(bitmap)


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    fun addData(imageURI:String) {


        val user = hashMapOf(
                "ImgProfile" to imageName,
                "ImageURI" to imageURI
        )

        db.collection("Member").document(mail).update(user as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "แก้ไขสำเร็จ", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "แก้ไขไม่สำเร็จ", Toast.LENGTH_SHORT).show()
                }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}