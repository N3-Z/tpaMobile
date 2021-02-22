package com.example.prk.santuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    var database : FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_register)

        rRegisterBtn.setOnClickListener {
            insertUser(database)
        }

        go_to_login.setOnClickListener{
            startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
            finish()
        }
    }

    private fun insertUser(database: FirebaseFirestore) {

        val dialog = ProgressDialog(this)
        dialog.setMessage("Please wait ...")

        dialog.show()

        if (rUsernameTxt.text.toString().isEmpty()) {
            rUsernameTxt.error = "Please enter your username"
            rUsernameTxt.requestFocus()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(rEmailTxt.text.toString()).matches()) {
            rEmailTxt.error = "Please enter your valid email"
            rEmailTxt.requestFocus()
            return
        }

        if (rPasswordTxt.text.toString().isEmpty()) {
            rPasswordTxt.error = "Please enter your password"
            rPasswordTxt.requestFocus()
            return
        }

        if (!rPasswordTxt.text.toString().equals(rCPasswordTxt.text.toString())) {
            rCPasswordTxt.error = "Password not match"
            rCPasswordTxt.requestFocus()
            return
        }

        val user = User(
                 "",
                rUsernameTxt.text.toString(),
                rEmailTxt.text.toString(),
                rPasswordTxt.text.toString(),
                "https://firebasestorage.googleapis.com/v0/b/santuy-451fc.appspot.com/o/256-512.png?alt=media&token=4f4ecdc6-9352-438c-b918-bbc1fd548c69",
                "",
                "0",
                ArrayList()
        )
        //USERNAME NGGA BOLEH SAMA
        database.collection("users").whereEqualTo("username", user.username).get()
                .addOnSuccessListener {
                    documents ->
                    dialog.dismiss()
                    if(documents.isEmpty){
                            database.collection("users").add(user).addOnCompleteListener{
                                task ->
                                if(!task.result.id.isEmpty()){
                                    database.collection("users")
                                            .document(task.result.id)
                                            .update("userID",task.result.id ).addOnCompleteListener {

                                                Toast.makeText(this@RegisterActivity, "Sign Up Success", Toast.LENGTH_SHORT).show()
                                                startActivity(Intent(this@RegisterActivity,LoginActivity::class.java))
                                                finish()

                                            }
                                }
                            }

                        }else{
                            rUsernameTxt.error = "Username Already Exist"
                            rUsernameTxt.requestFocus()
                        }

        }



    }

}
