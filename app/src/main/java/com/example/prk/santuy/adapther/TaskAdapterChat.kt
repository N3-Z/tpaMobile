package com.example.prk.santuy.adapther

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.prk.santuy.*
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.text.SimpleDateFormat

class TaskAdapterChat(var taskList: ArrayList<Task>, val ctx: Context, val user: User,val title : String, val projectid : String): RecyclerView.Adapter<TaskAdapterChat.ViewHolder>(), Serializable {
    private val activity : Chat_Info = ctx as Chat_Info
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskTitle?.text = taskList[position].title
        holder.taskNotes?.text = taskList[position].notes
        holder.taskDeadline?.text = dateFormat.format(taskList[position].deadline).toString()
        holder.taskEdit.setOnClickListener{
            FirebaseFirestore.getInstance().collection("tasks")
                    .whereEqualTo("taskID", taskList[position].taskID)
                    .get().addOnCompleteListener{tasks ->
                        if(!tasks.result.isEmpty){
                            val t = Task(tasks.result.first().getString("taskID"),
                                    tasks.result.first().getString("userID"),
                                    tasks.result.first().getString("title"),
                                    tasks.result.first().getString("difficulty"),
                                    tasks.result.first().getDate("deadline"),
                                    tasks.result.first().getString("notes"))
                            val intent = Intent(activity, ChatTask::class.java)
                            intent.putExtra("user", user)
                            intent.putExtra("title", title)
                            intent.putExtra("projectID", projectid)
                            intent.putExtra("task", t)
//                            intent.putExtra("deadline",dateFormat.format().toString())
                            activity.startActivity(intent)
                            activity.finish()
                        }
                    }
        }
    }
    class ViewHolder(taskView: View): RecyclerView.ViewHolder(taskView){
        val taskTitle = taskView.findViewById<TextView>(R.id.taskTitle)
        val taskNotes = taskView.findViewById<TextView>(R.id.taskNotes)
        val taskDeadline = taskView.findViewById<TextView>(R.id.taskDeadline)
        val taskEdit = taskView.findViewById<Button>(R.id.done_btn)
    }
}