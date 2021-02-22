package com.example.prk.santuy.adapther

import android.content.Context
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.prk.santuy.ChatActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.helper.ChatHelper
import com.example.prk.santuy.models.Chat
import java.io.Serializable
import java.text.SimpleDateFormat
import android.util.Log
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatAdapter constructor(private val listViewType: List<Int>,
                              private val listChat: List<Chat>, val ctx : Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>(){


    companion object {
        val VIEW_TYPE_MY_SELF = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
        return when(p1){
            VIEW_TYPE_MY_SELF -> {
                val view = v.inflate(R.layout.activity_layout_chat_myself, null)
                ViewHolderChatItemMySelf(view)
            }
            else -> {
                val view = v.inflate(R.layout.activity_layout_chat_user, null)
                ViewHolderChatItemUser(view)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = listChat[position]
        listViewType[position].let {
            when (it) {
                VIEW_TYPE_MY_SELF -> {
                    val viewHolderChatItemMySelf = holder as ViewHolderChatItemMySelf
                    val param = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT)
                    param.setMargins(100,15,0,0)
                    if(chat.message.startsWith("https://") && chat.message.contains(".jpg")){
                        viewHolderChatItemMySelf.imageMySelf.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                        viewHolderChatItemMySelf.imageMySelf.layoutParams.height = 300
                        Glide.with(ctx).load(chat.message).into(viewHolderChatItemMySelf.imageMySelf)
                        viewHolderChatItemMySelf.textViewMessage.layoutParams.height = 0
                    }else if(chat.message.startsWith("https://")){
                        var file = "https://firebasestorage.googleapis.com/v0/b/santuy-451fc.appspot.com/o/images%2Ffile.png?alt=media&token=a20e7638-7048-4bf0-9a06-313113a4f9fc"
                        viewHolderChatItemMySelf.imageMySelf.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                        viewHolderChatItemMySelf.imageMySelf.layoutParams.height = 300
                        Glide.with(ctx).load(file).into(viewHolderChatItemMySelf.imageMySelf)
                        viewHolderChatItemMySelf.textViewMessage.layoutParams.height = 0
                    }
                    else{
                        viewHolderChatItemMySelf.textViewMessage.text = chat.message
                    }

                    viewHolderChatItemMySelf.textViewDateTime.text = chat.time
                    viewHolderChatItemMySelf.tralala.layoutParams = param
                }
                else -> {
                    val viewHolderChatUser = holder as ViewHolderChatItemUser
                    val param = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT)
                    param.setMargins(0,15,54,0)
                    if(chat.message.startsWith("https://") && chat.message.contains(".jpg")){
                        viewHolderChatUser.imageUser.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                        viewHolderChatUser.imageUser.layoutParams.height = 300
                        Glide.with(ctx).load(chat.message).into(viewHolderChatUser.imageUser)
                        viewHolderChatUser.textViewMessageusr.layoutParams.height = 0
                    }else if(chat.message.startsWith("https://")){
                        var file = "https://firebasestorage.googleapis.com/v0/b/santuy-451fc.appspot.com/o/images%2Ffile.png?alt=media&token=a20e7638-7048-4bf0-9a06-313113a4f9fc"
                        viewHolderChatUser.imageUser.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                        viewHolderChatUser.imageUser.layoutParams.height = 300
                        Glide.with(ctx).load(file).into(viewHolderChatUser.imageUser)
                        viewHolderChatUser.textViewMessageusr.layoutParams.height = 0
                    }
                    else{
                        viewHolderChatUser.textViewMessageusr.text = chat.message
                    }


                    viewHolderChatUser.textViewDateTimeusr.text = chat.time
                    viewHolderChatUser.textUsernameusr.text = chat.name
                    viewHolderChatUser.usertralalala.layoutParams = param
                }
            }
        }
    }
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Log.d("teslalala","ualala")
//
//        var tempimg = list[position].message
//        if(username.equals(list[position].name)){
//            val param = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT)
//            param.setMargins(100,0,0,5)
//
//            holder.rightchat.layoutParams = param
//
//            holder.time_r?.text = list[position].time
//            if(list[position].message.contains("https://")){
//                holder.temp_r.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
//                holder.temp_r.layoutParams.height = 300
//                Glide.with(ctx).load(tempimg).into(holder.temp_r)
//            }else{
//                holder.msg_r?.text = list[position].message
//            }
//        }
//        else{
//            val param = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT)
//            param.setMargins(0,5,100,0)
//
//            holder.leftchat.layoutParams = param
//
//            holder.username?.text = list[position].name
//            holder.time?.text = list[position].time
//            if(list[position].message.contains("https://")){
//                holder.temp.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
//                holder.temp.layoutParams.height = 300
//                Glide.with(ctx).load(tempimg).into(holder.temp)
//            }else{
//                holder.msg?.text = list[position].message
//            }
//        }
//    }



//    override fun getItemCount(): Int {
//        if(list.size == 0 || list == null) return 0
//        return list.size
//    }
    override fun getItemCount(): Int = listChat.size

    override fun getItemViewType(position: Int): Int = listViewType[position]

//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
//        val leftchat = view.findViewById<LinearLayout>(R.id.left_chat)
//        val username = view.findViewById<TextView>(R.id.chat_username)
//        val msg = view.findViewById<TextView>(R.id.chat_msg)
//        val time = view.findViewById<TextView>(R.id.chat_time)
//        val temp = view.findViewById<ImageView>(R.id.chat_temp)
//
//        val rightchat = view.findViewById<LinearLayout>(R.id.right_chat)
//        val msg_r = view.findViewById<TextView>(R.id.chat_msg_r)
//        val time_r = view.findViewById<TextView>(R.id.chat_time_r)
//        val temp_r = view.findViewById<ImageView>(R.id.chat_temp_r)
//    }

    open inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView)


    inner class ViewHolderChatItemMySelf constructor(itemView: View) : ViewHolder(itemView) {
        val textViewDateTime: TextView = itemView.findViewById(R.id.text_view_date_time_item_layout_chat_my_self)
        val textViewMessage: TextView = itemView.findViewById(R.id.text_view_message_item_layout_chat_my_self)
        val tralala : LinearLayout = itemView.findViewById(R.id.tralala_view_myself)
        val imageMySelf : ImageView = itemView.findViewById(R.id.image_view_myself)
    }

    inner class ViewHolderChatItemUser constructor(itemView: View) : ViewHolder(itemView) {
        val textViewDateTimeusr: TextView = itemView.findViewById(R.id.text_view_date_time_item_layout_chat_user)
        val textViewMessageusr: TextView = itemView.findViewById(R.id.text_view_message_item_layout_chat_user)
        val textUsernameusr : TextView = itemView.findViewById(R.id.text_view_username)
        val usertralalala : LinearLayout = itemView.findViewById(R.id.tralala_view_user)
        val imageUser : ImageView = itemView.findViewById(R.id.image_view_user)
    }

}