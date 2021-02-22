package com.example.prk.santuy

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_profile)
        val user = intent.extras!!.get("user") as User

        val imageUri = user.image
        val userImage : ImageView = findViewById(R.id.userImg)
        Picasso.with(this).load(imageUri).into(userImage)

        var ProfileUsername : TextView = findViewById(R.id.pUsername)
        var ProfileCoins : TextView = findViewById(R.id.pUserCoin)
        var ProfileStatus : TextView = findViewById(R.id.pUserStatus)

        ProfileUsername.text = user.username
        ProfileCoins.text = user.coins
        ProfileStatus.text = user.status

        cancel_profile.setOnClickListener {
            val intent =  Intent(this@ProfileActivity, MainActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            finish()
        }

    }
}
