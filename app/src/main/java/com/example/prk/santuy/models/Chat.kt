package com.example.prk.santuy.models

import java.io.Serializable

class Chat constructor(
        var name : String = "",
        var message : String  = "",
        var time : String = "",
        var date : String = ""
): Serializable