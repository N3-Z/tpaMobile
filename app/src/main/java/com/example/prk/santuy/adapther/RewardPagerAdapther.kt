package com.example.prk.santuy.adapther

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.example.prk.santuy.BoughtRewards
import com.example.prk.santuy.RewardsFragment


class RewardPagerAdapther(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val fragmentList : MutableList<Fragment> = ArrayList()
    private val titleList : MutableList<String> = ArrayList()

    override fun getItem(position: Int): Fragment? {

        return fragmentList[position] //does not happen
    }

    override fun getCount(): Int {
        return fragmentList.size //three fragments
    }

    fun addFragment(f : Fragment, title : String){
        fragmentList.add(f)
        titleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }
}