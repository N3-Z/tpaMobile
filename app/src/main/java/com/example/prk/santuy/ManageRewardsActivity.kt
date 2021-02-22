package com.example.prk.santuy
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.example.prk.santuy.models.Reward
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manage_reward.*
import kotlinx.android.synthetic.main.activity_manage_rewards.*

class ManageRewardsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_rewards)

        val user = intent.extras!!.get("user") as User
        val reward = intent.extras!!.get("reward") as Reward
        val rewardID  : String = reward.rewardID

        val intent =  Intent(this@ManageRewardsActivity, MainActivity::class.java)
        intent.putExtra("user", user)

        uRtitle.setText(reward.title)
        uRnotes.setText(reward.notes)
        uRcost.setText(reward.cost)

        uRewardBtn.setOnClickListener{

            updateReward(rewardID)

        }

        dRewardBtn.setOnClickListener{

            val confirm_dialog = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialog_view = inflater.inflate(R.layout.delete_dialog, null)
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)


            confirm_dialog.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->

                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                dialog.show()

                FirebaseFirestore.getInstance()
                        .collection("rewards")
                        .document(rewardID)
                        .delete().addOnCompleteListener {
                            dialog.dismiss()
                            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
                            startActivity(intent.putExtra("fragment", 3))
                            finish()
                        }

            })

            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            {dialog, which -> dialog.dismiss() }
            )

            confirm_dialog.show()

        }

        cuRewardBtn.setOnClickListener{
            startActivity(intent)
            finish()
        }
    }

    fun updateReward(rewardID: String){
        val urt: String = uRtitle.text.toString()
        val urn: String = uRnotes.text.toString()
        val urc: String = uRcost.text.toString()

        if (urt.isEmpty()) {
            newRtitle.error = "Please enter your Reward title"
            newRtitle.requestFocus()
            return
        }

        if (urn.isEmpty()) {
            newRnotes.error = "Please enter your Reward Notes"
            newRnotes.requestFocus()
            return
        }

        if (urc.isEmpty()) {
            newRcost.error = "Please enter your Reward Cost"
            newRcost.requestFocus()
            return
        }


        val confirm_dialog = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        val dialog_view = inflater.inflate(R.layout.update_dialog, null)
        confirm_dialog.setView(dialog_view)
        confirm_dialog.setCancelable(true)


        confirm_dialog.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->

            val dialog = ProgressDialog(this)
            dialog.setMessage("Please wait ...")

            dialog.show()

            FirebaseFirestore.getInstance()
                    .collection("rewards")
                    .document(rewardID)
                    .update(mapOf(
                            "title" to urt,
                            "notes" to urn,
                            "cost" to urc
                    )).addOnCompleteListener {
                        dialog.dismiss()
                        Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()

                        startActivity(intent)
                        finish()
                    }
        })
        confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
        {dialog, which -> dialog.dismiss() }
        )

        confirm_dialog.show()
    }

}
