package com.example.prk.santuy.adapther

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.prk.santuy.MainActivity
import com.example.prk.santuy.ManageRewardsActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.models.Reward
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.Serializable
import android.content.DialogInterface
import android.os.Bundle
import com.example.prk.santuy.RewardContainer
import kotlinx.android.synthetic.main.fragment_partners.view.*


class BoughtRewardAdapter(val rewardList: ArrayList<Reward> ,val MainContext: Context, val user: User): RecyclerView.Adapter<BoughtRewardAdapter.ViewHolder>(), Serializable {

    private val activity : MainActivity = MainContext as MainActivity

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.bought_reward_list, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.rewardTitle?.text = rewardList[position].title
        holder.rewardNotes?.text = rewardList[position].notes
        holder.rewardCost?.text = rewardList[position].cost

        holder.boughtRewardButton.setOnClickListener{

            val confirm_dialog = AlertDialog.Builder(MainContext)
            val inflater = LayoutInflater.from(MainContext)
            val dialog_view = inflater.inflate(R.layout.delete_dialog, null)
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)

            confirm_dialog.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->
                val dialog = ProgressDialog(MainContext)
                dialog.setMessage("Please wait ...")
                dialog.show()

                    FirebaseFirestore.getInstance()
                            .collection("rewards")
                            .document(rewardList[position].rewardID)
                            .delete().addOnCompleteListener {


                                val boughtRewardList = ArrayList<Reward>()
                                val rewardList = ArrayList<Reward>()

                                val bundle = Bundle()
                                val rf = RewardContainer()
                                val ft = activity.supportFragmentManager.beginTransaction()

                                FirebaseFirestore.getInstance().collection("rewards").whereEqualTo("userID", user.userID).get().addOnCompleteListener { documents ->
                                        for (task in documents.result) {
                                            val t = Reward(
                                                    task.getString("rewardID"),
                                                    task.getString("userID"),
                                                    task.getString("title"),
                                                    task.getString("notes"),
                                                    task.getString("cost")
                                            )
                                            if (task.getBoolean("buy")) {
                                                boughtRewardList.add(t)
                                            } else {
                                                rewardList.add(t)
                                            }
                                        }

                                        val rewardAdapther = RewardAdapther(rewardList, MainContext, user)
                                        val boughtRewardAdapther = BoughtRewardAdapter(boughtRewardList, MainContext, user)

                                        bundle.putSerializable("user", user)
                                        bundle.putSerializable("reward", rewardAdapther)
                                        bundle.putSerializable("bought_reward", boughtRewardAdapther)
                                        bundle.putInt("tabLayout", 1)


                                        dialog.dismiss()
                                        Toast.makeText(activity, "Delete Success", Toast.LENGTH_SHORT).show()


                                        rf.arguments = bundle

                                        ft.replace(R.id.container, rf)
                                        ft.commit()

                                }


                            }

            })

            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            {dialog, which -> dialog.dismiss() }
            )

            confirm_dialog.show()

        }


    }
    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewHolder(rewardView: View): RecyclerView.ViewHolder(rewardView){
        val rewardTitle = rewardView.findViewById<TextView>(R.id.bought_reward_Title)
        val rewardNotes = rewardView.findViewById<TextView>(R.id.bought_reward_Notes)
        val rewardCost = rewardView.findViewById<TextView>(R.id.bought_reward_Cost)
        val boughtRewardButton = rewardView.findViewById<Button>(R.id.bought_delete_Button)
    }

}