package com.example.BarterApp.bottomnavibar.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.Member
import com.example.BarterApp.R
import com.example.BarterApp.Room_Trading
import com.example.BarterApp.adapter.ItemListener
import com.example.BarterApp.model.Notification
import com.example.BarterApp.model.Product
import com.example.BarterApp.model.TradingRoom
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.common.eventbus.Subscribe
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_incoming_offers.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


lateinit var mRecyclerView1: RecyclerView
lateinit var mDatabase1: DatabaseReference
private val db = FirebaseFirestore.getInstance()
/**
 * A simple [Fragment] subclass.
 * Use the [incomeOffer_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class incomeOffer_fragment : Fragment() , ItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var uname : String
    var firebaseDatabase = Firebase.database.reference

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
        val view = inflater.inflate(R.layout.fragment_income_trading, container, false)

        db.collection("Member").document(arguments?.getString("email")!!).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    uname = document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString()
                }
            }

        mDatabase1 = FirebaseDatabase.getInstance().getReference().child("Offer")

        mRecyclerView1 = view.findViewById(R.id.InrecyclerView)
        mRecyclerView1.layoutManager = LinearLayoutManager(activity)
        showRecyclerView()



        return view
    }

    private fun showRecyclerView() {

        val query = mDatabase1.orderByChild("productUserID").equalTo(arguments?.getString("email")!!)
        val options = FirebaseRecyclerOptions.Builder<Notification>()
            .setQuery(query, Notification::class.java)
            .setLifecycleOwner(this)
            .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Notification, NotiViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiViewHolder {
                return NotiViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.activity_incoming_offers, parent, false)
                )
            }

            override fun onBindViewHolder(holder: NotiViewHolder, position: Int, model: Notification) {
                holder.itemView.name_user.setText(uname)

                Log.d("RQID" , model.RequestUserID!!)


                FirebaseDatabase.getInstance().getReference().child("Product").child(model.productID!!).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val product = snapshot.getValue(Product::class.java)

                        FirebaseFirestore.getInstance().collection("Product").document(model.productUserID!!).collection("inventory").document(model.productID!!).get().addOnSuccessListener {

                            if ((product?.ProductState == "2" && model.OfferStatus == "2") || (product?.ProductState == "11" && model.OfferStatus == "2" ) ){

                                holder.itemView.TradeAcceptBtn.visibility = View.INVISIBLE
                            }

                            else if (product?.ProductState == "1"){
                                holder.itemView.TradeAcceptBtn.visibility = View.VISIBLE
                            }

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }


                })

                CheckofferProduct(holder.itemView.TradeAcceptBtn,model.OfferStatus!!,model.OfferProductID1!!,model.RequestUserID!!)
                CheckofferProduct(holder.itemView.TradeAcceptBtn,model.OfferStatus!!,model.OfferProductID2!!,model.RequestUserID!!)
                CheckofferProduct(holder.itemView.TradeAcceptBtn,model.OfferStatus!!,model.OfferProductID3!!,model.RequestUserID!!)


                /************************* set ภาพสินค้าที่เราของโพส *****************************/
                db.collection("Product").document(arguments?.getString("email")!!).collection("inventory").document(model.productID.toString()).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.ownerproduct)
                        }
                    }

                /****************************************************************************/

             holder.itemView.setOnClickListener{
               val di =  Dialog_flagemt()
                 val bundle = Bundle()
                 val rqID = arrayOf<String>("", "", "")
                 rqID[0]= model.OfferProductID1.toString()
                 rqID[1]= model.OfferProductID2.toString()
                 rqID[2]= model.OfferProductID3.toString()
                 bundle.putStringArray("rqID",rqID)
                 bundle.putString("rqUser",model.RequestUserID)
                 di.arguments = bundle
                 di.show(requireActivity().getSupportFragmentManager(),"itemDialog")



             }

                /************************* set ภาพสินค้าที่เคนที่ขอแลก *****************************/

                db.collection("Member").document(model.RequestUserID.toString()).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            holder.itemView.name_offercoming.setText(document.data?.get(Member.Name.db).toString()+"  "+document.data?.get(Member.Surname.db).toString())
                        }
                    }


                db.collection("Product").document(model.RequestUserID.toString()).collection("inventory").document(model.OfferProductID1.toString()).get()
                    .addOnSuccessListener { document ->
                        if (document != null) {

                            Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.requestproduct1)
                        }
                    }
                if (model.OfferProductID2.toString() != "") {
                    db.collection("Product").document(model.RequestUserID.toString()).collection("inventory").document(model.OfferProductID2.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {

                                Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.requestproduct2)
                            }
                        }
                }
                if (model.OfferProductID3.toString() != "") {
                    db.collection("Product").document(model.RequestUserID.toString()).collection("inventory").document(model.OfferProductID3.toString()).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {

                                Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(holder.itemView.requestproduct3)
                            }
                        }
                }
                /*********************************************************************************/

                /******************************* กดยอมรับ *****************************************/
                holder.itemView.TradeAcceptBtn.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (model.OfferStatus == "1") {

                            val intent = Intent(activity, Room_Trading::class.java)
                            intent.putExtra("owner_mail", arguments?.getString("email")!!)
                            intent.putExtra("tradeID", "TR_"+model.OfferID)
                            intent.putExtra("postMail", model.productUserID)
                            intent.putExtra("offerMail", model.RequestUserID)
                            startActivity(intent)

                        } else {
                            val offerStatus: HashMap<String, Any?> = hashMapOf(

                                "offerStatus" to "1"

                            )
                            firebaseDatabase.child("Offer").child(model.OfferID.toString()).updateChildren(offerStatus).addOnSuccessListener {
                                Log.d("Status", "Status Change!!")
                            }.addOnCanceledListener {
                                Log.d("Status", "Status not Change!!")
                            }

                            val trade = TradingRoom(
                                model.OfferID,
                                model.productUserID,
                                model.RequestUserID,
                                "1",
                                model.productID,
                                "",
                                "",
                                "",
                                "",
                                model.OfferProductID1,
                                model.OfferProductID2,
                                model.OfferProductID3,
                                "",
                                "",
                                "SP","SP"
                            )


                            firebaseDatabase.child("Trading")
                                .child("TR_" + model.OfferID.toString()).setValue(trade)


                            val productStatus : HashMap<String, Any?> = hashMapOf(
                                    "productState" to "2"

                                    )
                            firebaseDatabase.child("Product").child(model.productID!!).updateChildren(productStatus).addOnSuccessListener {
                                db.collection("Product").document(model.productUserID!!).collection("inventor").document(model.productID!!).update("ProductState","2")
                                Log.d("change","Status has change")

                            }.addOnCanceledListener {

                            }
                            val rqproductStatus : HashMap<String, Any?> = hashMapOf(
                                    "productState" to "11"

                            )
                            firebaseDatabase.child("Product").child(model.OfferProductID1!!).updateChildren(rqproductStatus)
                            if (model.OfferProductID2.toString() != "") {
                                firebaseDatabase.child("Product").child(model.OfferProductID2!!).updateChildren(rqproductStatus)
                                        .addOnSuccessListener {

                                        }
                            }
                            if (model.OfferProductID3.toString() != "") {
                                firebaseDatabase.child("Product").child(model.OfferProductID3!!).updateChildren(rqproductStatus)
                                        .addOnSuccessListener {

                                        }
                            }



                            val intent = Intent(activity, Room_Trading::class.java)
                            intent.putExtra("owner_mail", arguments?.getString("email")!!)
                            intent.putExtra("tradeID",  "TR_"+model.OfferID)
                            intent.putExtra("postMail", model.productUserID)
                            intent.putExtra("offerMail", model.RequestUserID)
                            startActivity(intent)



                        }
                    }
                })
                /******************************* กดปฏิเสธ *****************************************/

                if (model.OfferStatus == "1"){
                    holder.itemView.cancelBTN.visibility = View.INVISIBLE
                }

                holder.itemView.cancelBTN.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        FirebaseDatabase.getInstance().getReference().child("Offer").child(model.OfferID.toString()).removeValue()

                    }
                })

            }


        }
        mRecyclerView1.adapter = FirebaseRecyclerAdapter
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


    class NotiViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

    }
    @Subscribe
    fun getData(info : String) {
        Log.d("inMail",info)
    }

    private fun setThreeProduct(rqMail : String,productID : String,imgView : ImageView , nameProduct: TextView,handProduct: TextView,qualProduct : TextView,timeProduct : TextView,card : CardView){
if (productID == ""){
    card.visibility = View.INVISIBLE
}else {
    db.collection("Product").document(rqMail).collection("inventory").document(productID).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Picasso.with(activity).load(document.data?.get("ProductImage").toString()).into(imgView)
                    nameProduct.setText(document.data?.get("ProductName").toString())
                    handProduct.setText(document.data?.get("ProductHand").toString())
                    qualProduct.setText(document.data?.get("ProductQuality").toString())
                    timeProduct.setText(document.data?.get("ProductTime").toString())
                }
            }

}
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
            incomeOffer_fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun CheckofferProduct(tradeBTN : Button,offerStatus : String,ProductID : String,userID : String) {
        if (ProductID != ""){
            FirebaseDatabase.getInstance().getReference().child("Product").child(ProductID).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val product = snapshot.getValue(Product::class.java)

                    val db = FirebaseFirestore.getInstance().collection("Product")
                    db.document(userID).collection("inventory").document(ProductID).get().addOnSuccessListener {

                        if ((product?.ProductState == "2" && offerStatus == "2") || (product?.ProductState == "11" && offerStatus == "2")) {

                            tradeBTN.visibility = View.INVISIBLE
                        } else if (product?.ProductState == "1") {
                            tradeBTN.visibility = View.VISIBLE
                        }

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }
    }

    override fun onItemClick() {
        TODO("Not yet implemented")
    }


}


