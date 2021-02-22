package com.example.prk.santuy

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat.getSystemService
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.fragment_find_partner.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FindPartner.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FindPartner.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class FindPartner : Fragment() {

    lateinit var insertPartner : Button
    lateinit var alreadyPartner : TextView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val myview = inflater.inflate(R.layout.fragment_find_partner, container, false)

        val user: User = this.arguments?.getSerializable("user") as User
        val partner: User = this.arguments?.getSerializable("partner") as User
        var lalala: String = this.arguments?.getSerializable("lalala") as String
        val project = this.arguments?.getSerializable("project") as Project
        Toast.makeText(activity, project.title, Toast.LENGTH_LONG).show()

        val imageUri = partner.image
        val partnerImage: ImageView = myview.findViewById(R.id.partnerImg)
        Picasso.with(requireActivity()).load(imageUri).into(partnerImage)

        val ProfileUsername: TextView = myview.findViewById(R.id.partner_name)
        ProfileUsername.text = partner.username

         insertPartner = myview.findViewById(R.id.insertPartner)
         alreadyPartner = myview.findViewById(R.id.already_partner)
         alreadyPartner.visibility = View.GONE


        for (p in user.partner) {
            if (partner.userID.equals(p)) {
                alreadyPartner.visibility = View.VISIBLE
                insertPartner.visibility = View.GONE
                break
            }
        }


        insertPartner.setOnClickListener {

            addNewPartner(user, partner, lalala, project)
            insertPartner.visibility = View.INVISIBLE
        }


        return myview
    }

    private fun addNewPartner(user: User, partner: User, lalala: String, project: Project) {

        if (lalala.equals("AddPartner")) {
            if (user.userID.equals(partner.userID)) {
                Toast.makeText(activity, "You can't add Yourself :)", Toast.LENGTH_SHORT).show()
                return
            }

            val dialog = ProgressDialog(activity)
            dialog.setMessage("Please wait ...")
            dialog.show()

            user.partner.add(partner.userID)

            partner.partner.add(user.userID)
            FirebaseFirestore.getInstance()
                    .collection("users").document(user.userID).update("partner", user.partner).addOnCompleteListener {
                        FirebaseFirestore.getInstance()
                                .collection("users").document(partner.userID).update("partner", partner.partner).addOnCompleteListener {
                                    dialog.dismiss()
                                    Toast.makeText(activity, "Success Add Partner", Toast.LENGTH_SHORT).show()
                                    alreadyPartner.visibility = View.VISIBLE
                                    insertPartner.visibility = View.GONE
                                }
                    }
            pushnotif(partner.username.toString())

        } else if (lalala.equals("ChatInfo")) {
            project.userID.add(partner.userID)
            Log.d("projectt", project.projectID)
            for (i in project.userID) {
                Log.d("projectt", i)
            }
            var temp = project.projectID
            Log.d("projectttt", temp)
            FirebaseFirestore.getInstance()
                    .collection("projects").document(temp).update("userID", project.userID).addOnCompleteListener {
                        Toast.makeText(activity, "Success Add Partner", Toast.LENGTH_SHORT).show()
                        alreadyPartner.visibility = View.VISIBLE
                        insertPartner.visibility = View.GONE

                    }
            pushnotif(partner.username.toString())

        }

    }

    private fun pushnotif(name: String) {
        val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel_id = "lalala"

        @SuppressLint("WrongConstant")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(notificationChannel_id, "test notification", NotificationManager.IMPORTANCE_MAX)


            notificationChannel.description = "test Content notification"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.vibrationPattern = longArrayOf(0, 1000, 500, 500)
            notificationChannel.enableVibration(true)


            notificationManager.createNotificationChannel(notificationChannel)
        }

        val notificationBuilder = NotificationCompat.Builder(requireActivity(), notificationChannel_id)

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(android.support.v4.R.drawable.notification_icon_background)
                .setContentTitle("New Friend")
                .setContentText("Now " + name + " is your friend")

        notificationManager.notify(1, notificationBuilder.build())
    }

}
