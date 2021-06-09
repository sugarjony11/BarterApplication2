package com.example.BarterApp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.model.Chat
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_incoming_offers.view.*
import kotlinx.android.synthetic.main.chat_text_left.view.*
import java.text.SimpleDateFormat
import java.util.*


lateinit var recyclerView: RecyclerView
lateinit var chatDB: DatabaseReference
private var dateFM = SimpleDateFormat("dd/MM/YYYY HH:mm:ss")
private val firebaseDB = FirebaseFirestore.getInstance()
var name = "set"
class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Log.d("tradID",intent.getStringExtra("tradeID")!!)
        Log.d("sender",intent.getStringExtra("sender")!!)
        Log.d("reader",intent.getStringExtra("reader")!!)


        var date = Date()
       chatDB = FirebaseDatabase.getInstance().getReference().child("Chat").child(intent.getStringExtra("tradeID")!!)
        recyclerView = findViewById(R.id.ShowChat)
        recyclerView.layoutManager = LinearLayoutManager(this)


        sendBTN.setOnClickListener {
            val msg = Chat(intent.getStringExtra("sender")!!,intent.getStringExtra("senderName")!!,intent.getStringExtra("senderPic")!!,intent.getStringExtra("reader")!!,text_send.text.toString(), dateFM.format(date))
            chatDB.push().setValue(msg)
            text_send.setText("")
        }

        showMessage()


    }

    private fun showMessage() {

        val options = FirebaseRecyclerOptions.Builder<Chat>()
                .setQuery(chatDB, Chat::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Chat, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.chat_text_left, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Chat) {

                if (model.sender != intent.getStringExtra("owner_mail")){
                    holder.itemView.youText.setBackgroundResource(R.drawable.chat_right)
//                    ProductViewHolder(LayoutInflater.from(parent).inflate(R.layout.chat_text_left, parent, false))
                }

                Log.d("get",intent.getStringExtra("sender")!!)
                Picasso.with(this@ChatActivity).load(model.senderPic).into(holder.itemView.youPic)
                holder.itemView.nameUsermsg.setText(model.senderName)
                Log.d("set", name)
                holder.itemView.youText.setText(model.message)




            }



        }
        recyclerView.adapter = FirebaseRecyclerAdapter

    }
    class ProductViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}