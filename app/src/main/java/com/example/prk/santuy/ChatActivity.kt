package com.example.prk.santuy

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.prk.santuy.adapther.ChatAdapter
import com.example.prk.santuy.adapther.TaskAdapther
import com.example.prk.santuy.helper.ChatHelper
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.helper.MyFirebaseInstanceService
import com.example.prk.santuy.models.Chat
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.activity_create_project.*
import java.io.File
import java.io.FileInputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.log


class ChatActivity : AppCompatActivity() {
    var uniqid = ""
    lateinit var mauth: FirebaseAuth
    lateinit var userRef: DatabaseReference
    lateinit var groupChat: DatabaseReference
    lateinit var textMsg: TextView
    lateinit var sendBtn: ImageButton
    lateinit var userid: String
    lateinit var username: String
    lateinit var chatAdapter: ChatAdapter
    lateinit var chat_title_ : TextView
    lateinit var backtomain : ImageButton

    var chatHelper = ChatHelper()
    var chatList = ArrayList<Chat>()
    var chatUser = ArrayList<Int>()

    lateinit var uploadFile: ImageButton
    lateinit var uploadImage: ImageButton

    lateinit var infobtn: ImageButton

    var selectedPhotoUri: Uri? = null
    var path = ""

    var jfile = ""
    var msg = ""
    var status = true
//    lateinit var groupAdapter : GroupAda

    lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_chat)
        InitializeFields()

        //roomid
        var title = intent.extras!!.get("title").toString()
        var list_chat__: RecyclerView = findViewById(R.id.list_chat__)
        uploadImage = findViewById(R.id.btn_imageUpload)
        uploadFile = findViewById(R.id.btn_fileUpload)

        val project = intent.extras!!.get("project") as Project
        Log.d("projectname", project.projectID)


        uniqid = project.projectID
        val user = intent.extras!!.get("user") as User



        mauth = FirebaseAuth.getInstance()
        userRef = FirebaseDatabase.getInstance().getReference().child("users")
        groupChat = FirebaseDatabase.getInstance().getReference().child("Group").child(uniqid)

        userid = user.userID
        username = user.username.toString()

        Log.d("titleeee", title)

        val actionBar = supportActionBar
        actionBar?.hide()

        chat_title_.setText(title)

        val intent =  Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)
        backtomain.setOnClickListener {
            startActivity(intent)
            finish()
        }

        uploadFile.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, 0)
        }

        uploadImage.setOnClickListener {
            jfile = ".jpg"
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "*/*"
            startActivityForResult(intent, 0)
        }

        ChatHelper.idx = 0

        chatAdapter = ChatAdapter(chatUser,chatList,applicationContext)
        var chatRef = FirebaseDatabase.getInstance().reference.child("Group/" + uniqid)
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {

            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (p0!!.exists()) {
                    chatList = ArrayList<Chat>()

                    for (h in p0.children) {
                        var chat = h.getValue(Chat::class.java)
                        chatList.add(chat!!)
                        if(chat.name.equals(username)){
                            chatUser.add(1)
                        }else{
                            chatUser.add(2)
                        }
                    }
                    chatAdapter = ChatAdapter(chatUser,chatList,applicationContext)

                   project.last_chat = msg

                    FirebaseFirestore.getInstance()
                            .collection("projects")
                            .document(uniqid)
                            .update(mapOf(
                                    "last_chat" to msg
                            ))

                    list_chat__.setHasFixedSize(true)
                    list_chat__.layoutManager = LinearLayoutManager(this@ChatActivity)
                    list_chat__.adapter = chatAdapter

                    if(status){
                        pushnotif(title + ": " +chatList.get(chatList.size - 1).message)
                    }

                    status = true
                    chatAdapter.notifyDataSetChanged()

                }
            }
        })


        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        sendBtn.setOnClickListener {
            status = false
            val msgKey = groupChat.push().key
            msg = textMsg.text.toString()



            if(selectedPhotoUri == null && !msg.isEmpty()){
                chatHelper.SaveMessageInfoToDatabase(groupChat, username, msg, msgKey, this)
            }else{
                uploadImageToFbStorage(msgKey)
            }

            textMsg.setText("")
            chatAdapter.notifyDataSetChanged()

        }

        infobtn.setOnClickListener {
            val intent = Intent(this, Chat_Info::class.java)
            intent.putExtra("user", user)
            intent.putExtra("title", title)
            intent.putExtra("projectID", uniqid)
            intent.putExtra("project", project)
            startActivity(intent)
            finish()
        }

    }

    private fun uploadImageToFbStorage(msgKey : String) {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()+jfile
        val reff = FirebaseStorage.getInstance().getReference("images/$filename")


        reff.putFile(selectedPhotoUri!!)
                .addOnFailureListener {
                    Log.d("SelectedFile", it.localizedMessage)
                }.addOnSuccessListener {
                    Log.d("SelectedFile", "Success: ${it.metadata?.path}")
                    path = it.metadata?.contentType.toString()
                    var url = it.downloadUrl.toString()


                    chatHelper.SaveMessageInfoToDatabase(groupChat, username, url, msgKey, this)
                    uploadImage.setBackgroundColor(0)
                    path = ""
                    jfile = ""
                    selectedPhotoUri = null
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("file", " terambil")

            selectedPhotoUri = data.data
            Log.d("tes", data.data.toString())
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            val bitmapDrawble = BitmapDrawable(bitmap)
            uploadImage.setBackgroundDrawable(bitmapDrawble)
        }
    }

    private fun InitializeFields() {
        textMsg = findViewById(R.id.text_msg)
        sendBtn = findViewById(R.id.btn_sendchat)
        infobtn = findViewById(R.id.chat_add_info)
        chat_title_ = findViewById(R.id.chat_title)
        backtomain = findViewById(R.id.chat_back_to_project)
    }

    fun send(){
//        val fm = FirebaseMessaging.getInstance().
//        fm.send(RemoteMessage.Builder("$SENDER_ID@"))
        val token = FirebaseInstanceId.getInstance().token
        val d = Log.d("testestes", token)
        val to = "AIzaSyBfCRd8Y-2RbBpJmfVRkaHCABK29bo4jcQ"
        val msgId = AtomicInteger()
        FirebaseMessaging.getInstance().send(RemoteMessage.Builder("827729386052@gcm.googleapis.com")
                .setMessageId(msgId.get().toString())
                .addData("hello", "world")
                .build())
//        FirebaseMessaging.getInstance().send()
    }
    private fun pushnotif(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel_id = "lalala"

        @SuppressLint("WrongConstant")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(notificationChannel_id, "test notification", NotificationManager.IMPORTANCE_MAX)


            notificationChannel.description = "test Content notification"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 500)
            notificationChannel.enableVibration(true)


            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, notificationChannel_id)

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.support.v4.R.drawable.notification_icon_background)
                .setContentTitle("New Message")
                .setContentText(message)

        notificationManager.notify(1, notificationBuilder.build())
    }
}
