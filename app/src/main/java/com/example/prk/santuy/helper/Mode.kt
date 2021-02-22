package com.example.prk.santuy.helper

import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.prk.santuy.R

class Mode {
    companion object{
        fun setTheme_(gas : AppCompatActivity){
            var sharePref = PreferenceManager.getDefaultSharedPreferences(gas)
            val mode = sharePref.getInt("mode",1)
            Log.d("ahahaha",mode.toString())
            if(mode == 2){
                gas.setTheme(R.style.darkmode)
            }
            else{
                gas.setTheme(R.style.AppTheme)
            }

        }

    }
}