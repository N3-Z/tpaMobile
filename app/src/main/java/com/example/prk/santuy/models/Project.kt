package com.example.prk.santuy.models

import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class Project constructor(
    var projectID : String ="",
    var title : String = "",
    var notes : String = "",
    var difficulty : String = "",
    var deadline : Date,
    var last_chat : String = "",
    var lastdate : Date,
    var userID : ArrayList<String>

) : Serializable