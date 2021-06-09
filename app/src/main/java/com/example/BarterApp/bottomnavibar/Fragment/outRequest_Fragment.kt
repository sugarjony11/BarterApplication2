package com.example.BarterApp.bottomnavibar.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.Member
import com.example.BarterApp.R
import com.example.BarterApp.Room_Trading
import com.example.BarterApp.adapter.ItemListener
import com.example.BarterApp.model.Notification
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_out_offers.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var mSearch2: EditText
lateinit var mSearchBtn2: ImageView
lateinit var mClearBtn2: ImageView
lateinit var mRecyclerView2: RecyclerView
lateinit var mDatabase2: DatabaseReference

/**
 * A simple [Fragment] subclass.
 * Use the [outRequest_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class outRequest_Fragment : Fragment() , ItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val db = FirebaseFirestore.getInstance()
    private lateinit var uname : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_out_trading, container, false)

        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("Offer")

        mRecyclerView2 = view.findViewById(R.id.OutrecyclerView)
        mRecyclerView2.layoutManager = LinearLayoutManager(activity)


        Log.d("inmail",arguments?.getString("email")!!)
        if (arguments?.getString("email") != null){

            db.collection("Member").document(arguments?.getString("email")!!).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            uname = document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString()
                        }
                    }

        }
        showRecyclerView()
        return view
    }

    private fun showRecyclerView() {

        val q = mDatabase2.orderByChild("requestUserID").equalTo(arguments?.getString("email")!!)
        val options = FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(q, Notification::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Notification,NotificationViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
                return NotificationViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.activity_out_offers, parent, false)
                )
            }

            override fun onBindViewHolder(holder: NotificationViewHolder, position: Int, model: Notification) {

                holder.itemView.name_offercoming.setText(uname)

                /************************* set ภาพสินค้าคนที่ขอแลก *****************************/
                db.collection("Product").document(model.productUserID.toString()).collection("inventory").document(model.productID.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.ownerpd)
                            }
                        }

                db.collection("Member").document(model.productUserID.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                holder.itemView.name_post_user.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString())
                            }
                        }


                /****************************************************************************/


                /************************* set ภาพสินค้าที่เราขอแลก *****************************/



                db.collection("Product").document(arguments?.getString("email")!!).collection("inventory").document(model.OfferProductID1.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.pd1)
                            }
                        }
                if (model.OfferProductID2.toString() != "") {
                    db.collection("Product").document(arguments?.getString("email")!!).collection("inventory").document(model.OfferProductID2.toString()).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {

                                    Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.pd2)
                                }
                            }
                }
                if (model.OfferProductID3.toString() != "") {
                    db.collection("Product").document(arguments?.getString("email")!!).collection("inventory").document(model.OfferProductID3.toString()).get()
                            .addOnSuccessListener { document ->
                                if (document != null) {

                                    Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.pd3)
                                }
                            }
                }
                /*********************************************************************************/

                if (model.OfferStatus.toString() == "1"){
                    holder.itemView.cancelBtn.visibility = View.INVISIBLE
                    holder.itemView.setOnClickListener(object : View.OnClickListener {
                        override fun onClick(v: View?) {
                            val intent = Intent(activity, Room_Trading::class.java)
                            intent.putExtra("owner_mail", arguments?.getString("email")!!)
                            intent.putExtra("tradeID", "TR_"+model.OfferID)
                            intent.putExtra("postMail",model.productUserID)
                            intent.putExtra("offerMail",model.RequestUserID)
                            startActivity(intent)
                        }
                    })
                }

                holder.itemView.cancelBtn.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        FirebaseDatabase.getInstance().getReference().child("Offer").child(model.OfferID.toString()).removeValue()
                    }
                })


            }



        }
        mRecyclerView2.adapter = FirebaseRecyclerAdapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


    class NotificationViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Home_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                outRequest_Fragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onItemClick() {
        TODO("Not yet implemented")
    }


}