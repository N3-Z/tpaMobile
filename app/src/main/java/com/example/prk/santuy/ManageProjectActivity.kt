package com.example.prk.santuy
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import com.example.prk.santuy.R.id.rewardID
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_manage_project.*
import java.text.SimpleDateFormat
import java.util.*

class ManageProjectActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)

        setContentView(R.layout.activity_manage_project)

        var user = intent.extras!!.get("user") as User
        var project = intent.extras!!.get("project") as Project
        val projectID = project.projectID

        val intent =  Intent(this@ManageProjectActivity, MainActivity::class.java)
        intent.putExtra("user", user)
        var min: Long = Calendar.getInstance().time.time

        val calendar = Calendar.getInstance()

        val deadline = DatePickerDialog.OnDateSetListener{
            _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            uprojectDeadline.setText(dateFormat.format(calendar.getTime()))

        }


        uprojectDeadline.setOnClickListener{

            val dpd = DatePickerDialog(this@ManageProjectActivity, deadline, calendar
            .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

            dpd.datePicker.minDate = min

            dpd.show()

        }

        uPtitle.setText(project.title)
        uPnotes.setText(project.notes)
        if(project.difficulty.equals("Easy")){
            uEasy.isChecked = true
        }else if(project.difficulty.equals("Medium")){
            uMedium.isChecked = true
        }else{
            uHard.isChecked = true
        }

        val setDdeadline : String = dateFormat.format( project.deadline)
        Log.d("INI", setDdeadline)
        try {

            uprojectDeadline.setText(setDdeadline)

        }catch (e : Exception){
            Log.d("INI", e.toString())
        }

        uProjectBtn.setOnClickListener{


            updateProject(projectID)



        }

        dProjectBtn.setOnClickListener{
            val confirm_dialog = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            val dialog_view = inflater.inflate(R.layout.delete_dialog, null)
            confirm_dialog.setView(dialog_view)
            confirm_dialog.setCancelable(true)

            confirm_dialog.setPositiveButton("Delete", DialogInterface.OnClickListener { dialog, which ->

                val dialog = ProgressDialog(this)
                dialog.setMessage("Please wait ...")

                Log.d("USER ID", user.userID)
                Log.d("USER ID", project.userID.first())


                if(!user.userID.equals(project.userID.first())){

                    Toast.makeText(this, "You are not authorized!", Toast.LENGTH_LONG).show()

                }else{
                    dialog.show()
                    FirebaseFirestore.getInstance()
                            .collection("projects")
                            .document(projectID)
                            .delete().addOnCompleteListener {
                                dialog.dismiss()
                                Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }
                }


            })



            confirm_dialog.setNegativeButton("CANCEL", DialogInterface.OnClickListener
            {dialog, which -> dialog.dismiss() }
            )

            confirm_dialog.show()
        }

        cupdateProjectBtn.setOnClickListener{
            startActivity(intent)
            finish()
        }

    }

    fun updateProject(projectID : String){

        val uPT = uPtitle.text.toString()
        val uPN = uPnotes.text.toString()
        val uPD = findViewById<RadioButton>(uprojectDifficulty.checkedRadioButtonId).text.toString()
        val uProjectDeadline = dateFormat.parse(uprojectDeadline.text.toString())

        if (uPT.isEmpty()) {
            uPtitle.error = "Please enter your Project title"
            uPtitle.requestFocus()
            return
        }

        if (uPN.isEmpty()) {
            uPnotes.error = "Please enter your Project Notes"
            uPnotes.requestFocus()
            return
        }

        if (uprojectDeadline.text.toString().isEmpty()) {
            uprojectDeadline.error = "Please enter your Project Deadline"
            uprojectDeadline.requestFocus()
            return
        }


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
                    .collection("projects")
                    .document(projectID)
                    .update(mapOf(
                            "title" to uPT,
                            "notes" to uPN,
                            "deadline" to uProjectDeadline,
                            "difficulty" to uPD
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
