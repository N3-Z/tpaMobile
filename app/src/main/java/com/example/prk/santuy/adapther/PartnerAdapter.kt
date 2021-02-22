package com.example.prk.santuy.adapther

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.example.prk.santuy.MainActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable

class PartnerAdapter(val list:ArrayList<String>, val ctx : Context): RecyclerView.Adapter<PartnerAdapter.ViewHolder>(), Serializable {



    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_partner_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.partnerCard.visibility = View.GONE
        holder.partnerProgress.visibility = View.VISIBLE

        FirebaseFirestore.getInstance().collection("users")
                .whereEqualTo("userID", list[position])
                .get().addOnCompleteListener{task ->
                    if(!task.result.isEmpty){
                        var user = User(task.result.first().getString("userID"),
                                task.result.first().getString("username"),
                                "",
                                "",
                                task.result.first().getString("image"),
                                task.result.first().getString("status"), "",
                                task.result.first().get("partner") as ArrayList<String>)
                        holder.partner_username?.text = user.username
                        Picasso.with(ctx).load(user.image).into(holder.parnerimage)
                        if(user.status!!.isEmpty()){
                            holder.partner_status?.text = "no status"
                        }else{
                            holder.partner_status?.text = user.status
                        }
                    }
                    holder.partnerCard.visibility = View.VISIBLE
                    holder.partnerProgress.visibility = View.GONE
                }
        Log.d("partnercuyehe", list[position])
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val partner_username = view.findViewById<TextView>(R.id.partner_username_)
        val partner_status = view.findViewById<TextView>(R.id.partner_status_)
        val parnerimage = view.findViewById<CircleImageView>(R.id.parnerimage)
        val partnerCard = view.findViewById<LinearLayout>(R.id.partner_card)
        val partnerProgress = view.findViewById<ProgressBar>(R.id.partner_progress)

    }

}


