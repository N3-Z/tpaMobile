package com.example.prk.santuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import kotlinx.android.synthetic.main.activity_not_found_parter.*

class NotFoundParterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_not_found_parter)
        var user = intent.extras!!.get("user") as User
        cNotFoundQRBtn.setOnClickListener{
            val intent =  Intent(this@NotFoundParterActivity, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }
    }
}
