package com.example.prk.santuy

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.prk.santuy.models.User
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_add_partner.*
import android.util.Log
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.google.firebase.firestore.FirebaseFirestore


class AddPartnerActivity : AppCompatActivity() {

    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_add_partner)

        user = intent.extras!!.get("user") as User


        addByID.setOnClickListener {
            val intent =  Intent(this, AddPartnerByID::class.java)
            intent.putExtra("user", user)

            startActivity(intent)
            finish()
        }

        addByQR.setOnClickListener {
            val scanner = IntentIntegrator(this)
            scanner.initiateScan()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK){
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if(result != null) {
                if(result.getContents() == null) {
                    Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("FOUND",  result.getContents())
                    FirebaseFirestore.getInstance()
                            .collection("users").whereEqualTo("username", result.getContents()).get()
                            .addOnCompleteListener { task ->
                                if (!task.result.isEmpty) {
                                    val partners = task.result.first().data.get("partner") as ArrayList<String>

                                    for (p in partners) {
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

                                    val intent = Intent(this, AddPartnerByQR::class.java)
                                    intent.putExtra("user", user)
                                    intent.putExtra("partner", partner)

                                    startActivity(intent)
                                    finish()

                                } else {
                                    val intent = Intent(this, NotFoundParterActivity::class.java)
                                    intent.putExtra("user", user)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }


}
