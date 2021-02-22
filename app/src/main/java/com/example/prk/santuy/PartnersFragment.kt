package com.example.prk.santuy


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.prk.santuy.adapther.PartnerAdapter
import com.example.prk.santuy.models.User
import com.squareup.picasso.Picasso


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("ValidFragment")
/**
 * A simple [Fragment] subclass.
 *
 */
class PartnersFragment() : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view = inflater.inflate(R.layout.fragment_partners, container,false)
        val user : User = this.arguments?.getSerializable("user") as User
        val task_priority = this.arguments?.getString("task_priority")

        val usernameTxt = view.findViewById<TextView>(R.id.user_id)
        val usercoinTxt = view.findViewById<TextView>(R.id.user_coin)
        val userDeadlineTxt = view.findViewById<TextView>(R.id.user_priority_task)

        usernameTxt.text = user.username
        usercoinTxt.text = "Coin: "+user.coins
        userDeadlineTxt.text = task_priority
        Log.d("INI asdfasdf", task_priority.toString() )


        val imageUri = user.image
        val userImage : ImageView = view.findViewById(R.id.userImg)
        Picasso.with(requireActivity()).load(imageUri).into(userImage)

        val gotoAddPartner = view.findViewById<ImageButton>(R.id.go_to_add_partner)
        val gotoProfile = view.findViewById<ConstraintLayout>(R.id.gotoProfileBtn)

        gotoAddPartner.setOnClickListener {
            val intent =  Intent(activity, AddPartnerActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }

        gotoProfile.setOnClickListener{
            val intent =  Intent(activity, ProfileActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }


        var partnerAdapter = this.arguments?.getSerializable("partner") as PartnerAdapter
        var partnerView : RecyclerView = view.findViewById(R.id.partner_list_)
        partnerView.setHasFixedSize(true)
        partnerView.layoutManager = LinearLayoutManager(context)


        Log.d("partnercuysize", partnerAdapter.itemCount.toString())
        partnerView.adapter = partnerAdapter


        return view
    }


}
