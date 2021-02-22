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
import com.example.prk.santuy.models.Reward
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.io.Serializable
import android.content.DialogInterface
import android.os.Bundle
import com.example.prk.santuy.*


class RewardAdapther(val rewardList: ArrayList<Reward> ,val MainContext: Context, val user: User): RecyclerView.Adapter<RewardAdapther.ViewHolder>(), Serializable {

    private val activity : MainActivity = MainContext as MainActivity

    lateinit var rewardAdapther: RewardAdapther
    lateinit var boughtRewardAdapther: BoughtRewardAdapter

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.reward_list, parent, false)
        return ViewHolder(v);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.rewardTitle?.text = rewardList[position].title
        holder.rewardNotes?.text = rewardList[position].notes
        holder.rewardCost?.text = rewardList[position].cost
        holder.buyButton.setOnClickListener{

            val confirm_buy_dialog = AlertDialog.Builder(MainContext)
            val inflater = LayoutInflater.from(MainContext)
            val dialog_view = inflater.inflate(R.layout.confirm_buy_dialog, null)
            confirm_buy_dialog.setView(dialog_view)
            confirm_buy_dialog.setCancelable(true)

            confirm_buy_dialog.setPositiveButton("Buy", DialogInterface.OnClickListener { dialog, which ->

                val rewardCost = rewardList[position].cost.toInt()
                val userCoins = user.coins.toInt()
                if (userCoins >= rewardCost) {
                    val dialog = ProgressDialog(MainContext)
                    dialog.setMessage("Please wait ...")

                    dialog.show()
                    val newCoins =(userCoins - rewardCost).toString()
                    user.coins = newCoins
                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.userID)
                            .update(mapOf("coins" to newCoins)  )
                            .addOnCompleteListener {
                                FirebaseFirestore.getInstance()
                                        .collection("rewards")
                                        .document(
                                                rewardList[position].rewardID)
                                        .update("buy", true)
                                        .addOnCompleteListener{

                                            dialog.dismiss()
                                            Toast.makeText(MainContext, "Congratulations! Here is your Reward!", Toast.LENGTH_SHORT ).show()

                                            val boughtRewardList = ArrayList<Reward>()
                                            val bundle = Bundle()
                                            val rf = RewardContainer()
                                            val ft = activity.supportFragmentManager.beginTransaction()

                                            boughtRewardAdapther = BoughtRewardAdapter(boughtRewardList, MainContext, user)
                                            rewardList.clear()

                                            FirebaseFirestore.getInstance().collection("rewards").whereEqualTo("userID", user.userID).get().addOnCompleteListener { documents ->
                                                if (!documents.result.isEmpty) {
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

                                                    rewardAdapther = RewardAdapther(rewardList, MainContext, user)
                                                    boughtRewardAdapther = BoughtRewardAdapter(boughtRewardList, MainContext, user)

                                                    bundle.putSerializable("user", user)
                                                    bundle.putSerializable("reward", rewardAdapther)
                                                    bundle.putSerializable("bought_reward", boughtRewardAdapther)

                                                    rf.arguments = bundle

                                                    ft.replace(R.id.container, rf)
                                                    ft.commit()

                                                }
                                            }


                                        }

                            }
                } else {
                    Toast.makeText(MainContext, "Your Coins isn't enough", Toast.LENGTH_SHORT ).show()
                }
                dialog.dismiss()
            })


            confirm_buy_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
                {dialog, which -> dialog.dismiss() }
            )

            confirm_buy_dialog.show()

        }
        holder.rewardCard.setOnClickListener{
            val dialog = ProgressDialog(MainContext)
            dialog.setMessage("Please wait ...")

            dialog.show()
            FirebaseFirestore.getInstance().collection("rewards")
                    .whereEqualTo("rewardID",rewardList[position].rewardID)
                    .get().addOnCompleteListener { tasks ->
                            dialog.dismiss()

                            if(!tasks.result.isEmpty){
                                val r  = Reward(
                                        tasks.result.first().getString("rewardID"),
                                        tasks.result.first().getString("userID"),
                                        tasks.result.first().getString("title"),
                                        tasks.result.first().getString("notes"),
                                        tasks.result.first().getString("cost")
                                )

                                val intent: Intent = Intent(activity, ManageRewardsActivity::class.java)
                                intent.putExtra("reward", r )
                                intent.putExtra("user", user)

                                activity.startActivity(intent)
                                activity.finishMainActivity()
                            }

                    }

        }

    }
    override fun getItemCount(): Int {
        return rewardList.size
    }

    class ViewHolder(rewardView: View): RecyclerView.ViewHolder(rewardView){
        val rewardTitle = rewardView.findViewById<TextView>(R.id.rewardTitle)
        val rewardNotes = rewardView.findViewById<TextView>(R.id.rewardNotes)
        val rewardCost = rewardView.findViewById<TextView>(R.id.rewardCost)
        val buyButton = rewardView.findViewById<Button>(R.id.buyButton)
        val rewardCard = rewardView.findViewById<CardView>(R.id.rewardID)
    }
}