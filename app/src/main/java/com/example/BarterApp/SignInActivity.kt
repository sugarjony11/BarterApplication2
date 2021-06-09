package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_regis.*

class SignInActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance()

        LoginButton.setOnClickListener {
            login()
        }
        RegisButton.setOnClickListener {
            register()
        }


    }

    fun register() {

        val intent = Intent(applicationContext, RegisActivity::class.java)
        startActivity(intent)

    }


    fun login() {
        var uname: String

        if (emailLogin.text.toString().isEmpty()) {
            emailLogin.error = "โปรดใส่อีเมล"
            emailLogin.requestFocus()
            return
        }
        if (passwordLogin.text.toString().isEmpty()) {
            passwordLogin.error = "โปรดใส่รหัสผ่าน"
            passwordLogin.requestFocus()
            return
        } else {
            auth.signInWithEmailAndPassword(emailLogin.text.toString(), passwordLogin.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        Toast.makeText(applicationContext, "เข้าสู่ระบบสำเร็จ", Toast.LENGTH_LONG).show()
                        //intent
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        intent.putExtra("Email", emailLogin.text.toString())
                        startActivity(intent)
                        finish()
                    }

                }
        }.addOnFailureListener { exception ->
            if (exception != null) {
                Toast.makeText(this@SignInActivity, "รหัสผ่านหรืออีเมลล์ไม่ถูกต้อง", Toast.LENGTH_SHORT).show()
            }
        }
//        if (emailLogin.text.toString() != null) {
//            Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show()
//            db.collection("Member").document(emailLogin.text.toString()!!).get()
//                    .addOnSuccessListener { document ->
//                        if (document != null) {
//
//
//                            Log.d("exist", "DocumentSnapshot data: ${document.data}")
//
//                            uname = document.data?.get(Member.Name.db).toString()+"   "+ document.data?.get(Member.Surname.db).toString()
//                            intent.putExtra("name", uname)
//
//                            uname = document.data?.get(Member.Address.db).toString()
//                            intent.putExtra("address", uname)
//
//                            uname = document.data?.get(Member.ItemCount.db).toString()
//                            intent.putExtra("itemcount", uname)
//
//                            uname = document.data?.get(Member.TradingCount.db).toString()
//                            intent.putExtra("tradecout", uname)
//
//                            uname = document.data?.get(Member.ScorePoint.db).toString()
//                            intent.putExtra("score", uname)
//
//
//                        } else {
//                            Log.d("exist", "No such document")
//
//                        }
//                    }.addOnFailureListener{exception ->
//                        Log.w("exist", "Error getting documents: ", exception)
//                    }
//        }




    }
}





//fun loadProfile(EMAIL : String){
//    if (EMAIL != null) {
//        Toast.makeText(this, "hi", Toast.LENGTH_SHORT).show()
//        db.collection("Member").document(EMAIL!!).get()
//                .addOnSuccessListener { document ->
//                    if (document != null) {
//
//
//                        Log.d("exist", "DocumentSnapshot data: ${document.data}")
//
//                        MemberData.uname = document.data?.get(Member.Name).toString()
////                        MemberData.uname.append("   ")
////                        MemberData.uname.append(document.data?.get(Member.Surname))
////
////
////                       MemberData.uaddr.append(document.data?.get(Member.Address))
////
////                        MemberData.uitemcount.append(document.data?.get(Member.ItemCount))
////                        MemberData.utradecount.append(document.data?.get(Member.TradingCount))
////                        MemberData.uscore.append(document.data?.get(Member.ScorePoint))
//
//                    } else {
//                        Log.d("exist", "No such document")
//
//                    }
//                }.addOnFailureListener{exception ->
//                    Log.w("exist", "Error getting documents: ", exception)
//                }
//    }
//
//}








