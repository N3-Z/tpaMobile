package com.example.prk.santuy

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.*
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_chat_task.*
import kotlinx.android.synthetic.main.activity_create_task.*
import java.text.SimpleDateFormat
import java.util.*

class ChatTask : AppCompatActivity() {
    lateinit var tempdate: EditText
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_chat_task)

        var user = intent.extras!!.get("user") as User
        var title = intent.extras!!.get("title") as String
        var projectid = intent.extras!!.get("projectID") as String
        var task = intent.extras!!.get("task") as Task


        val actionBar = supportActionBar
        actionBar?.hide()

        var deadline = ""

        val deadlinebtn = findViewById<ImageButton>(R.id.createchat_task_deadline)
        val createbtn = findViewById<Button>(R.id.createchat_task_btn)
        val backbtn = findViewById<ImageButton>(R.id.chat_back_to_info)

        if (!task.userID.isEmpty()) {
            createbtn.setText("Update")
            findViewById<EditText>(R.id.createchat_task_title).setText(task.title)

            if (task.difficulty.equals("Easy")) {
                findViewById<RadioButton>(R.id.uEasy).isChecked = true
            } else if (task.difficulty.equals("Medium")) {
                findViewById<RadioButton>(R.id.uMedium).isChecked = true
            } else {
                findViewById<RadioButton>(R.id.uHard).isChecked = true
            }
            findViewById<EditText>(R.id.createchat_task_note).setText(task.notes)
            deadline = dateFormat.format(task.deadline).toString()
            Toast.makeText(this, deadline, Toast.LENGTH_LONG).show()

        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        tempdate = findViewById(R.id.createchat_date_date)

        val intent = Intent(this, Chat_Info::class.java)
        intent.putExtra("user", user)
        intent.putExtra("title", title)
        intent.putExtra("projectID", projectid)


        backbtn.setOnClickListener {
            startActivity(intent)
            finish()
        }


        deadlinebtn.setOnClickListener {
            var dp = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                tempdate.setText("" + dayOfMonth + "-" + (month + 1) + "-" + year)
            }, year, month, day)
            dp.show()
            Toast.makeText(this, tempdate.text.toString(), Toast.LENGTH_LONG).show()
            deadline = tempdate.text.toString()
        }

        createbtn.setOnClickListener {
            var title = findViewById<EditText>(R.id.createchat_task_title).text.toString()
            var dificulty = ""

            try{
                dificulty = findViewById<RadioButton>(createchat_task_dificulty.checkedRadioButtonId).text.toString()
            }catch (e : Exception){ }


            var note = findViewById<EditText>(R.id.createchat_task_note).text.toString()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter your Task Title", Toast.LENGTH_LONG).show()
            } else if (dificulty.isEmpty()) {
                Toast.makeText(this, "Please enter your Task Dificulty", Toast.LENGTH_LONG).show()
            } else if (deadline.isEmpty() || deadline.equals("")) {
                Toast.makeText(this, "Please enter your Task Deadline", Toast.LENGTH_LONG).show()
            } else if (note.isEmpty()) {
                Toast.makeText(this, "Please enter your Task Note", Toast.LENGTH_LONG).show()
            } else if (projectid.isEmpty()) {
                Toast.makeText(this, "ProjectID notfound", Toast.LENGTH_LONG).show()
            } else {
                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                dialog.show()

                //insert task group
                if (task.userID.equals("") || task.userID.equals(" ")) {
                    var deadlinedate: Date = SimpleDateFormat("dd-MM-yyyy").parse(deadline)
                    val newT = Task("", projectid, title, dificulty, deadlinedate, note)
                    FirebaseFirestore.getInstance().collection("tasks").add(newT).addOnSuccessListener { documentReference ->
                        FirebaseFirestore.getInstance().collection("tasks")
                                .document(documentReference.id)
                                .update("taskID", documentReference.id).addOnCompleteListener {
                                    Toast.makeText(this, "Create Success!", Toast.LENGTH_LONG)
                                    startActivity(intent)
                                    finish()
                                }
                    }
                }
                //update task
                else {
                    var deadlinedate: Date = SimpleDateFormat("dd-MM-yyyy").parse(deadline)
                    FirebaseFirestore.getInstance().collection("tasks").document(task.taskID)
                            .update(mapOf(
                                    "title" to title,
                                    "notes" to note,
                                    "deadline" to deadlinedate,
                                    "difficulty" to dificulty
                            )).addOnCompleteListener {
                                Toast.makeText(this, "Create Success!", Toast.LENGTH_LONG)
                                startActivity(intent)
                                finish()
                            }

                }
            }
        }
    }
}
