package com.example.BarterApp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_report_user.*
import java.util.*

class ReportUser : AppCompatActivity() {

    private lateinit var rt : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_user)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        CancelBtn.setOnClickListener {
            onBackPressed()
        }
        ReportBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                if(report1.isChecked == false && report2.isChecked == false && report3.isChecked == false && report4.isChecked == false &&report5.isChecked == false){
                    Toast.makeText(this@ReportUser,"กรุณาเลือกเหตุผลที่จะรายงาน",Toast.LENGTH_LONG).show()

                }else {
                    val reportUser : HashMap<String, Any?> = hashMapOf(

                            "ReportType" to rt

                    )

                    FirebaseFirestore.getInstance().collection("Report").document(intent.getStringExtra("pdMail")!!).collection(intent.getStringExtra("owner").toString()).add(reportUser).addOnSuccessListener {
                        Toast.makeText(this@ReportUser, "รายงานผู้ใช้สำเร็จ", Toast.LENGTH_LONG).show()


                        val tot = Intent(this@ReportUser, OtherProfile::class.java)
                        tot.putExtra("owner_mail",intent.getStringExtra("owner").toString())
                        tot.putExtra("uuid",intent.getStringExtra("pdMail"))
                        startActivity(tot)

                    }


                }

            }

        })
    }

    fun reportClick(view: View){
        if (view is RadioButton) {
            val check = view.isChecked

            when (view.getId()) {
                R.id.report1 ->
                    if (check) {
                        Toast.makeText(this@ReportUser, "1", Toast.LENGTH_LONG).show()
                        rt = "เป็นบัญชีผู้ใช้ปลอม"
                    }
                R.id.report2 ->
                    if (check) {
                        Toast.makeText(this@ReportUser, "2", Toast.LENGTH_LONG).show()
                        rt = "หลอกลวง ทุจริต"
                    }
                R.id.report3 ->
                    if (check) {
                        Toast.makeText(this@ReportUser, "3", Toast.LENGTH_LONG).show()

                        rt = "แอบอ้างใช้รูปของบุคคลอื่น"
                    }
                R.id.report4 ->
                    if (check) {
                        Toast.makeText(this@ReportUser, "4", Toast.LENGTH_LONG).show()
                        rt = "คุกคามด้วยคำพูด พูดจาไม่เหมาะสม"
                    }
                R.id.report5 ->
                    if (check) {
                        Toast.makeText(this@ReportUser, "5", Toast.LENGTH_LONG).show()
                        rt = "มีรูปภาพเนื้อหาที่ไม่เหมาะสม"
                    }
            }
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()

        return super.onSupportNavigateUp()
    }
}