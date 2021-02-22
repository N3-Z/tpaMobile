package com.example.prk.santuy

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.example.prk.santuy.adapther.TaskAdapterChat
import com.example.prk.santuy.adapther.TaskAdapther
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_add_partner_by_id.*
import java.util.*
import kotlin.collections.ArrayList

class Chat_Info : AppCompatActivity() {

    lateinit var chat_back_info : ImageButton
    lateinit var add_partner : EditText
    lateinit var search_partnerbtn : Button
    lateinit var list_task_group : RecyclerView
    lateinit var chat_add_task : ImageButton

    var TaskRef = FirebaseFirestore.getInstance().collection("tasks")
    lateinit var bundle: Bundle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_chat__info)
        initialized()
        bundle = Bundle()

        val actionBar = supportActionBar
        actionBar?.hide()
        var user = intent.extras!!.get("user") as User
        var title = intent.extras!!.get("title") as String
        var projectid = intent.extras!!.get("projectID") as String
        val project_temp = intent.extras!!.get("project") as Project

        val bundle = Bundle()
        var taskList = ArrayList<Task>()
        var adapter = TaskAdapterChat(taskList,this, user, title, projectid)


        list_task_group.setHasFixedSize(true)
        list_task_group.layoutManager = LinearLayoutManager(this)
        list_task_group.adapter = adapter

        TaskRef.whereEqualTo("userID", projectid).orderBy("deadline", Query.Direction.ASCENDING).get().addOnCompleteListener { documents ->

            if (!documents.result.isEmpty) {
                for (task in documents.result) {
                    val t = Task(
                            task.getString("taskID"),
                            task.getString("userID"),
                            task.getString("title"),
                            task.getString("difficulty"),
                            task.getDate("deadline"),
                            task.getString("notes")
                    )
                    taskList.add(t)
                    adapter.notifyDataSetChanged()
                }
            }
        }


        chat_back_info.setOnClickListener{
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("user", user)
            intent.putExtra("title", title)
            intent.putExtra("project", project_temp)
            startActivity(intent)
            finish()
        }
        chat_add_task.setOnClickListener{
            val intent = Intent(this, ChatTask::class.java)
            intent.putExtra("user", user)
            intent.putExtra("title", title)
            intent.putExtra("projectID", projectid)
            intent.putExtra("task", Task("","","","",Date(),""))
            startActivity(intent)
            finish()
        }

        var project = Project("","","","", Date(),"",Date(), ArrayList())
        FirebaseFirestore.getInstance().collection("projects").whereEqualTo("projectID", projectid).orderBy("deadline", Query.Direction.ASCENDING).get().addOnCompleteListener { task ->
            project = Project(
                    task.result.first().getString("projectID"),
                    task.result.first().getString("title"),
                    task.result.first().getString("notes"),
                    task.result.first().getString("difficulty"),
                    task.result.first().getDate("deadline"),
                    task.result.first().getString("last_chat"),
                    task.result.first().getDate("lastdate"),
                    task.result.first().get("userID") as ArrayList<String>)
        }


        search_partnerbtn.setOnClickListener {
            val dialog = ProgressDialog(this)
            dialog.setMessage("Please wait ...")

            dialog.show()
            FirebaseFirestore.getInstance()
                    .collection("users").whereEqualTo("username", add_partner.text.toString()).get()
                    .addOnCompleteListener {
                        task ->
                        dialog.dismiss()
                        if (!task.result.isEmpty){
                            val partners = task.result.first().data.get("partner") as ArrayList<String>

                            for (p in partners){
                                Log.d("INI", p.toString())
                            }

                            val partner = User(
                                    task.result.first().getString("userID"),
                                    task.result.first().getString("username"),
                                    task.result.first().getString("email"),
                                    task.result.first().getString("password"),
                                    task.result.first().getString("image"),
                                    task.result.first().getString("status"),
                                    task.result.first().getString("coins"),
                                    partners
                            )
//                            Toast.makeText(this, project.title, Toast.LENGTH_LONG).show()
                            val fragment = FindPartner()
                            bundle.putSerializable("user", user)
                            bundle.putSerializable("partner", partner)
                            bundle.putSerializable("lalala", "ChatInfo")
                            bundle.putSerializable("project", project)

                            fragment.arguments = bundle
                            supportFragmentManager.beginTransaction().replace(R.id.partner_container, fragment, fragment.javaClass.simpleName).commit()

                        }
                        else{
                            val fragment = NotFoundPartner()
                            supportFragmentManager.beginTransaction().replace(R.id.partner_container, fragment, fragment.javaClass.simpleName).commit()
                        }
                    }
        }

    }
    private fun initialized(){
        chat_back_info = findViewById(R.id.chat_back_info)
        add_partner = findViewById(R.id.add_partner_username_)
        search_partnerbtn = findViewById(R.id.search_partnerbtn)
        list_task_group = findViewById(R.id.list_task_group)
        chat_add_task = findViewById(R.id.chat_add_task)
    }
}
