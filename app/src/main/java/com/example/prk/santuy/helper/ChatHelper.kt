package com.example.prk.santuy.helper

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.support.v4.content.ContextCompat.getSystemService
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.example.prk.santuy.ChatActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.adapther.ChatAdapter
import com.example.prk.santuy.models.Chat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatHelper {
    companion object{
        var idx = 0
    }

    lateinit var notificationChannel : NotificationChannel
    lateinit var builder : Notification.Builder
    private val channelId = "com.example.prk.santuy.helper"
    private val description = "Test notification"


    fun SaveMessageInfoToDatabase(groupChat:DatabaseReference, username:String,msg:String, msgKey:String, ctx:Context) {

//        if(TextUtils.isEmpty(msg)){
//            Toast.makeText(ctx, "Please write message first..", Toast.LENGTH_LONG).show()
//        }
//        else{
            var calforDate = Calendar.getInstance()
            var curDateFormat = SimpleDateFormat("MMM dd, yyyy")
            var currDate = curDateFormat.format(calforDate.time)

            var calfortime = Calendar.getInstance()
            var curTimeFormat = SimpleDateFormat("hh:mm a")
            var currTime = curTimeFormat.format(calfortime.time)

            var groupMessageKey = HashMap<String, Object>()
            groupChat.updateChildren(groupMessageKey as Map<String, Object>?)

            var groupMessageKeyRef = groupChat.child(msgKey)
            var messageInfoMap = HashMap<Any, Any>()
            messageInfoMap.put("name", username)
            messageInfoMap.put("message", msg)
            messageInfoMap.put("date", currDate)
            messageInfoMap.put("time", currTime)
            groupMessageKeyRef.setValue(messageInfoMap as Map<String, Object>?)
//        }
    }





    fun ShowNotification(context: Context, notificationManager : NotificationManager, title : String, msg : String){
        val intent = Intent(context,ChatActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(channelId, description,NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(context, channelId)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
        }else {
            builder = Notification.Builder(context)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
        }

        notificationManager.notify(1234,builder.build())

    }

}