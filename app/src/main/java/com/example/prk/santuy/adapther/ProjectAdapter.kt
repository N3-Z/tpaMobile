package com.example.prk.santuy.adapther

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.prk.santuy.ChatActivity
import com.example.prk.santuy.MainActivity
import com.example.prk.santuy.ManageProjectActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable
import java.text.SimpleDateFormat
import kotlin.math.log

class ProjectAdapter(val list:ArrayList<Project>,val ctx:AppCompatActivity, val user: User): RecyclerView.Adapter<ProjectAdapter.ViewHolder>(), Serializable {

//    private val activity : MainActivity = ctx as MainActivity
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.activity_listproject, parent, false);
        return ViewHolder(v);
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title_project?.text = list[position].title
        holder.last_chat_project?.text = list[position].last_chat
        holder.lastdate_project?.text = dateFormat.format(list[position].deadline).toString()
        holder.btntochat.setOnClickListener{

            val intent = Intent(ctx,ChatActivity::class.java)
            intent.putExtra("project", list[position])
            intent.putExtra("title", list[position].title)
            intent.putExtra("user",user)
            ctx.startActivity(intent)
            ctx.finish()
        }

        holder.btntoEdit.setOnClickListener{

            val intent: Intent = Intent(ctx,ManageProjectActivity::class.java)
            intent.putExtra("project", list[position] )
            intent.putExtra("user", user)
            ctx.startActivity(intent)
            ctx.finish()

//
//            FirebaseFirestore.getInstance().collection("projects")
//                    .whereEqualTo("projectID",list[position].projectID)
//                    .get().addOnCompleteListener { tasks ->
//                        if(!tasks.result.isEmpty){
//                            val userID = tasks.result.first().get("userID") as ArrayList<String>
//                            val p  = Project(
//                                    tasks.result.first().getString("projectID"),
//                                    tasks.result.first().getString("title"),
//                                    tasks.result.first().getString("notes"),
//                                    tasks.result.first().getString("difficulty"),
//                                    tasks.result.first().getDate("deadline"),
//                                    tasks.result.first().getString("last_chat"),
//                                    tasks.result.first().getDate("lastdate"),
//                                    userID
//                                    )
//                            val intent: Intent = Intent(ctx,ManageProjectActivity::class.java)
//                            intent.putExtra("project", p )
//                            intent.putExtra("user", user)
//                            ctx.startActivity(intent)
//                            ctx.finish()
//                        }
//                    }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val title_project = view.findViewById<TextView>(R.id.title_project)
        val last_chat_project = view.findViewById<TextView>(R.id.last_chat_project)
        val lastdate_project = view.findViewById<TextView>(R.id.lastdate_project)
        val btntochat = view.findViewById<CardView>(R.id.btntochat)
        val btntoEdit = view.findViewById<Button>(R.id.editProject)
    }

}