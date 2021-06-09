package com.example.BarterApp.bottomnavibar.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.BarterApp.InforProductUser
import com.example.BarterApp.InformationActivity
import com.example.BarterApp.R
import com.example.BarterApp.adapter.ItemListener
import com.example.BarterApp.model.Product
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.rv_row.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

lateinit var mSearch: EditText
lateinit var mSearchBtn: ImageView
lateinit var mClearBtn: ImageView
lateinit var mRecyclerView: RecyclerView
lateinit var mDatabase: DatabaseReference

/**
 * A simple [Fragment] subclass.
 * Use the [Home_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home_Fragment : Fragment() , ItemListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



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
        val view = inflater.inflate(R.layout.fragment_home, container, false)



        mDatabase = FirebaseDatabase.getInstance().getReference().child("Product")
        mSearch = view.findViewById(R.id.Home_searchText)
        mSearchBtn = view.findViewById(R.id.Home_search_btn)
//        mClearBtn = view.findViewById(R.id.Home_clear_btn)
        mRecyclerView = view.findViewById(R.id.recyclerViewHome)
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
       showRecyclerView()

        mSearchBtn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val searchText = mSearch.getText().toString()
                if(searchText.isEmpty()){
                    showRecyclerView()
                }
                else{
                    firebaseUserSearch(searchText)
                }


            }

        })

//        mClearBtn.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                mSearch.setText("")
//                showRecyclerView()
//            }
//
//        })


        return view
    }

    private fun showRecyclerView() {

        val options = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(mDatabase.orderByChild("score"), Product::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(
                        LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false)
                )
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
                var status: String? = null
                if (model.ProductState.toString() == "1"|| model.ProductState.toString() == "11") {
                    status = "มีของ"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(context!!, R.color.selected))
                } else if (model.ProductState.toString() == "2" ) {
                    status = "แลกเปลี่ยน"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(context!!, R.color.yellowApp1))
                } else if (model.ProductState.toString() == "0") {
                    status = "ไม่มีของ"
                    holder.itemView.state_product.setText(status)
                }

                holder.itemView.name_product.setText(model.ProductName)
                holder.itemView.hand_product.setText("มือ " + model.ProductHand.toString())
                holder.itemView.percent_product.setText(model.ProductQuality.toString())
                holder.itemView.time_product.setText(model.ProductTime)
                holder.itemView.state_product.setText(status)


                Picasso.with(activity).load(model.ProductImage).into(holder.itemView.img_product)




                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {

                        if (model.USerID == arguments?.getString("email")) {
                            val intent = Intent(activity, InforProductUser::class.java)


                            intent.putExtra("Email", arguments?.getString("email"))
                            intent.putExtra("NAME", getRef(position).key)
                            intent.putExtra("PRODUCTID", model.ProductID)
                            startActivity(intent)
                        }else{



                            val intent = Intent(activity, InformationActivity::class.java)
                            intent.putExtra("userID",model.USerID)
                            intent.putExtra("NAME", getRef(position).key)
                            intent.putExtra("Owner_EMAIL",arguments?.getString("email"))

                            startActivity(intent)
                        }
                    }

                })
            }



        }
        mRecyclerView.adapter = FirebaseRecyclerAdapter
    }


    private fun firebaseUserSearch(searchText: String) {
        var query: Query = mDatabase.orderByChild("productName").startAt(searchText).endAt(searchText + "\uf8ff")
        val options = FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(query, Product::class.java)
                .setLifecycleOwner(this)
                .build()

        var FirebaseRecyclerAdapter = object : FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
                return ProductViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.rv_row, parent, false))
            }

            override fun onBindViewHolder(holder: ProductViewHolder, position: Int, model: Product) {
                var status: String? = null
                if (model.ProductState.toString() == "1"|| model.ProductState.toString() == "11") {
                    status = "มีของ"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(context!!, R.color.selected))
                } else if (model.ProductState.toString() == "2" ) {
                    status = "แลกเปลี่ยน"
                    holder.itemView.state_product.setText(status)
                    holder.itemView.state_product.setTextColor(ContextCompat.getColor(context!!, R.color.yellowApp1))
                } else if (model.ProductState.toString() == "0") {
                    status = "ไม่มีของ"
                    holder.itemView.state_product.setText(status)
                }

                holder.itemView.name_product.setText(model.ProductName)
                holder.itemView.hand_product.setText("มือ" + model.ProductHand.toString())
                holder.itemView.percent_product.setText(model.ProductQuality.toString())
                holder.itemView.time_product.setText(model.ProductTime)
                holder.itemView.state_product.setText(status)


                Picasso.with(activity).load(model.ProductImage).into(holder.itemView.img_product)

                holder.itemView.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        if (model.USerID == arguments?.getString("email")) {
                            val intent = Intent(activity, InforProductUser::class.java)

                            intent.putExtra("UserID",model.USerID)
                            intent.putExtra("Email", arguments?.getString("email"))
                            intent.putExtra("NAME", getRef(position).key)
                            intent.putExtra("PRODUCTID", model.ProductID)
                            startActivity(intent)
                        }else{


                            val intent = Intent(activity, InformationActivity::class.java)
                            intent.putExtra("NAME", getRef(position).key)
                            intent.putExtra("Owner_EMAIL",arguments?.getString("email"))

                            startActivity(intent)
                        }

                    }

                })
            }


        }
        mRecyclerView.adapter = FirebaseRecyclerAdapter

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }


    class ProductViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!){

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
                Home_Fragment().apply {
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