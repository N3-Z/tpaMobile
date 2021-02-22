package com.example.prk.santuy

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*

class ActivityImageProfile : AppCompatActivity() {
    var selectedPhotoUri: Uri? = null
    lateinit var user : User


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_image_profile)
        user = intent!!.extras.get("user") as User

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("file", "terambil " + data.data.toString())

            selectedPhotoUri = data.data
            uploadImageToFbStorage()
        }
    }
    private fun uploadImageToFbStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()+".jpg"
        val reff = FirebaseStorage.getInstance().getReference("images/$filename")


        reff.putFile(selectedPhotoUri!!)
                .addOnFailureListener {
                    Log.d("SelectedFile", it.localizedMessage)
                }.addOnSuccessListener {
                    Log.d("SelectedFile", "Success: ${it.metadata?.path}")
                    var url = it.downloadUrl.toString()

                    FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.userID)
                            .update(mapOf(
                                    "image" to url
                            )).addOnCompleteListener {
                                Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
                                user.image = url
                                val intent = Intent(this, MainActivity::class.java)

                                intent.putExtra("user", user)
                                startActivity(intent)

                                finish()
                            }
                }
    }

}
