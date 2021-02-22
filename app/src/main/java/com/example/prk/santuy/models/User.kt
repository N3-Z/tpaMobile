package com.example.prk.santuy.models

import java.io.Serializable

class User(
        var userID:String,
        var username:String? = "",
        var email:String? = "",
        var password:String? = "",
        var image:String = "",
        var status:String? = "",
        var coins:String = "0",
        var partner: ArrayList<String>
) : Serializable
