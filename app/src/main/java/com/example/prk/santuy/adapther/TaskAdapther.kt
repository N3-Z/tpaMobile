
package com.example.prk.santuy.adapther


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.prk.santuy.EditTask
import com.example.prk.santuy.MainActivity
import com.example.prk.santuy.R
import com.example.prk.santuy.TasksFragment
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TaskAdapther(var taskList: ArrayList<Task>, val ctx: Context, val user: User): RecyclerView.Adapter<TaskAdapther.ViewHolder>(),Serializable {

    var TaskRef = FirebaseFirestore.getInstance().collection("tasks")
    var min: Long = Calendar.getInstance().time.time

    private val activity : MainActivity = ctx as MainActivity
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
        return ViewHolder(v);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.taskTitle?.text = taskList[position].title
        holder.taskNotes?.text = taskList[position].notes
        holder.taskDeadline?.text = dateFormat.format(taskList[position].deadline).toString()


        if(min > taskList[position].deadline.time){
            holder.taskTitle.setTextColor(Color.parseColor("#ed413e"))
        }else{
            var sharePref = PreferenceManager.getDefaultSharedPreferences(ctx)
            val mode = sharePref.getInt("mode",1)
            if(mode == 2){
                holder.taskTitle.setTextColor(R.style.darkmode)
            }
            else{
                holder.taskTitle.setTextColor(R.style.AppTheme)
            }
        }

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
                            val intent : Intent = Intent(activity, EditTask::class.java)
                            intent.putExtra("task", t)
                            intent.putExtra("user", user)
                            activity.startActivity(intent)
                            activity.finishMainActivity()
                        }
                    }
        }

        holder.doneTask.setOnClickListener{

            var coins = ""

            Log.d("DIFFICULTY",taskList[position].difficulty)

            if(taskList[position].difficulty.equals("Easy")){
                coins = "2"
            }
            else if(taskList[position].difficulty.equals("Medium")){
                coins = "5"
            }
            else if(taskList[position].difficulty.equals("Hard")){
                coins = "10"
            }


            Toast.makeText(ctx, "You get " + coins + " coins!", Toast.LENGTH_LONG).show()

            user.coins = (user.coins.toInt() + coins.toInt()).toString()

            Log.d("CURR COIN", user.coins)

            FirebaseFirestore.getInstance()
                    .collection("users").document(user.userID)
                    .update(mapOf("coins" to user.coins))
                    .addOnCompleteListener {

                        TaskRef.document(taskList[position].taskID)
                                .delete().addOnCompleteListener {

                                TaskRef.whereEqualTo("userID", user.userID).
                                        orderBy("deadline", Query.Direction.ASCENDING).get().addOnCompleteListener OnNavigationItemSelectedListener@{ documents ->

                                taskList.clear()
                                var taskAdapther = TaskAdapther(taskList, ctx, user)
                                val bundle = Bundle()

                                val tf = TasksFragment()
                                val ft = activity.supportFragmentManager.beginTransaction()

                                    for (task in documents.result) {
                                        val t: Task = Task(
                                                task.getString("taskID"),
                                                task.getString("userID"),
                                                task.getString("title"),
                                                task.getString("difficulty"),
                                                task.getDate("deadline"),
                                                task.getString("notes")
                                        )
                                        taskList.add(t)
                                    }

                                    taskAdapther = TaskAdapther(taskList, ctx, user)
                                    bundle.putSerializable("user", user)
                                    bundle.putSerializable("task", taskAdapther)
                                    bundle.putParcelableArrayList("task_list", taskList)

                                    tf.arguments = bundle

                                    ft.replace(R.id.container, tf)
                                    ft.commit()

                            }
                        }

                    }
        }



    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun updateTasklist(newTaskList : ArrayList<Task>){
        this.taskList = newTaskList

        this.notifyDataSetChanged()
    }

    class ViewHolder(taskView: View): RecyclerView.ViewHolder(taskView){
        val taskTitle = taskView.findViewById<TextView>(R.id.taskTitle)
        val taskDeadline = taskView.findViewById<TextView>(R.id.taskDeadline)
        val taskNotes = taskView.findViewById<TextView>(R.id.taskNotes)
        val taskEdit = taskView.findViewById<LinearLayout>(R.id.editTask)
        val doneTask = taskView.findViewById<Button>(R.id.done_btn)


    }
}