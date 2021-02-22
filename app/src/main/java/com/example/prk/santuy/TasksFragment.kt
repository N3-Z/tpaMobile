
package com.example.prk.santuy


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.prk.santuy.adapther.TaskAdapther
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.facebook.WebDialog
import com.facebook.login.LoginResult
import com.github.sundeepk.compactcalendarview.CompactCalendarView
import com.github.sundeepk.compactcalendarview.CompactCalendarView.CompactCalendarViewListener
import com.github.sundeepk.compactcalendarview.domain.Event
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
//import kotlinx.android.synthetic.main.com_facebook_device_auth_dialog_fragment.*
import kotlinx.android.synthetic.main.fragment_tasks.*
import java.text.SimpleDateFormat
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class TasksFragment : Fragment() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    private val dateMonthFormat = SimpleDateFormat("MMMM yyyy")

    var TaskRef = FirebaseFirestore.getInstance().collection("tasks")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var  view = inflater.inflate(R.layout.fragment_tasks, container, false)

        val taskView : RecyclerView = view.findViewById(R.id.kavebacot)
        val taskViewProgressBar = view.findViewById<ProgressBar>(R.id.task_view_progressbar)
        val noTask = view.findViewById<TextView>(R.id.no_task)
        taskViewProgressBar.visibility = View.GONE
        noTask.visibility = View.GONE

        var min: Long = Calendar.getInstance().time.time
        val user : User = this.arguments?.getSerializable("user") as User
        val todayDate = dateFormat.format(min)


        val newCalendarView : CompactCalendarView = view.findViewById(R.id.calendar_view)
        val calendarMonth : TextView = view.findViewById(R.id.calendar_month)

        newCalendarView.setUseThreeLetterAbbreviation(true)
        newCalendarView.shouldScrollMonth(true)

        calendarMonth.setText( dateMonthFormat.format(min))

        taskView.setHasFixedSize(true)
        taskView.layoutManager = LinearLayoutManager(context)

        val taskAdapther =  this.arguments?.getSerializable("task") as TaskAdapther
        val taskList = this.arguments?.getParcelableArrayList<Task>("task_list") as ArrayList<Task>
        taskView.adapter = taskAdapther


        val iter = taskList.iterator()
        while (iter.hasNext()){
            val task : Task = iter.next()

            newCalendarView.addEvent(Event(Color.RED, task.deadline.time))

            if(!todayDate.equals(dateFormat.format(task.deadline))){

                Log.d("NOT FOUND", task.toString())
                iter.remove()
                taskAdapther.notifyDataSetChanged()

            }else{
                Log.d("ITEM", task.toString())
            }

        }

        if (taskList.size == 0){

            taskViewProgressBar.visibility = View.GONE
            noTask.visibility = View.VISIBLE
            taskView.visibility = View.VISIBLE

        }else{

            taskViewProgressBar.visibility = View.GONE
            taskView.visibility = View.VISIBLE

        }

        newCalendarView.setListener( object : CompactCalendarView.CompactCalendarViewListener{
            override fun onDayClick(dateClicked: Date?) {

                var dateSearch : String = ""
                dateSearch = dateFormat.format(dateClicked)
                taskViewProgressBar.visibility = View.VISIBLE
                noTask.visibility = View.GONE
                taskView.visibility = View.GONE

                newCalendarView.getEvents(dateClicked)

                taskList.clear()
                taskAdapther.notifyDataSetChanged()

                TaskRef.whereEqualTo("userID", user.userID).orderBy("deadline", Query.Direction.ASCENDING).get().addOnCompleteListener{
                    documents ->
                    if(!documents.result.isEmpty){
                        for(task in documents.result){
                            val t : Task = Task(
                                    task.getString("taskID"),
                                    task.getString("userID"),
                                    task.getString("title"),
                                    task.getString("difficulty"),
                                    task.getDate("deadline"),
                                    task.getString("notes")
                            )
                            taskList.add(t)
                            taskAdapther.notifyDataSetChanged()
                        }
                        val iter = taskList.iterator()
                        while (iter.hasNext()){

                            val task : Task = iter.next()


                            if(!dateSearch.equals(dateFormat.format(task.deadline))){

                                Log.d("NOT FOUND", task.toString())
                                iter.remove()
                                taskAdapther.notifyDataSetChanged()

                            }else{
                                Log.d("ITEM", task.toString())
                            }
                        }
                        if (taskList.size == 0){

                            taskViewProgressBar.visibility = View.GONE
                            noTask.visibility = View.VISIBLE
                            taskView.visibility = View.VISIBLE

                        }else{

                            taskViewProgressBar.visibility = View.GONE
                            taskView.visibility = View.VISIBLE

                        }
                    }
                    else{
                        taskViewProgressBar.visibility = View.GONE
                        noTask.visibility = View.VISIBLE
                        taskView.visibility = View.VISIBLE
                    }
                }


            }

            override fun onMonthScroll(firstDayOfNewMonth: Date?) {
                calendarMonth.setText(dateMonthFormat.format(firstDayOfNewMonth) )
                taskList.clear()
                taskAdapther.notifyDataSetChanged()
            }

        }

        )

        val addbtn : ImageButton = view.findViewById(R.id.add_task__)

        addbtn.setOnClickListener{
            val intent = Intent(activity, CreateTaskActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }





}

