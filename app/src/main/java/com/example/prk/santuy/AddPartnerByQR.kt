package com.example.prk.santuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_add_partner_by_qr.*

class AddPartnerByQR : AppCompatActivity() {

    lateinit var insertPartner: Button
    lateinit var alreadyPartner: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_add_partner_by_qr)

        var user = intent.extras!!.get("user") as User
        var partner = intent.extras!!.get("partner") as User
        val imageUri = partner.image
        val partnerImage : ImageView = findViewById(R.id.qrpartnerImg)
        Picasso.with(this).load(imageUri).into(partnerImage)

        val ProfileUsername : TextView = findViewById(R.id.qrpartner_name)
        ProfileUsername.text = partner.username

        insertPartner = findViewById(R.id.qrinsertPartner)
        alreadyPartner = findViewById(R.id.qr_already_partner)
        alreadyPartner.visibility = View.GONE



        for (p in user.partner ){
            if (partner.userID.equals(p)){

                alreadyPartner.visibility = View.VISIBLE
                insertPartner.visibility = View.GONE

                break
            }
        }

        insertPartner.setOnClickListener {

            addNewPartner(user,partner )

        }

        cAddQRBtn.setOnClickListener{
            val intent =  Intent(this@AddPartnerByQR, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }

    }

    private fun addNewPartner( user : User, partner : User){

        if(user.userID.equals(partner.userID)){
            Toast.makeText(this, "You can't add Yourself :)", Toast.LENGTH_SHORT).show()
            return
        }

        user.partner.add(partner.userID)
        partner.partner.add(user.userID)

        FirebaseFirestore.getInstance()
                .collection("users").document(user.userID).update("partner", user.partner).addOnCompleteListener{
                    FirebaseFirestore.getInstance()
                            .collection("users").document(partner.userID).update("partner", partner.partner).addOnCompleteListener{
                                Toast.makeText(this, "Success Add Partner", Toast.LENGTH_SHORT).show()
                                alreadyPartner.visibility = View.VISIBLE
                                insertPartner.visibility = View.GONE
                            }
                }

    }

}
