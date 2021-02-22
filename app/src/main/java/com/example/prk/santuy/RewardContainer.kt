package com.example.prk.santuy


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prk.santuy.adapther.RewardPagerAdapther
import android.support.design.widget.TabLayout
import android.widget.Button
import android.widget.ImageButton
import com.example.prk.santuy.adapther.BoughtRewardAdapter
import com.example.prk.santuy.adapther.RewardAdapther
import com.example.prk.santuy.models.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardContainer : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_reward_container, container, false)

        val bundle = Bundle()

        val user : User = this.arguments?.getSerializable("user") as User
        val rewardAdapter = this.arguments?.getSerializable("reward") as RewardAdapther
        val boughtRewardAdapther = this.arguments?.getSerializable("bought_reward") as BoughtRewardAdapter

        bundle.putSerializable("reward", rewardAdapter)
        bundle.putSerializable("bought_reward", boughtRewardAdapther)
        bundle.putSerializable("user", user)

        val myViewPager = view.findViewById<ViewPager>(R.id.reward_pager)
        val adapter = RewardPagerAdapther(childFragmentManager)

        val rewards_fragment = RewardsFragment()
        rewards_fragment.arguments = bundle
        val bought_fragment = BoughtRewards()
        bought_fragment.arguments = bundle

        adapter.addFragment(rewards_fragment,"Rewards")
        adapter.addFragment(bought_fragment, "Boughts")

        myViewPager.adapter = adapter
        myViewPager.offscreenPageLimit = adapter.count



        if( this.arguments?.get("tabLayout") != null ){
            myViewPager.setCurrentItem(2)
        }

        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)

        tabLayout.setupWithViewPager(myViewPager)

        val gotoInsertReward = view.findViewById<ImageButton>(R.id.insertReward)

        gotoInsertReward.setOnClickListener{
            val intent =  Intent(activity, ManageRewardActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }


}
