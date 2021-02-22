package com.example.prk.santuy

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_NO
import android.support.v7.app.AppCompatDelegate.MODE_NIGHT_YES
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.lang.Exception
import java.util.*


class AccountFragment : Fragment() {
    lateinit var myswitch: Switch
    lateinit var contextThemeWrapper: ContextThemeWrapper
    var selectedPhotoUri: Uri? = null
    var path = ""
    lateinit var updatebtn: Button
    lateinit var edit_profile_image: CircleImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val sharePref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val view = inflater.inflate(R.layout.fragment_account, container,false);

        if (AppCompatDelegate.getDefaultNightMode() == MODE_NIGHT_YES) {
            sharePref.edit().putInt("mode", MODE_NIGHT_YES).commit()
        } else {
            sharePref.edit().putInt("mode", MODE_NIGHT_NO).commit()
        }
        val mode = sharePref.getInt("mode",1)
        Log.d("ahahaha",mode.toString())


        var user: User = this.arguments?.getSerializable("user") as User

        myswitch = view.findViewById<Switch>(R.id.myswitch)


        if(mode == 2){
            myswitch.isChecked = true
        }
        else{
            myswitch.isChecked = false
        }
        myswitch.setOnCheckedChangeListener { buttonView, isChecked ->
            val confirm_dialog = AlertDialog.Builder(activity)
            val inflater = LayoutInflater.from(activity)
            val dialog_view = inflater.inflate(R.layout.update_dialog, null)
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)
            confirm_dialog.setPositiveButton("Change mode", DialogInterface.OnClickListener { dialog, which ->

                val dialog = ProgressDialog(activity)
                dialog.setMessage("Please wait ...")
                dialog.show()
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    sharePref.edit().putInt("mode", MODE_NIGHT_YES).commit()
                    restartApp(user)
                } else {
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    sharePref.edit().putInt("mode", MODE_NIGHT_NO).commit()
                    restartApp(user)
                }

            })
            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            { dialog, which -> dialog.dismiss()
            }
            )


            confirm_dialog.show()



            Log.d("hahaha", MODE_NIGHT_YES.toString())
            Log.d("hahaha", MODE_NIGHT_NO.toString())
        }


        val showQRcodeBtn = view.findViewById<Button>(R.id.showQRcodebtn)
        val LogOut = view.findViewById<Button>(R.id.LogOut)


        showQRcodeBtn.setOnClickListener {
            val intent = Intent(activity, ShowQRActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }


        LogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val sharePref = PreferenceManager.getDefaultSharedPreferences(requireActivity())
            sharePref.edit().putString("userid", "").commit()
            sharePref.edit().putString("username", "").commit()

            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        edit_profile_image = view.findViewById<CircleImageView>(R.id.edit_profile_image)
        var edit_profile_email = view.findViewById<EditText>(R.id.edit_profile_email)
        var edit_profile_username = view.findViewById<EditText>(R.id.edit_profile_username)
        var edit_profile_status = view.findViewById<EditText>(R.id.edit_profile_status)
        var edit_profile_password = view.findViewById<EditText>(R.id.edit_profile_password)
        updatebtn = view.findViewById(R.id.edit_profile_updtbtn)

        edit_profile_email.setText(user.email)
        edit_profile_username.setText(user.username)
        edit_profile_status.setText(user.status)
        edit_profile_password.setText(user.password)



        Picasso.with(requireActivity()).load(user.image).into(edit_profile_image)
        edit_profile_image.setOnClickListener {
            val intent = Intent(activity, ActivityImageProfile::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }

        updatebtn.setOnClickListener {
            val confirm_dialog = AlertDialog.Builder(activity)
            val inflater = LayoutInflater.from(activity)
            val dialog_view = inflater.inflate(R.layout.update_dialog, null)
            var username = edit_profile_username.text.toString()
            var email = edit_profile_email.text.toString()
            var status = edit_profile_status.text.toString()
            var password = edit_profile_password.text.toString()
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)
            confirm_dialog.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->

                val dialog = ProgressDialog(activity)
                dialog.setMessage("Please wait ...")

                dialog.show()

                FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.userID)
                        .update(mapOf(
                                "email" to email,
                                "username" to username,
                                "status" to status,
                                "password" to password
                        )).addOnCompleteListener {
                            dialog.dismiss()
                            Toast.makeText(activity, "Update Success", Toast.LENGTH_SHORT).show()
                            user.email = email
                            user.username = username
                            user.status = status
                            restartApp(user)
                        }

            })
            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            { dialog, which -> dialog.dismiss() }
            )


            confirm_dialog.show()
        }

        return view
    }


    private fun restartApp(user: User) {
        val intent = Intent(activity, MainActivity::class.java)

        intent.putExtra("user", user)
        startActivity(intent)
        activity?.finish()
    }

}
