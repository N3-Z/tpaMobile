
package com.example.prk.santuy

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_create_project.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList


class CreateProjectActivity : AppCompatActivity() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_create_project)

        val user = intent.extras!!.get("user") as User
        val intent =  Intent(this@CreateProjectActivity, MainActivity::class.java)
        intent.putExtra("user", user)
        var min: Long = Calendar.getInstance().time.time

        val calendar = Calendar.getInstance()
        val deadline = DatePickerDialog.OnDateSetListener{
            _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            projectDeadline.setText(dateFormat.format(calendar.getTime()))
        }

        projectDeadline.setOnClickListener{
            val dpd = DatePickerDialog(this@CreateProjectActivity, deadline, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH))

            dpd.datePicker.minDate = min

            dpd.show()
        }

        cinsertProjectBtn.setOnClickListener{
            startActivity(intent)
            finish()
        }

        iProjectBtn.setOnClickListener{
            insertReward(user, intent)
        }



    }

    private fun insertReward(user: User, intent: Intent) {
        val dialog = ProgressDialog(this)
        dialog.setMessage("Please wait ...")

        dialog.show()

        val newPT = newPtitle.text.toString()
        val newPN = newPnotes.text.toString()
        val newPD = findViewById<RadioButton>(projectDifficulty.checkedRadioButtonId).text.toString()
        val newProjectDeadline  = dateFormat.parse(projectDeadline.text.toString())

        if (newPT.isEmpty()) {
            newPtitle.error = "Please enter your Project title"
            newPtitle.requestFocus()
            return
        }

        if (newPN.isEmpty()) {
            newPnotes.error = "Please enter your Project Notes"
            newPnotes.requestFocus()
            return
        }

        if (projectDeadline.text.toString().isEmpty()) {
            projectDeadline.error = "Please enter your Project Deadline"
            projectDeadline.requestFocus()
            return
        }


        var userID = ArrayList<String>()

        userID.add(user.userID)

        val newP = Project( "",
                newPT, newPN, newPD, newProjectDeadline, "", Date(), userID)

        FirebaseFirestore.getInstance().collection("projects").add(newP).addOnSuccessListener {
            documentReference ->

            FirebaseFirestore.
                    getInstance().collection("projects")
                    .document(documentReference.id)
                    .update("projectID", documentReference.id).addOnCompleteListener{
                        Toast.makeText(this@CreateProjectActivity, "Create Success!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        startActivity(intent)
                        finish()
                    }

        }


    }


}
