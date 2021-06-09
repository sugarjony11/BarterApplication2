package com.example.BarterApp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.BarterApp.bottomnavibar.Fragment.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() , Communicator {
    var auth: FirebaseAuth? = null
    private lateinit var username : String
    private  val imgRef = Firebase.storage.reference

    private val db = FirebaseFirestore.getInstance()
    lateinit var UID: String

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val home = Home_Fragment()
        val group = Group_Fragment()
        val add = Add_Fragment()
        val notification = Noti_Fragment()
        val user = User_Fragment()

        val bundle = Bundle()


//        Toast.makeText(this, intent.getStringExtra("Email"), Toast.LENGTH_SHORT).show()





        /**************logout*****************/

        auth = FirebaseAuth.getInstance()

        /*************************************/




        /*********************** หน้าโปรไฟล์ ***********************/


        var EMAIL : String
        var uname : String

        if (intent.getStringExtra("Email").toString() == "null"){
            EMAIL = intent.getStringExtra("EmailBack").toString()


        }
        else{
            EMAIL = intent.getStringExtra("Email").toString()
        }

        /**********************UID*****************************/
        db.collection("Member").document(EMAIL).get()
                .addOnSuccessListener {
                    UID = it.data?.get("UID").toString()
//                    Log.d("UID","uid-------- $UID")
                }


        /***************************************************/


        db.collection("Member").document(EMAIL).get()
            .addOnSuccessListener { document ->
                if (document != null){

                    username = "ieie"

                    Log.d("exist", "DocumentSnapshot data: ${document.id}")

                    uname = document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(
                        Member.Surname.db
                    ).toString()
                    bundle.putString("name", uname)

                    uname = document.data?.get(Member.Address.db).toString()
                    bundle.putString("address", uname)

                    uname = document.data?.get(Member.ItemCount.db).toString()
                    bundle.putString("itemcount", uname)

                    uname = document.data?.get(Member.TradingCount.db).toString()
                    bundle.putString("tradecount", uname)

                    uname = document.data?.get(Member.ScorePoint.db).toString()
                    bundle.putString("score", uname)

                    val pic = document.data?.get(Member.ImgProfile.db).toString()
                    Log.d("IMG", pic)

                    val path = EMAIL+"_images/profileImage/"+pic



                    val profile = imgRef.child(path)

                    profile.getBytes(1024 * 1024).addOnSuccessListener {
                        val bitmap: Bitmap
                        bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                        val stream : ByteArrayOutputStream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        val img = stream.toByteArray()
                        bundle.putByteArray("image", img)
                        Log.d("exist", bitmap.toString())

                    }





                    user.arguments = bundle
                    bundle.putString("email", EMAIL)
                    notification.arguments = bundle
                    home.arguments = bundle
                    supportFragmentManager.beginTransaction().apply {

                        commit()
                    }

                }   else {
                    Log.d("exist", "No such document")
                    Toast.makeText(this, "not found", Toast.LENGTH_SHORT).show()

                }
            }.addOnFailureListener { exception ->
                Log.w("exist", "Error getting documents: ", exception)
            }




        db.collection("Member").document(EMAIL).get()
            .addOnSuccessListener { document ->
                val pic = document.data?.get(Member.ImgProfile.db).toString()
                Log.d("IMG2", pic)

                val path = EMAIL+"_images/profileImage/"+pic



                val profile = imgRef.child(path)

                profile.getBytes(1024 * 1024).addOnSuccessListener {
                    val bitmap: Bitmap
                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    val stream : ByteArrayOutputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    val img = stream.toByteArray()
                    bundle.putByteArray("image", img)
                    Log.d("exist", bitmap.toString())

                }
            }


        /*******************************************************/


        /************************ Navigation bar *******************************/
        select(home)

        button_gavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.ic_home -> select(home)
                R.id.ic_group -> select(group)
                R.id.ic_add -> select(add)
                R.id.ic_noti -> select(notification)
                R.id.ic_user -> select(user)


            }
            true
        }


    }

    fun select(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_layout, fragment)
            commit()

        }

    /***************************************************************/


    /****************** Logout *************************/
    fun logout(view: View) {


        auth!!.signOut()
        Toast.makeText(this, "ออกจากระบบสำเร็จ", Toast.LENGTH_LONG).show()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()

    }
    /********************************************/






    /*************************อัพโหลดสินค้า********************/

    fun upload_by_gallery(view: View) {
        var EMAIL: String

        //     Toast.makeText(this, intent.getStringExtra("Email").toString(), Toast.LENGTH_SHORT).show()


        if (intent.getStringExtra("Email").toString() == "null") {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            //   Toast.makeText(this, EMAIL, Toast.LENGTH_SHORT).show()
            db.collection("Member").document(EMAIL).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        //  Toast.makeText(this, document.data?.get("UID").toString(), Toast.LENGTH_SHORT).show()
                        Log.d("exist", "DocumentSnapshot data: ${document.id}")

                        val intent = Intent(this, PostActivity::class.java)
                        intent.putExtra("UID", document.data?.get("UID").toString())
                        intent.putExtra("Email_Path", EMAIL)
                        startActivity(intent)


                    }

                }


        } else {
            EMAIL = intent.getStringExtra("Email").toString()
            //   Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()


            db.collection("Member").document(EMAIL).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        //  Toast.makeText(this, document.data?.get("UID").toString(), Toast.LENGTH_SHORT).show()
                        Log.d("exist", "DocumentSnapshot data: ${document.id}")

                        val intent = Intent(this, PostActivity::class.java)
                        intent.putExtra("UID", document.data?.get("UID").toString())
                        intent.putExtra("Email_Path", EMAIL)
                        startActivity(intent)


                    }

                }
        }
    }
    override fun passData(editText: String) {
        //ทำไว้ก่อนเผื่อได้ใช้
    }
    /************************ แสดงตามหมวดหมู่่ *********************************/
    fun gotoFasionList(view: View){

        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            Log.d("type", EMAIL)
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "แฟชั่น")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            Log.d("type", EMAIL)
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "แฟชั่น")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }


    }
    fun gotoFurList(view: View){


        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "เฟอร์นิเจอร์")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "เฟอร์นิเจอร์")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }

    }
    fun gotoElectList(view: View){



        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "เครื่องใช้ไฟฟ้า")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "เครื่องใช้ไฟฟ้า")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }

    }
    fun gotoTechnoList(view: View){


        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "อุปกรณ์อิเล็กทรอนิกส์")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "อุปกรณ์อิเล็กทรอนิกส์")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }

    }
    fun gotoOfficeMList(view: View){


        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "อุปกรณ์เครื่องเขียน")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "อุปกรณ์เครื่องเขียน")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }


    }
    fun gotoToyList(view: View){



        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "ของเล่น")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ItemList::class.java)
            intent.putExtra("Type", "ของเล่น")
            intent.putExtra("Owner_EMAIL", EMAIL)
            startActivity(intent)
        }
    }
    /***********************************************************/


    /******************** แก้ไขโปรไฟล์ *****************************/
    fun editProfile(view: View){

        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, EditProfileUser::class.java)
            intent.putExtra("EMAIL", EMAIL)
            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, EditProfileUser::class.java)
            intent.putExtra("EMAIL", EMAIL)
            startActivity(intent)
        }

    }
    /************************************************************/


    /******************** ไปหน้าคลังสินค้า *****************************/

    fun stock(view: View) {
        var EMAIL: String
        if (intent.getStringExtra("Email") == null) {

            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, StockActivity::class.java)
            intent.putExtra("pdID", EMAIL)

            startActivity(intent)
        } else {

            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, StockActivity::class.java)
            intent.putExtra("pdID", EMAIL)

            startActivity(intent)
        }
    }
    /************************************************************/








    /******************** ไปหน้าคลังสินค้าที่สนใจ *****************************/
    fun favorite(view: View){

        var EMAIL: String


        if (intent.getStringExtra("Email") == null) {
            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("EMAIL", EMAIL)
            intent.putExtra("UID", UID)


            startActivity(intent)
        } else {
            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, FavoriteActivity::class.java)
            intent.putExtra("EMAIL", EMAIL)
            intent.putExtra("UID", UID)


            startActivity(intent)

                }



    }
    /************************************************************/




    /******************** ไปหน้าคลังFeedback *****************************/
    fun ViewFeedback(view: View){

        var EMAIL: String

        if (intent.getStringExtra("Email") == null) {
            EMAIL = intent.getStringExtra("EmailBack").toString()
            val intent = Intent(this, ViewFeedback()::class.java)
            intent.putExtra("EMAIL", EMAIL)
            intent.putExtra("UID", UID)


            startActivity(intent)
        } else {
            EMAIL = intent.getStringExtra("Email").toString()
            val intent = Intent(this, ViewFeedback()::class.java)
            intent.putExtra("EMAIL", EMAIL)
            intent.putExtra("UID", UID)


            startActivity(intent)
        }



    }
    /************************************************************/


    /******************** dialog ให้เพิ่มสินค้า *****************************/

    fun addProductBtn(){
        val intent = Intent(this, Add_Fragment::class.java)
        startActivity(intent)
    }

    /************************************************************/



}
