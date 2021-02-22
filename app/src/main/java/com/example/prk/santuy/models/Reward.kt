
package com.example.prk.santuy.models

import java.io.Serializable

class Reward constructor(
        var rewardID: String,
        var userID :String,
        var title:String = "",
        var notes:String = "",
        var cost:String = "0",
        var isBuy:Boolean = false
) : Serializable