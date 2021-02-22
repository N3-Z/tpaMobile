package com.example.prk.santuy

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.prk.santuy.adapther.BoughtRewardAdapter
import com.example.prk.santuy.models.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [BoughtRewards.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [BoughtRewards.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class BoughtRewards : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_bought_rewards, container, false)

        val user : User = this.arguments?.getSerializable("user") as User
        val boughtRewardAdapther = this.arguments?.getSerializable("bought_reward") as BoughtRewardAdapter

        if(boughtRewardAdapther.itemCount<=0){
            view = LayoutInflater.from(activity).inflate(R.layout.empty_bought_reward,null)
        }
        else{
            val rewardView   : RecyclerView = view.findViewById(R.id.bought_reward_list)
            rewardView.setHasFixedSize(true)
            rewardView.layoutManager = GridLayoutManager(context,3)
            rewardView.adapter = boughtRewardAdapther
        }

        return view
    }



}
