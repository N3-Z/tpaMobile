package com.example.prk.santuy

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.annotation.RequiresApi
import android.util.Log
import android.widget.*
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_task.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class CreateTaskActivity : AppCompatActivity() {
    lateinit var tempdate : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_create_task)

        val actionBar = supportActionBar
        actionBar?.hide()


        val deadlinebtn = findViewById<ImageButton>(R.id.create_task_deadline)
        val createbtn = findViewById<Button>(R.id.create_task_btn)
        val backtomenu = findViewById<ImageButton>(R.id.chat_back_to_main)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        tempdate = findViewById(R.id.temp_date_date)

        val user = intent.extras!!.get("user") as User
        val intent =  Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)

        deadlinebtn.setOnClickListener{
            var dp = DatePickerDialog(this,DatePickerDialog.OnDateSetListener{
                view, year, month, dayOfMonth ->  tempdate.setText("" + dayOfMonth + "-" + (month+1) + "-" + year)
            }, year, month, day)
            dp.show()
        }

        createbtn.setOnClickListener{
            var title = findViewById<EditText>(R.id.create_task_title).text.toString()

            var dificulty = ""

            try {
               dificulty = findViewById<RadioButton>(create_task_dificulty.checkedRadioButtonId).text.toString()

            }catch (e : Exception){

            }
            var note = findViewById<EditText>(R.id.create_task_note).text.toString()
            var deadline = tempdate.text
            if(title.isEmpty()){
                Toast.makeText(this, "Please enter your Task Title", Toast.LENGTH_LONG).show()
            }
            else if(dificulty.isEmpty()){
                Toast.makeText(this, "Please enter your Task Dificulty", Toast.LENGTH_LONG).show()
            }
            else if(deadline.isEmpty()){
                Toast.makeText(this, "Please enter your Task Deadline", Toast.LENGTH_LONG).show()
            }
            else if(note.isEmpty()){
                Toast.makeText(this, "Please enter your Task Note", Toast.LENGTH_LONG).show()
            }
            else if(user.userID.isEmpty()){
                Toast.makeText(this, "UserId notfound", Toast.LENGTH_LONG).show()
            }
            else{
                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                dialog.show()

                var deadlinedate : Date = SimpleDateFormat("dd-MM-yyyy").parse(deadline.toString())
                var uuid = UUID.randomUUID()
                val newT = Task(uuid.toString(), user.userID, title, dificulty, deadlinedate, note)
                FirebaseFirestore.getInstance().collection("tasks").add(newT).addOnSuccessListener {
                    documentReference ->
                    FirebaseFirestore.getInstance().collection("tasks")
                            .document(documentReference.id)
                            .update("taskID", documentReference.id).addOnCompleteListener{
                                Toast.makeText(this, "Create Success!", Toast.LENGTH_LONG)
                                startActivity(intent)
                                finish()
                            }
                }
            }
        }
        backtomenu.setOnClickListener{
            startActivity(intent)
            finish()
        }
    }
}
