
package com.example.prk.santuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Reward
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manage_reward.*

class ManageRewardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_manage_reward)

        val user = intent.extras!!.get("user") as User


        insertRewardBtn.setOnClickListener{
            insertReward(user, intent)
        }

        cinsertRewardBtn.setOnClickListener{
            val intent =  Intent(this@ManageRewardActivity, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }

    }

    private fun insertReward(user: User, intent: Intent) {
        val newRT = newRtitle.text.toString()
        val newRN = newRnotes.text.toString()
        val newRC = newRcost.text.toString()
        val newR = Reward("null",user.userID, newRT, newRN, newRC, false)

        if (newRT.isEmpty()) {
            newRtitle.error = "Please enter your Reward title"
            newRtitle.requestFocus()
            return
        }

        if (newRN.isEmpty()) {
            newRnotes.error = "Please enter your Reward Notes"
            newRnotes.requestFocus()
            return
        }

        if (newRC.isEmpty()) {
            newRcost.error = "Please enter your Reward Cost"
            newRcost.requestFocus()
            return
        }



        FirebaseFirestore.getInstance().collection("rewards").add(newR).addOnCompleteListener{
            task ->
            if(!task.result.id.isEmpty()){
                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                dialog.show()
                FirebaseFirestore.getInstance().collection("rewards")
                        .document(task.result.id)
                        .update("rewardID", task.result.id).addOnCompleteListener{

                    Toast.makeText(this@ManageRewardActivity, "Success Create Reward", Toast.LENGTH_SHORT).show()
                    val intent =  Intent(this@ManageRewardActivity, MainActivity::class.java)
                    intent.putExtra("user", user)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()

                }
            }
        }


    }

}
