package com.example.prk.santuy

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_task.*
import java.text.SimpleDateFormat

class EditTask : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_edit_task)

        var user = intent.extras!!.get("user") as User
        var task = intent.extras!!.get("task") as Task
        var taskID = task.taskID

        val intent =  Intent(this@EditTask, MainActivity::class.java)
        intent.putExtra("user", user)


        var title = findViewById<EditText>(R.id.edit_task_title)
        var note = findViewById<EditText>(R.id.edit_task_note)
        var deadline = findViewById<EditText>(R.id.edit_task_deadline)

        title.setText(task.title)
        note.setText(task.notes)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        var date_ = dateFormat.format(task.deadline)
        deadline.setText(date_)


        if(task.difficulty.equals("Easy")){
            eEasy.isChecked = true
        }else if(task.difficulty.equals("Medium")){
            eMedium.isChecked = true
        }else{
            eHard.isChecked = true
        }

        var delbtn = findViewById<Button>(R.id.edit_task_deletebtn)
        var updtbtn = findViewById<Button>(R.id.edit_task_btnupdate)
        var cancelbtn = findViewById<Button>(R.id.edit_task_cancelbtn)
        delbtn.setOnClickListener{
            val confirm_dialog = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialog_view = inflater.inflate(R.layout.delete_dialog, null)
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)

            confirm_dialog.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->

                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                dialog.show()
                FirebaseFirestore.getInstance()
                        .collection("tasks")
                        .document(taskID)
                        .delete().addOnCompleteListener {
                            dialog.dismiss()
                            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
                            startActivity(intent)
                            finish()
                        }

            })

            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            {dialog, which -> dialog.dismiss() }
            )

            confirm_dialog.show()
        }

        updtbtn.setOnClickListener{
            val temptitle: String = title.text.toString()
            val tempnote : String = note.text.toString()

            if(temptitle.isEmpty()){
                title.error = "Please enter your Task Title"
                title.requestFocus()
            }
            else if(tempnote.isEmpty()){
                note.error = "Please enter yout Task Note"
                note.requestFocus()
            }else{
                val confirm_dialog = AlertDialog.Builder(this)
                val inflater = LayoutInflater.from(this)
                val dialog_view = inflater.inflate(R.layout.update_dialog, null)
                confirm_dialog.setView(dialog_view)
                confirm_dialog.setCancelable(true)

                confirm_dialog.setPositiveButton("Update", DialogInterface.OnClickListener { dialog, which ->

                    val dialog = ProgressDialog(this)
                    dialog.setMessage("Please wait ...")

                    dialog.show()

                    FirebaseFirestore.getInstance()
                            .collection("tasks")
                            .document(taskID)
                            .update(mapOf(
                                    "title" to temptitle,
                                    "notes" to tempnote,
                                    "deadline" to task.deadline,
                                    "difficulty" to task.difficulty
                            )).addOnCompleteListener {
                                dialog.dismiss()
                                Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }

                })
                confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
                { dialog, which -> dialog.dismiss() }
                )


                confirm_dialog.show()
            }
        }

        cancelbtn.setOnClickListener {
            startActivity(intent)
            finish()
        }

    }

    private fun delete(){

    }

    private fun update(){

    }


}
