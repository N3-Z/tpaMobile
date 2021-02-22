package com.example.prk.santuy.helper

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class MyFirebaseInstanceService : FirebaseInstanceIdService(){

    override fun onTokenRefresh() {
        val token = FirebaseInstanceId.getInstance().token
        Log.d("TOKEN_", token + " ")
    }
    companion object{
        fun addTokenFireStore(newRegistration : String?){
            if(newRegistration == null) throw NullPointerException("FCM token is null")
        }
    }



}