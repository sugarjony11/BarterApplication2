package com.example.BarterApp.bottomnavibar.Fragment

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.BarterApp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_user.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [User_Fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class User_Fragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private  val imgRef = Firebase.storage.reference
private     val db = FirebaseFirestore.getInstance()




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
        // Inflate the layout for this fragment


        val view = inflater.inflate(R.layout.fragment_user, container, false)
        /*********************************************************/
//        val pic = arguments?.getString("img")
//        val email = arguments?.getString("email")
/*************************โหลดรูป**********************************/


val bytearray = arguments?.getByteArray("image")
        val bmp = BitmapFactory.decodeByteArray(bytearray,0,bytearray!!.size)

        view.pic_profile.setImageBitmap(bmp)


/*************************โหลดรูป**********************************/



//
      view.nameBar.text  = arguments?.getString("name")
       view.addressText.text = arguments?.getString("address")
        view.count_tread.text = arguments?.getString("tradecount")
        view.count_item.text = arguments?.getString("itemcount")
        view.count_comment.text = arguments?.getString("score")




        return view
    }




    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment User_Fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                User_Fragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}


