package com.example.prk.santuy

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_partner_by_id.*
import java.util.*
import kotlin.collections.ArrayList

class AddPartnerByID : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_add_partner_by_id)

        val user = intent.extras!!.get("user") as User
        val bundle = Bundle()

        if(savedInstanceState == null){
            val fragment = BlankFragment()
            supportFragmentManager.beginTransaction().replace(R.id.partner_container, fragment, fragment.javaClass.simpleName).commit()
        }

        search_partner.setOnClickListener {
            val dialog = ProgressDialog(this)
            dialog.setMessage("Please wait ...")

            dialog.show()
            FirebaseFirestore.getInstance()
                    .collection("users").whereEqualTo("username", partner_ID.text.toString()).get()
                    .addOnCompleteListener {
                        task ->
                        dialog.dismiss()
                        if (!task.result.isEmpty){
                            val partners = task.result.first().data.get("partner") as ArrayList<String>

                            for (p in partners){
                                Log.d("INI", p.toString())
                            }

                            val partner = User(
                                    task.result.first().getString("userID"),
                                    task.result.first().getString("username"),
                                    task.result.first().getString("email"),
                                    task.result.first().getString("password"),
                                    task.result.first().getString("image"),
                                    task.result.first().getString("status"),
                                    task.result.first().getString("coins"),
                                    partners
                            )

                            val fragment = FindPartner()
                            bundle.putSerializable("user", user)
                            bundle.putSerializable("partner", partner)
                            bundle.putSerializable("lalala", "AddPartner")
                            bundle.putSerializable("project", Project("","","","", Date(),"", Date(), ArrayList()))
                            fragment.arguments = bundle
                            supportFragmentManager.beginTransaction().replace(R.id.partner_container, fragment, fragment.javaClass.simpleName).commit()

                        }
                        else{
                            val fragment = NotFoundPartner()
                            supportFragmentManager.beginTransaction().replace(R.id.partner_container, fragment, fragment.javaClass.simpleName).commit()
                        }
                    }
        }

        cancel_partner.setOnClickListener{
            val intent =  Intent(this@AddPartnerByID, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }



    }


}
