package com.example.BarterApp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.model.Feedback
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_row_feedback.*
import kotlinx.android.synthetic.main.rv_row_feedback.view.*

class ViewFeedback : AppCompatActivity() {
    private lateinit var dbRef  : DatabaseReference
    lateinit var mRecyclerView: RecyclerView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_feedback)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        mRecyclerView = findViewById(R.id.recyclerFeedback)
        mRecyclerView.layoutManager = LinearLayoutManager(this)


        var EMAIL = intent.getStringExtra("EMAIL")
        Log.d("mail", EMAIL!!)

        var UID = intent.getStringExtra("UID")
        Log.d("UID", UID!!)


        dbRef  = FirebaseDatabase.getInstance().getReference().child("Feedback").child(UID!!)

        setupRecycleView()

    }

    fun setupRecycleView(){

        // val query = dbRef.orderByChild("UID").equalTo(uid)
        var query: Query = dbRef.orderByChild("reader").equalTo(intent.getStringExtra("EMAIL"))


        val firebaseRecycleOption = FirebaseRecyclerOptions.Builder<Feedback>()
                .setQuery(query, Feedback::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirestoreRecyclerAdapter = object : FirebaseRecyclerAdapter<Feedback,FeedbackViewHolder>(firebaseRecycleOption) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
                return FeedbackViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_row_feedback, parent, false))
            }



            override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int, model: Feedback) {

               Picasso.with(this@ViewFeedback).load(model.Image).into(holder.itemView.RQpic_profile)
                db.collection("Member").document(model.sender!!).get() .addOnSuccessListener { document ->
                    if (document != null) {
                        holder.itemView.nameComment.text = document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString()


                    }
                }

                holder.itemView.TextComment.text = model.Comment
                var rating = model.Rating
                holder.itemView.ratingComment.rating = rating!!.toFloat()


              //  Picasso.with(holder?.itemView?.context).load(model.ProductImage).into(holder.itemView.img_product)

                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {

                    }

                })
            }

        }
        mRecyclerView.adapter = FirestoreRecyclerAdapter



    }
    class FeedbackViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}