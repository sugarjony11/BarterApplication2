package com.example.BarterApp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.BarterApp.model.Chat
import com.example.BarterApp.model.TradingRoom
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.android.synthetic.main.activity_room_trading.*
import kotlinx.android.synthetic.main.dialog_canceltrading.*
import kotlinx.android.synthetic.main.dialog_canceltrading.close_dialog
import kotlinx.android.synthetic.main.dialog_coin.view.*
import kotlinx.android.synthetic.main.dialog_coin.view.close_dialog
import kotlinx.android.synthetic.main.dialog_coin.view.ok_btn
import kotlinx.android.synthetic.main.dialog_confirmoffer.*
import kotlinx.android.synthetic.main.dialog_confirmoffer.view.*
import kotlinx.android.synthetic.main.dialog_deleteproduct.view.*
import kotlinx.android.synthetic.main.dialog_explain.*
import kotlinx.android.synthetic.main.dialog_infor.view.*
import java.util.*


class Room_Trading : AppCompatActivity() {
    var PD_block=0
    var RQ_block=0
    private lateinit var postMail : String
    private lateinit var offerMail : String
    private lateinit var tradeID : String

    private val db = FirebaseFirestore.getInstance()
    private lateinit var pd_Status : String
    private lateinit var rq_Status : String
    private val pdID = arrayOf<String>("", "", "", "", "", "", "", "", "", "")
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {



        postMail = intent.getStringExtra("postMail")!!
        offerMail = intent.getStringExtra("offerMail")!!
        tradeID = intent.getStringExtra("tradeID")!!
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_trading)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Log.d("OfferID", tradeID)
        Log.d("array_Size", pdID.size.toString())


        /************************* ไปแชท *****************************/
        chatBtn.setOnClickListener{
            chatBtn.setImageResource(R.drawable.ic_chat_read)
            db.collection("Member").document(intent.getStringExtra("owner_mail")!!).get() .addOnSuccessListener { document ->
                if (document != null) {
                    val toChat = Intent(this, ChatActivity::class.java)
                    toChat.putExtra("owner_mail", intent.getStringExtra("owner_mail"))
                    toChat.putExtra("sender", intent.getStringExtra("owner_mail"))
                    toChat.putExtra("senderName", document.data?.get("Name").toString() + " " + document.data?.get("Surname").toString())
                    toChat.putExtra("senderPic", document.data?.get("ImageURI").toString())
                    toChat.putExtra("tradeID", tradeID)
                    if (intent.getStringExtra("owner_mail") == postMail){
                        toChat.putExtra("reader", offerMail)
                    }else{
                        toChat.putExtra("reader", postMail)
                    }
                    startActivity(toChat)

                }
            }


        }

