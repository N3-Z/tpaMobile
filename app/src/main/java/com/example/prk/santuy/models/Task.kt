package com.example.prk.santuy.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
class Task constructor(
        var taskID : String,
        var userID :String,
        var title:String = "",
        var difficulty:String = "",
        var deadline:Date,
        var notes:String = ""
) : Parcelable

