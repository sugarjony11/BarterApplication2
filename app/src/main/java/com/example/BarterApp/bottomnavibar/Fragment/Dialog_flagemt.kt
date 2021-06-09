package com.example.BarterApp.bottomnavibar.Fragment
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.BarterApp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_itemoffer.view.*


class Dialog_flagemt : DialogFragment() {



    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view : View = inflater.inflate(R.layout.dialog_itemoffer,container,false)

        val rqID = arguments?.getStringArray("rqID")
     Log.d("rqID",arguments?.getString("rqUser")!!)
        FirebaseFirestore.getInstance().collection("Product").document(arguments?.getString("rqUser")!!).collection("inventory").document(rqID?.get(0)!!).get().addOnSuccessListener {

            view.name_product1.setText(it.data?.get("ProductName").toString())
            view.hand_product1.setText(" มือ "+it.data?.get("ProductHand").toString())
            view.percent_product1.setText(it.data?.get("ProductQuality").toString() + " %")
            view.time_product1.setText(it.data?.get("ProductTime").toString())
            Picasso.with(activity).load(it.data?.get("ProductImage").toString()).into(view.img_product1)

        }

        if (rqID[1] != "") {
            FirebaseFirestore.getInstance().collection("Product").document(arguments?.getString("rqUser")!!).collection("inventory").document(rqID?.get(1)!!).get().addOnSuccessListener {

                view.name_product2.setText(it.data?.get("ProductName").toString())
                view.hand_product2.setText(" มือ " + it.data?.get("ProductHand").toString())
                view.percent_product2.setText(it.data?.get("ProductQuality").toString() + " %")
                view.time_product2.setText(it.data?.get("ProductTime").toString())
                Picasso.with(activity).load(it.data?.get("ProductImage").toString()).into(view.img_product2)

            }
        }
            if (rqID[1] == ""){
                view.productCard2.visibility = View.INVISIBLE
            }
            if (rqID[2] != ""){
                FirebaseFirestore.getInstance().collection("Product").document(arguments?.getString("rqUser")!!).collection("inventory").document(rqID?.get(2)!!).get().addOnSuccessListener {

                    view.name_product3.setText(it.data?.get("ProductName").toString())
                    view.hand_product3.setText(" มือ "+it.data?.get("ProductHand").toString())
                    view.percent_product3.setText(it.data?.get("ProductQuality").toString() + " %")
                    view.time_product3.setText(it.data?.get("ProductTime").toString())
                    Picasso.with(activity).load(it.data?.get("ProductImage").toString()).into(view.img_product3)

                }

        }
        if (rqID[2] == ""){
            view.productCard3.visibility = View.INVISIBLE
        }


        view.closeButton.setOnClickListener {
            dismiss()
        }
        return view
    }




}
