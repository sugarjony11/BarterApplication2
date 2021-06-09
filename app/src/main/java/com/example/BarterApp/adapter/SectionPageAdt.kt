package com.example.BarterApp.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPageAdt(fm: FragmentManager,email : String) : FragmentPagerAdapter(fm) {

    val fragmentList = arrayListOf<Fragment>()
    val titleList = arrayListOf<String>()
    val bundle = Bundle()
    val email = email
    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getItem(position: Int): Fragment {
        bundle.putString("email",email)
        fragmentList.get(position).arguments = bundle
        return fragmentList.get(position)

    }

    override fun getPageTitle(position: Int):CharSequence {
        return titleList.get(position)
    }

    fun addFragment(fragment: Fragment,title: String){
        fragmentList.add(fragment)
        titleList.add(title)
    }

}