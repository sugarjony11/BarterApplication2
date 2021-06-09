package com.example.BarterApp.bottomnavibar.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.BarterApp.R
import com.example.BarterApp.adapter.SectionPageAdt
import com.google.android.material.tabs.TabLayout


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class Noti_Fragment : Fragment() {

    var myFragment: View?=null
    var viewpager: ViewPager?=null
    var tabLayout:TabLayout?=null


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
        // Inflate the layout for this fragment

        myFragment =  inflater.inflate(R.layout.fragment_noti, container, false)

        viewpager = myFragment?.findViewById(R.id.viewPager)
        tabLayout = myFragment?.findViewById(R.id.tabLaout)

        return myFragment

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewpager?.let { setUpViewPage(it) }
        tabLayout?.setupWithViewPager(viewpager)


        tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })


    }

    private fun setUpViewPage(view: ViewPager){
        var adapter= SectionPageAdt(childFragmentManager,arguments?.getString("email")!!)
        adapter.addFragment(incomeOffer_fragment(),"ข้อเสนอที่เข้ามา")
        adapter.addFragment(outRequest_Fragment(),"ข้อเสนอที่ยื่นไป")


        view.adapter = adapter
    }


//    class  OutTrading: Fragment(){
//        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//            super.onViewCreated(view, savedInstanceState)
//            val outFrag = Noti_Fragment()
//            Noti_Fragment(outFrag,R.id.frame_layout)
//        }
//    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Noti_Fragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

    }


}