        FirebaseDatabase.getInstance().getReference().child("Chat").child(intent.getStringExtra("tradeID")!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chat = snapshot.getValue(Chat::class.java)
                if (snapshot.hasChildren()) {
                    chatBtn.setImageResource(R.drawable.ic_chat_unread)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        /************************* ไปวิธีแลกเปลี่ยน *****************************/
        explainBtn.setOnClickListener{
            val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_explain, null)
            val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
            val  mAlertDialog = mBuilder.show()

            mAlertDialog.close_dialog.setOnClickListener {
                mAlertDialog.dismiss()
            }

        }

        /****************** set ค่าแลกเปลี่ยนเริ่มต้น ********************/
        val mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Trading")

        Log.d("owM", intent.getStringExtra("owner_mail")!!)

        /*
        *  SP = selecting product
        *  SL = selected product
        *  OK = accept product
        *  TR = รับสินค้า
        * */


        /***************ตั้งค่าฝั่งเจ้าของโพส*****************/
        db.collection("Member").document(intent.getStringExtra("postMail")!!).get() .addOnSuccessListener { document ->
            if (document != null) {
                PDname.setText(document.data?.get(Member.Name.db).toString() + "  " + document.data?.get(Member.Surname.db).toString())
                Picasso.with(this).load(document.data?.get("ImageURI").toString()).into(PDpic)
            }
        }



        mDatabase1.child(intent.getStringExtra("tradeID")!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(TradingRoom::class.java)

                if (product?.PD_Status == "SP") {
                    UnLockActionPD()
                    confirmBtn.visibility = View.INVISIBLE


                    Log.d("talk", "Yippy 1")
                    state1.setTextColor(ContextCompat.getColor(applicationContext, R.color.selecting))
                    state1.setText("สถานะ: กำลังเลือกสินค้า")

                    pd_Status = product?.PD_Status.toString()

                    if (intent.getStringExtra("owner_mail") == postMail) {
                        selectBtn.setText("เลือกสินค้า")
                    }


                } else if (product?.PD_Status == "SL") {

                    state1.setTextColor(ContextCompat.getColor(applicationContext, R.color.selected))
                    state1.setText("สถานะ: เลือกสินค้าเสร็จสิ้น")
                    pd_Status = product?.PD_Status.toString()
                    LockActionPD()
                    if (intent.getStringExtra("owner_mail") == postMail) {
                        selectBtn.setText("แก้ไขสินค้า")
                        confirmBtn.setText("ยืนยันสินค้า")
                    }
                    if (product?.PD_Status == "SL" && product?.PD_Status == "SL") {

                        confirmBtn.visibility = View.VISIBLE

                    }
                } else if (product?.PD_Status == "OK") {

                    LockActionPD()
                    state1.setTextColor(ContextCompat.getColor(applicationContext, R.color.ok))
                    state1.setText("สถานะ: ยืนยันสินค้าเสร็จสิ้น")
                    pd_Status = product?.PD_Status.toString()
                    if (product?.PD_Status == "OK" && product?.RQ_Status == "SL") {

                        selectBtn.setText("แก้ไขสินค้า")
                        confirmBtn.visibility = View.VISIBLE
                        if (intent.getStringExtra("owner_mail") == postMail) {
                            confirmBtn.setText("รอผู้ใช้อีกฝ่ายยืนยัน")
                        }
                    } else if (product?.PD_Status == "OK" && product?.RQ_Status == "OK") {
                                updateProduct()
                        /******************************************/
                        if (intent.getStringExtra("owner_mail") == postMail && product?.TradingStatus == "1") {
                            val activity = this@Room_Trading as Activity
                            if (!activity.isFinishing()) {
                                val mDialogView = LayoutInflater.from(this@Room_Trading).inflate(R.layout.dialog_confirmoffer, null)
                                val mBuilder = AlertDialog.Builder(this@Room_Trading).setView(mDialogView)
                                val mAlertDialog = mBuilder.show()

                                mDialogView.ok_btn.setOnClickListener {
                                    mAlertDialog.dismiss()
                                    updateTradeStatus()
                                }
                            }

                        }
                        /******************************************/

                        selectBtn.visibility = View.INVISIBLE
                        confirmBtn.setText("ยืนยันรับสินค้า")
                    } else if (product?.PD_Status == "OK" && product?.RQ_Status == "TR") {
                        if (intent.getStringExtra("owner_mail") == postMail) {
                            canceltradBtn.visibility = View.INVISIBLE
                            selectBtn.visibility = View.INVISIBLE
                            confirmBtn.setText("ยืนยันรับสินค้า")
                        }
                    }

                } else if (product?.PD_Status == "TR" || (product?.RQ_Status == "OK" && product?.PD_Status == "TR")) {
                    canceltradBtn.visibility = View.INVISIBLE
                    pd_Status = product?.PD_Status.toString()
                    state1.setTextColor(ContextCompat.getColor(applicationContext, R.color.traded))
                    state1.setText("สถานะ: รับสินค้าเสร็จสิ้น")
                    if (intent.getStringExtra("owner_mail") == postMail) {
                        confirmBtn.visibility = View.INVISIBLE
                        selectBtn.visibility = View.INVISIBLE
                        LockActionPD()
                        LockActionRQ()
                    }

                }


                if (product?.PD_Item1 != "") {

                    setProduct(product?.PD_Item1.toString(), pd_item1, pdadd1, "postMail", 0)
//                    pdID[0] = product?.PD_Item1.toString()
                }
                if (product?.PD_Item1 == "") {
                    todefault(pd_item1,pdadd1)
                }
                if (product?.PD_Item2 != "") {

                    setProduct(product?.PD_Item2.toString(), pd_item2, pdadd2, "postMail", 1)
//                    pdID[1] = product?.PD_Item2.toString()
                }
                if (product?.PD_Item2 == "") {
                    todefault(pd_item2,pdadd2)
                }
                if (product?.PD_Item3 != "") {
                    setProduct(product?.PD_Item3.toString(), pd_item3, pdadd3, "postMail", 2)
//                    pdID[2] = product?.PD_Item3.toString()
                }
                if (product?.PD_Item3 == "") {
                    todefault(pd_item3,pdadd3)
                }
                if (product?.PD_Item4 != "") {
                    setProduct(product?.PD_Item4.toString(), pd_item4, pdadd4, "postMail", 3)
//                    pdID[3] = product?.PD_Item4.toString()
                }
                if (product?.PD_Item4 == "") {
                    todefault(pd_item4,pdadd4)
                }

                if (product?.PD_Item5 != "") {
                    setProduct(product?.PD_Item5.toString(), pd_item5, pdadd5, "postMail", 4)
//                    pdID[4] = product?.PD_Item5.toString()
                }
                if (product?.PD_Item5 == "") {
                    todefault(pd_item5,pdadd5)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })


        /***************ตั้งค่าฝั่งผู้ขอแลก*****************/

        db.collection("Member").document(intent.getStringExtra("offerMail")!!).get() .addOnSuccessListener { document ->
            if (document != null) {
                RQname.setText(document.data?.get(Member.Name.db).toString() + "  " + document.data?.get(Member.Surname.db).toString())
                Picasso.with(this).load(document.data?.get("ImageURI").toString()).into(RQpic)
            }
        }



        mDatabase1.child(intent.getStringExtra("tradeID")!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.getValue(TradingRoom::class.java)
                if (product?.RQ_Status == "SP") {
                    UnLockActionRQ()
                    confirmBtn.visibility = View.INVISIBLE
                    Log.d("talk", "Yippy 1")
                    state2.setTextColor(ContextCompat.getColor(applicationContext, R.color.selecting))
                    state2.setText("สถานะ: กำลังเลือกสินค้า")

                    rq_Status = product?.RQ_Status.toString()
                    if (intent.getStringExtra("owner_mail") == offerMail) {
                        selectBtn.setText("เลือกสินค้า")

                    }


                } else if (product?.RQ_Status == "SL") {
                    state2.setTextColor(ContextCompat.getColor(applicationContext, R.color.selected))
                    state2.setText("สถานะ: เลือกสินค้าเสร็จสิ้น")
                    rq_Status = product?.RQ_Status.toString()
                    LockActionRQ()
                    if (intent.getStringExtra("owner_mail") == offerMail) {
                        selectBtn.setText("แก้ไขสินค้า")
                        confirmBtn.setText("ยืนยันสินค้า")
                    }
                    if (product?.PD_Status == "SL" && product?.PD_Status == "SL") {
                        confirmBtn.visibility = View.VISIBLE
                    }
                } else if (product?.RQ_Status == "OK") {
                    LockActionRQ()
                    state2.setTextColor(ContextCompat.getColor(applicationContext, R.color.ok))
                    state2.setText("สถานะ: ยืนยันสินค้าเสร็จสิ้น")
                    rq_Status = product?.RQ_Status.toString()
                    if (product?.PD_Status == "SL" && product?.RQ_Status == "OK") {
                        selectBtn.setText("แก้ไขสินค้า")
                        confirmBtn.visibility = View.VISIBLE
                        if (intent.getStringExtra("owner_mail") == offerMail) {
                            confirmBtn.setText("รอผู้ใช้อีกฝ่ายยืนยัน")
                        }
                    } else if (product?.RQ_Status == "OK" && product?.PD_Status == "OK") {

                        /******************************************/
                        if (intent.getStringExtra("owner_mail") == offerMail && product?.TradingStatus == "1") {
                            val activity = this@Room_Trading as Activity
                            if (!activity.isFinishing()) {
                                val mDialogView = LayoutInflater.from(this@Room_Trading).inflate(R.layout.dialog_confirmoffer, null)
                                val mBuilder = AlertDialog.Builder(this@Room_Trading).setView(mDialogView)
                                val mAlertDialog = mBuilder.show()

                                mDialogView.ok_btn.setOnClickListener {
                                    mAlertDialog.dismiss()
                                    updateTradeStatus()
                                }
                            }
                        }
                        /******************************************/

                        selectBtn.visibility = View.INVISIBLE
                        confirmBtn.setText("ยืนยันรับสินค้า")
                    } else if (product?.RQ_Status == "OK" && product?.PD_Status == "TR") {
                        if (intent.getStringExtra("owner_mail") == offerMail) {
                            canceltradBtn.visibility = View.INVISIBLE
                            confirmBtn.setText("ยืนยันรับสินค้า")
                            selectBtn.visibility = View.INVISIBLE
                            LockActionPD()
                            LockActionRQ()
                        }
                    }
                } else if (product?.RQ_Status == "TR" || (product?.RQ_Status == "TR" && product?.PD_Status == "OK")) {
                    canceltradBtn.visibility = View.INVISIBLE
                    rq_Status = product?.RQ_Status.toString()
                    state2.setTextColor(ContextCompat.getColor(applicationContext, R.color.traded))
                    state2.setText("สถานะ: รับสินค้าเสร็จสิ้น")
                    if (intent.getStringExtra("owner_mail") == offerMail) {
                        confirmBtn.visibility = View.INVISIBLE
                        selectBtn.visibility = View.INVISIBLE
                        LockActionPD()
                        LockActionRQ()
                    }
                }


                if (product?.RQ_Item1 != "") {

                    setProduct(product?.RQ_Item1.toString(), rq_item1, rqadd1, "offerMail", 5)
//                    pdID[5] = product?.RQ_Item1.toString()

                }
                if (product?.RQ_Item1 == "") {
                    todefault(rq_item1, rqadd1,)
                }
                if (product?.RQ_Item2 != "") {

                    setProduct(product?.RQ_Item2.toString(), rq_item2, rqadd2, "offerMail", 6)
//                    pdID[6] = product?.RQ_Item2.toString()

                }
                if (product?.RQ_Item2 == "") {
                    todefault( rq_item2, rqadd2)
                }
                if (product?.RQ_Item3 != "") {
                    setProduct(product?.RQ_Item3.toString(), rq_item3, rqadd3, "offerMail", 7)
//                    pdID[7] = product?.RQ_Item3.toString()

                }
                if (product?.RQ_Item3 == "") {
                    todefault(rq_item3, rqadd3)
                }
                if (product?.RQ_Item4 != "") {
                    setProduct(product?.RQ_Item4.toString(), rq_item4, rqadd4, "offerMail", 8)
//                    pdID[8] = product?.RQ_Item4.toString()
                }
                if (product?.RQ_Item4 == "") {
                    todefault(rq_item4, rqadd4)
                }

                if (product?.RQ_Item5 != "") {
                    setProduct(product?.RQ_Item5.toString(), rq_item5, rqadd5, "offerMail", 9)
//                    pdID[9] = product?.RQ_Item5.toString()
                }
                if (product?.RQ_Item5 == "") {
                    todefault(rq_item5, rqadd5)
                }


            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }


        })

        /**********************************************************/


        /*-------------- dialog กดยกเลิกการแลกเปลี่ยน -------------------*/
        canceltradBtn.setOnClickListener {
            val activity = this@Room_Trading as Activity
            if (!activity.isFinishing()) {
                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_canceltrading, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val mAlertDialog = mBuilder.show()
                val offer = tradeID.split("TR_").toTypedArray()
                mAlertDialog.canceltradbtn.setOnClickListener {


                    Reset()

                    mDatabase1.child(tradeID).removeValue().addOnSuccessListener {
                        FirebaseDatabase.getInstance().getReference().child("Offer").child(offer[1]).removeValue()
                        Toast.makeText(this, "สิ้นสุดการแลกเปลี่ยน", Toast.LENGTH_LONG).show()
                    }
                    finish()
                }
                mAlertDialog.close_dialog.setOnClickListener {
                    mAlertDialog.dismiss()
                }
            }
        }
        /*-------------------------------------------------------------*/

        selectBtn.setOnClickListener {
            if (selectBtn.text == "เลือกสินค้า") {
                Log.d("select", "SELECT")

                if (intent.getStringExtra("owner_mail") == postMail){
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "pd_Status" to "SL"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }else{
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "rq_Status" to "SL"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }



            }
            else if (pd_Status == "SL" || rq_Status == "SL"){
                if (intent.getStringExtra("owner_mail") == postMail){
                    UnLockActionRQ()
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "pd_Status" to "SP"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }else{
                    UnLockActionPD()
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "rq_Status" to "SP"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }




            }


        }



        /*-------------- dialog ยืนยันสินค้าในการแลกเปลี่ยน -------------------*/
        confirmBtn.setOnClickListener {
            if (confirmBtn.text == "ยืนยันสินค้า") {
//                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_confirmoffer, null)
//                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
//                val  mAlertDialog = mBuilder.show()




                if (intent.getStringExtra("owner_mail") == postMail){
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "pd_Status" to "OK"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }else{
                    val UserStatus : HashMap<String, Any?> = hashMapOf(
                        "rq_Status" to "OK"
                    )

                    val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

                    moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(UserStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")
                    }.addOnCanceledListener {
                        Log.d("Status", "Status not Change!!")
                    }
                }



            }
            else if ((pd_Status == "OK" && rq_Status == "OK") || (pd_Status == "TR" && rq_Status == "OK") || (pd_Status == "OK" && rq_Status == "TR") ){

                val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_comfirmtrad, null)
                val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                val  mAlertDialog = mBuilder.show()
                var owner_mail = intent.getStringExtra("owner_mail")!!

                mAlertDialog.confirmBtn.setOnClickListener{
                    /*----------ส่ง TradID ไป Feedback-------------*/
//                    var tradkey:String
//                    tradkey = tradeID.toString()



                    val intent = Intent(this, FeedbackActivity::class.java)
                    intent.putExtra("productArray", pdID)
                    intent.putExtra("tradeID", tradeID)
                    intent.putExtra("offerMail", offerMail)
                    intent.putExtra("postMail", postMail)
                    intent.putExtra("owner_mail", owner_mail)

                    startActivity(intent)

                    //   startActivity(intent)
                    mAlertDialog.dismiss()
                }
                mAlertDialog.close_dialog.setOnClickListener {
                    mAlertDialog.dismiss()
                }

            }
        }
        /*-------------------------------------------------------------*/





        registerForContextMenu(pd_item1)
        registerForContextMenu(pd_item2)
        registerForContextMenu(pd_item3)
        registerForContextMenu(pd_item4)
        registerForContextMenu(pd_item5)
        registerForContextMenu(clear1)

        registerForContextMenu(rq_item1)
        registerForContextMenu(rq_item2)
        registerForContextMenu(rq_item3)
        registerForContextMenu(rq_item4)
        registerForContextMenu(rq_item5)
        registerForContextMenu(clear2)

        pd_item1.setOnClickListener {
            PD_block=1
            Toast.makeText(this, "ไม่สามารถเปลี่ยนสินค้าได้", Toast.LENGTH_SHORT).show()
        }

        pd_item2.setOnClickListener {
            PD_block=2
            openContextMenu(pd_item2)
        }
        pd_item3.setOnClickListener {
            PD_block=3
            openContextMenu(pd_item3)
        }
        pd_item4.setOnClickListener {
            PD_block=4
            openContextMenu(pd_item4)
        }
        pd_item5.setOnClickListener {
            PD_block=5
            openContextMenu(pd_item5)
        }
        clear1.setOnClickListener {



            RemoveProduct(pd_item2, pdadd2, "pd_Item2", "pd")
            todefault(pd_item2,pdadd2)
            RemoveProduct(pd_item3, pdadd3, "pd_Item3", "pd")
            todefault(pd_item3,pdadd3)
            RemoveProduct(pd_item4, pdadd4, "pd_Item4", "pd")
            todefault(pd_item4,pdadd4)
            RemoveProduct(pd_item5, pdadd5, "pd_Item5", "pd")
            todefault(pd_item5,pdadd5)

        }

        rq_item1.setOnClickListener {
            RQ_block=1
            openContextMenu(rq_item1)
        }
        rq_item2.setOnClickListener {
            RQ_block=2
            openContextMenu(rq_item2)
        }
        rq_item3.setOnClickListener {
            RQ_block=3
            openContextMenu(rq_item3)
        }
        rq_item4.setOnClickListener {
            RQ_block=4
            openContextMenu(rq_item4)
        }
        rq_item5.setOnClickListener {
            RQ_block=5
            openContextMenu(rq_item5)
        }
        clear2.setOnClickListener {

            RemoveProduct(rq_item1, rqadd1, "rq_Item1", "rq")
            todefault(rq_item1,rqadd1)
            RemoveProduct(rq_item2, rqadd2, "rq_Item2", "rq")
            todefault(rq_item2,rqadd2)
            RemoveProduct(rq_item3, rqadd3, "rq_Item3", "rq")
            todefault(rq_item3,rqadd3)
            RemoveProduct(rq_item4, rqadd4, "rq_Item4", "rq")
            todefault(rq_item4,rqadd4)
            RemoveProduct(rq_item5, rqadd5, "rq_Item5", "rq")
            todefault(rq_item5,rqadd5)

        }



    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.menu_select_trad, menu)
        super.onCreateContextMenu(menu, v, menuInfo)
    }



    @SuppressLint("ResourceAsColor")
    override fun onContextItemSelected(item: MenuItem): Boolean {

        when(true) {
            /*---------------------เลือกสินค้าจากคลัง-----------------------*/

            /*--------------------- เจ้าของโพส -----------------------*/
            item.itemId == R.id.slectitem -> {

                if (PD_block == 2) {
                    PD_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "2")
                    intent.putExtra("EMAIL", postMail)
                    intent.putExtra("Role", "pd")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (PD_block == 3) {
                    PD_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "3")
                    intent.putExtra("EMAIL", postMail)
                    intent.putExtra("Role", "pd")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (PD_block == 4) {
                    PD_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "4")
                    intent.putExtra("EMAIL", postMail)
                    intent.putExtra("Role", "pd")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (PD_block == 5) {
                    PD_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "5")
                    intent.putExtra("EMAIL", postMail)
                    intent.putExtra("Role", "pd")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }


                /*--------------------- คนยื่นแลก -----------------------*/

                if (RQ_block == 1) {
                    RQ_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "1")
                    intent.putExtra("EMAIL", offerMail)
                    intent.putExtra("Role", "rq")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (RQ_block == 2) {
                    RQ_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "2")
                    intent.putExtra("EMAIL", offerMail)
                    intent.putExtra("Role", "rq")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (RQ_block == 3) {
                    RQ_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "3")
                    intent.putExtra("EMAIL", offerMail)
                    intent.putExtra("Role", "rq")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (RQ_block == 4) {
                    RQ_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "4")
                    intent.putExtra("EMAIL", offerMail)
                    intent.putExtra("Role", "rq")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }
                if (RQ_block == 5) {
                    RQ_block = 0
                    val intent = Intent(this, offer_Stock::class.java)
                    intent.putExtra("form", "TR")
                    intent.putExtra("position", "5")
                    intent.putExtra("EMAIL", offerMail)
                    intent.putExtra("Role", "rq")
                    intent.putExtra("tradeID", tradeID)
                    startActivity(intent)


                }

                return true
            }
            /*--------------------ใช้เงินแทนการแลกเปลี่ยน-------------------------*/
            item.itemId == R.id.coin -> {
                val activity = this@Room_Trading as Activity
                if (!activity.isFinishing()) {
                    val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_coin, null)
                    val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
                    val mAlertDialog = mBuilder.show()


                    /*-------user1--------*/
                    if (PD_block == 1) {
                        mDialogView.ok_btn.setOnClickListener {
                            PD_block = 0

                            SaveMoney(pdadd1, mDialogView.numText.text.toString(), "pd_Item1", "pd")

                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (PD_block == 2) {
                        mDialogView.ok_btn.setOnClickListener {
                            PD_block = 0

                            SaveMoney(pdadd2, mDialogView.numText.text.toString(), "pd_Item2", "pd")

                            mAlertDialog.dismiss()
                        }

                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (PD_block == 3) {
                        mDialogView.ok_btn.setOnClickListener {
                            PD_block = 0
                            SaveMoney(pdadd3, mDialogView.numText.text.toString(), "pd_Item3", "pd")
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (PD_block == 4) {
                        mDialogView.ok_btn.setOnClickListener {
                            PD_block = 0
                            SaveMoney(pdadd4, mDialogView.numText.text.toString(), "pd_Item4", "pd")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (PD_block == 5) {
                        mDialogView.ok_btn.setOnClickListener {
                            PD_block = 0
                            SaveMoney(pdadd5, mDialogView.numText.text.toString(), "pd_Item5", "pd")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }

                    /*-------user2--------*/
                    if (RQ_block == 1) {
                        mDialogView.ok_btn.setOnClickListener {
                            RQ_block = 0
                            SaveMoney(rqadd1, mDialogView.numText.text.toString(), "rq_Item1", "rq")
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (RQ_block == 2) {
                        mDialogView.ok_btn.setOnClickListener {
                            RQ_block = 0
                            SaveMoney(rqadd2, mDialogView.numText.text.toString(), "rq_Item2", "rq")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }

                    if (RQ_block == 3) {
                        mDialogView.ok_btn.setOnClickListener {
                            RQ_block = 0
                            SaveMoney(rqadd3, mDialogView.numText.text.toString(), "rq_Item3", "rq")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                    if (RQ_block == 4) {
                        mDialogView.ok_btn.setOnClickListener {
                            RQ_block = 0
                            SaveMoney(rqadd4, mDialogView.numText.text.toString(), "rq_Item4", "rq")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }

                    if (RQ_block == 5) {
                        mDialogView.ok_btn.setOnClickListener {
                            RQ_block = 0
                            SaveMoney(rqadd5, mDialogView.numText.text.toString(), "rq_Item5", "rq")

                            //dismiss dialog
                            mAlertDialog.dismiss()

                        }
                        mDialogView.close_dialog.setOnClickListener {
                            Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                            mAlertDialog.dismiss()
                        }

                    }
                }
                /**----->**/        return true }
            //ปุ่มรีเซตค่า
            item.itemId == R.id.reset -> {
                /*-------user1--------*/
                if (PD_block == 2) {
                    PD_block = 0
                    RemoveProduct(pd_item2, pdadd2, "pd_Item2", "pd")
                } else if (PD_block == 3) {
                    PD_block = 0
                    RemoveProduct(pd_item3, pdadd3, "pd_Item3", "pd")
                } else if (PD_block == 4) {
                    PD_block = 0
                    RemoveProduct(pd_item4, pdadd4, "pd_Item4", "pd")
                } else if (PD_block == 5) {
                    PD_block = 0
                    RemoveProduct(pd_item5, pdadd5, "pd_Item5", "pd")
                }

                /*-------user2--------*/
                if (RQ_block == 1) {
                    RQ_block = 0
                    RemoveProduct(rq_item1, rqadd1, "rq_Item1", "rq")
                }
                if (RQ_block == 2) {
                    RQ_block = 0
                    RemoveProduct(rq_item2, rqadd2, "rq_Item2", "rq")
                }
                if (RQ_block == 3) {
                    RQ_block = 0
                    RemoveProduct(rq_item3, rqadd3, "rq_Item3", "rq")
                }
                if (RQ_block == 4) {
                    RQ_block = 0
                    RemoveProduct(rq_item4, rqadd4, "rq_Item4", "rq")
                }
                if (RQ_block == 5) {
                    RQ_block = 0
                    RemoveProduct(rq_item5, rqadd5, "rq_Item5", "rq")
                }
                Toast.makeText(this, "รีเซตค่า", Toast.LENGTH_LONG).show()
                return true
            }
            //ปุ่มกดยกเลิก
            item.itemId == R.id.cancel -> {
                Toast.makeText(this, "ยกเลิก", Toast.LENGTH_LONG).show()
                return true
            }

            else -> return super.onContextItemSelected(item)
        }



    }
    /************************************** เก็บเงิน ************************************************/
    private fun SaveMoney(textView: TextView, Money: String, role: String, roleUser: String){

        val moneymoney : HashMap<String, Any?> = hashMapOf(
            role to Money
        )

        val moneyDB = FirebaseDatabase.getInstance().getReference().child("Trading")

        moneyDB.child(intent.getStringExtra("tradeID")!!).updateChildren(moneymoney).addOnSuccessListener {
            Log.d("Status", "Status Change!!")
        }.addOnCanceledListener {
            Log.d("Status", "Status not Change!!")
        }
        val produtStatus : HashMap<String, Any?> = hashMapOf(
            "productState" to "1"
        )

        if (roleUser == "pd"){
            if (pdID[role.get(role.length - 1).toString().toInt() - 1] != "") {

                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID[role.get(role.length - 1).toString().toInt() - 1]).updateChildren(produtStatus).addOnSuccessListener {
                    pdID[role.get(role.length - 1).toString().toInt() - 1] = ""
                    Log.d("Status", "Status Change!!")
                }
            }
        }else{

            if (pdID[role.get(role.length - 1).toString().toInt() + 4] != "") {
                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID[role.get(role.length - 1).toString().toInt() + 4]).updateChildren(produtStatus).addOnSuccessListener {
                    pdID[role.get(role.length - 1).toString().toInt() + 4] = ""
                    Log.d("Status", "Status Change!!")
                }
            }
        }


    }
    /************************************** set สินค้า ************************************************/
    private fun setProduct(productID: String, imgBlock: ImageView, txt: TextView, roleMail: String, position: Int){



        var pattern = Regex("^[0-9]*\$")
        if (pattern.containsMatchIn(productID) == true)
        {  txt.setTextColor(Color.BLACK)
            txt.setText(productID + "\n บาท")

            imgBlock.setImageResource(R.drawable.ic_trade_bg)
            imgBlock.setOnLongClickListener(null)
        }
        else {
//            val productStatus : HashMap<String, Any?> = hashMapOf(
//                    "productState" to "11"
//
//            )
//            FirebaseDatabase.getInstance().getReference().child("Product").child(productID).updateChildren(productStatus).addOnSuccessListener {
//                Log.d("change","Status has change")
//
//            }.addOnCanceledListener {
//
//            }
            db.collection("Product").document(intent.getStringExtra(roleMail)!!).collection("inventory").document(productID).get().addOnSuccessListener { document ->

                pdID[position] = productID
                Picasso.with(this@Room_Trading).load(document.data?.get("ProductImage").toString()).into(imgBlock)
                txt.setText("")
                Log.d("loop $position", pdID[position])
            }
            imgBlock.setOnLongClickListener{
                showIforDialog(productID, intent.getStringExtra(roleMail)!!)
                return@setOnLongClickListener true
            }
        }
    }
    /************************************** remove สินค้า ************************************************/
    private fun RemoveProduct(imgBlock: ImageView, txt: TextView, position: String, role: String){
        imgBlock.setImageResource(R.drawable.ic_trade_bg)
        txt.setTextColor(Color.WHITE)
        txt.setText("+")
        val remove : HashMap<String, Any?> = hashMapOf(
            position to ""
        )

        val tradeDB = FirebaseDatabase.getInstance().getReference().child("Trading")

        tradeDB.child(intent.getStringExtra("tradeID")!!).updateChildren(remove).addOnSuccessListener {
            Log.d("Status", "Status Change!!")
        }.addOnCanceledListener {
            Log.d("Status", "Status not Change!!")
        }



        if (pdID[position.get(position.length - 1).toString().toInt() - 1].isNotEmpty() || pdID[position.get(position.length - 1).toString().toInt() + 4].isNotEmpty()  ) {


            val produtStatus: HashMap<String, Any?> = hashMapOf(
                "productState" to "1"
            )

            if (role == "pd") {

                if (pdID[1].isEmpty()){
                    pdID[position.get(position.length - 1).toString().toInt() - 1] = ""
                }else {

                    Log.d("postion", pdID[position.get(position.length - 1).toString().toInt() - 1])
                    FirebaseDatabase.getInstance().getReference().child("Product").child(pdID[position.get(position.length - 1).toString().toInt() - 1]).updateChildren(produtStatus).addOnSuccessListener {
                        Log.d("Status", "Status Change!!")

                    }
                }
                pdID[position.get(position.length - 1).toString().toInt() - 1] = ""
            } else {

                Log.d("postion", pdID[position.get(position.length - 1).toString().toInt() + 4])
                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID[position.get(position.length - 1).toString().toInt() + 4]).updateChildren(produtStatus).addOnSuccessListener {
                    Log.d("Status", "Status Change!!")
                    pdID[position.get(position.length - 1).toString().toInt() + 4] = ""
                }

            }
        }
    }

    private fun showIforDialog(ProductID: String, userMail: String){
        val mDialogView = LayoutInflater.from(this).inflate(R.layout.dialog_infor, null)
        val mBuilder = AlertDialog.Builder(this).setView(mDialogView)
        val  mAlertDialog = mBuilder.show()

        mDialogView.closeButton.setOnClickListener(){

            mAlertDialog.dismiss()
        }

        db.collection("Product").document(userMail).collection("inventory").document(ProductID).get().addOnSuccessListener {

            Picasso.with(this@Room_Trading).load(it.data?.get("ProductImage").toString()).into(mDialogView.product_img)
            mDialogView.name_product.setText(it.data?.get("ProductName").toString())
            mDialogView.categoryText.setText(it.data?.get("ProductType").toString())
            mDialogView.typeText.setText("ลักษณะสินค้า : มือ" + it.data?.get("ProductHand").toString())
            mDialogView.perText.setText("สภาพสินค้า : " + it.data?.get("ProductQuality").toString() + " %")
            mDialogView.timeText.setText("ระยะเวลา : " + it.data?.get("ProductTime").toString())
            mDialogView.detailText.setText(it.data?.get("ProductDetail").toString())


        }

    }

    private fun LockActionPD() {

        pd_item1.isEnabled = false
        pd_item2.isEnabled = false
        pd_item3.isEnabled = false
        pd_item4.isEnabled = false
        pd_item5.isEnabled = false
        clear1.isEnabled = false
    }

    private fun LockActionRQ(){

        rq_item1.isEnabled = false
        rq_item2.isEnabled = false
        rq_item3.isEnabled = false
        rq_item4.isEnabled = false
        rq_item5.isEnabled = false
        clear2.isEnabled = false
    }
    private fun UnLockActionPD() {

        pd_item1.isEnabled = true
        pd_item2.isEnabled = true
        pd_item3.isEnabled = true
        pd_item4.isEnabled = true
        pd_item5.isEnabled = true
        clear1.isEnabled = true
    }
    private fun UnLockActionRQ(){

        rq_item1.isEnabled = true
        rq_item2.isEnabled = true
        rq_item3.isEnabled = true
        rq_item4.isEnabled = true
        rq_item5.isEnabled = true
        clear2.isEnabled = true
    }

    private fun todefault(imgBlock: ImageView, txt: TextView,){
        imgBlock.setImageResource(R.drawable.ic_trade_bg)
        txt.setTextColor(Color.WHITE)
        txt.setText("+")
    }

    private fun updateTradeStatus(){

        val tradeStatus : HashMap<String, Any?> = hashMapOf(
            "tradingStatus" to "2"
        )

        val ts = FirebaseDatabase.getInstance().getReference().child("Trading")

        ts.child(intent.getStringExtra("tradeID")!!).updateChildren(tradeStatus).addOnSuccessListener {
            Log.d("Status", "Status Change!!")
        }.addOnCanceledListener {
            Log.d("Status", "Status not Change!!")
        }

    }

    private fun Reset(){
        val prductStatus : HashMap<String, Any?> = hashMapOf(
            "productState" to "1"

        )
        for (i in 0..9)
            if (pdID[i] != "")
                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID[i]).updateChildren(prductStatus).addOnSuccessListener {

                }
    }


    private fun updateProduct() {
        val productState: HashMap<String, Any?> = hashMapOf(

                "productState" to "2"

        )

        for (i in 0..4) {
            if (pdID!![i] != "") {

                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID!![i]).updateChildren(productState).addOnSuccessListener {
                    db.collection("Product").document(intent.getStringExtra("owner_mail")!!).collection("inventory").document(pdID!![i]).update("ProductState","2")
                    Log.d("Status pd $i", "Status change")
                }.addOnCanceledListener {
                    Log.d("Status $i", "Status not change")
                }
            }
                if (pdID!![i+5] != ""){

                FirebaseDatabase.getInstance().getReference().child("Product").child(pdID!![i+5]).updateChildren(productState).addOnSuccessListener {
                    db.collection("Product").document(intent.getStringExtra("owner_mail")!!).collection("inventory").document(pdID!![i+5]).update("ProductState","2")
                    Log.d("Status rq $i", "Status change")
                }.addOnCanceledListener {
                    Log.d("Status $i", "Status not change")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}