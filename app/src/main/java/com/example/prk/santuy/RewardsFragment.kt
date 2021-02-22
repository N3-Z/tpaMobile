package com.example.prk.santuy
import com.example.prk.santuy.R
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.prk.santuy.adapther.RewardAdapther
import com.example.prk.santuy.models.User
import kotlinx.android.synthetic.main.fragment_rewards.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RewardsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_rewards, container, false)


        val user : User = this.arguments?.getSerializable("user") as User

        val rewardView   : RecyclerView = view.findViewById(R.id.reward_list)
        rewardView.setHasFixedSize(true)
        rewardView.layoutManager = GridLayoutManager(context,3)
        val rewardAdapter = this.arguments?.getSerializable("reward") as RewardAdapther
        rewardView.adapter = rewardAdapter

        return view
    }


}
