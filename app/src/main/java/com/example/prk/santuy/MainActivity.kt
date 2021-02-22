package com.example.prk.santuy
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.internal.BottomNavigationMenu
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.NotificationCompat
import android.util.Log
import android.widget.Toast
import com.example.prk.santuy.adapther.*
import com.example.prk.santuy.helper.Mode
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.Reward
import com.example.prk.santuy.models.Task
import com.example.prk.santuy.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class MainActivity : AppCompatActivity(), Serializable {

    lateinit var buttonNav : BottomNavigationView
    lateinit var bundle:Bundle
    lateinit var user:User

    var TaskRef = FirebaseFirestore.getInstance().collection("tasks")
    var RewardRef = FirebaseFirestore.getInstance().collection("rewards")
    var ProjectRef = FirebaseFirestore.getInstance().collection("projects")
    var PartnerRef = FirebaseFirestore.getInstance().collection("users")

    lateinit var partnerAdapter: PartnerAdapter
    lateinit var taskAdapther: TaskAdapther
    lateinit var rewardAdapther: RewardAdapther
    lateinit var boughtRewardAdapther: BoughtRewardAdapter
    lateinit var projectAdapther: ProjectAdapter
    lateinit var task_priority : String

    private fun settheme(){
        var sharePref = PreferenceManager.getDefaultSharedPreferences(this)
        val mode = sharePref.getInt("mode",1)

        if(mode == 2){
            this.setTheme(R.style.darkmode)
        }
        else{
            this.setTheme(R.style.AppTheme)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mode.setTheme_(this)
        setContentView(R.layout.activity_main)

        val token = FirebaseInstanceId.getInstance().token
        Log.d("testestes", token)

        val dialog = ProgressDialog(this)
        dialog.setMessage("Please wait ...")

        dialog.show()

        bundle = Bundle()
        user = intent.extras!!.get("user") as User

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        var partner = ArrayList<String>()
        val taskList = ArrayList<Task>()
        val rewardList = ArrayList<Reward>()
        val boughtRewardList = ArrayList<Reward>()
        val projectList = ArrayList<Project>()
        partnerAdapter = PartnerAdapter(partner,this)
        task_priority = "null"
        taskAdapther = TaskAdapther(taskList, this, user)
        rewardAdapther = RewardAdapther(rewardList, this@MainActivity, user)
        boughtRewardAdapther = BoughtRewardAdapter(boughtRewardList, this@MainActivity, user)
        projectAdapther = ProjectAdapter(projectList, this@MainActivity, user)



        TaskRef.whereEqualTo("userID", user.userID).orderBy("deadline", Query.Direction.ASCENDING).get().addOnCompleteListener OnNavigationItemSelectedListener@{
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
                }
                taskAdapther= TaskAdapther(taskList,this, user)
                task_priority = taskList.first().title
                bundle.putString("task_priority", task_priority )

            }

            ProjectRef.orderBy("deadline",Query.Direction.ASCENDING).get().addOnCompleteListener{
                task ->
                if(!task.result.isEmpty){
                    for (doc in task.result){
                        val project : ArrayList<String> = doc.get("userID") as ArrayList<String>
                        for(p in project){
                            val userIDs =  doc.data.get("userID") as ArrayList<String>
                            if (p.equals(user.userID)){
                                val pr = Project(
                                        doc.getString("projectID"),
                                        doc.getString("title"),
                                        doc.getString("notes"),
                                        doc.getString("difficulty"),
                                        doc.getDate("deadline"),
                                        doc.getString("last_chat"),
                                        doc.getDate("lastdate"),
                                        userIDs
                                )
                                projectList.add(pr)
                                break
                            }
                        }
                        projectAdapther = ProjectAdapter(projectList, this@MainActivity, user)
                    }
                }

                RewardRef.whereEqualTo("userID", user.userID).get().addOnCompleteListener{
                    documents ->
                    if(!documents.result.isEmpty){
                        for(task in documents.result){
                            val t = Reward(
                                    task.getString("rewardID"),
                                    task.getString("userID"),
                                    task.getString("title"),
                                    task.getString("notes"),
                                    task.getString("cost")
                            )
                            if(task.getBoolean("buy")){
                                boughtRewardList.add(t)
                            }else{
                                rewardList.add(t)
                            }
                        }
                        rewardAdapther = RewardAdapther(rewardList, this@MainActivity, user)
                    }
                    PartnerRef.whereEqualTo("userID", user.userID).get().addOnCompleteListener{document ->
                        if(!document.result.isEmpty){
                            var partners = document.result.first().get("partner") as ArrayList<String>
                            for(p in partners){
                                partner.add(p)
                            }
                            partnerAdapter = PartnerAdapter(partner, this)
                        }
                    }
                }
            }

            val prevIntent = intent



            bundle.putSerializable("partner", partnerAdapter)
            bundle.putSerializable("user", user)
            bundle.putSerializable("task", taskAdapther)
            bundle.putParcelableArrayList("task_list", taskList)
            bundle.putSerializable("reward", rewardAdapther)
            bundle.putSerializable("bought_reward", boughtRewardAdapther)
            bundle.putSerializable("project", projectAdapther)
            dialog.dismiss()

            if(prevIntent.hasExtra("fragment")){
                val value = prevIntent.extras.get("fragment")
                if(value == 3){
                    val fragment = RewardContainer()
                    fragment.arguments = bundle
                    supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                    buttonNav.selectedItemId = R.id.rewardMenu
                }
            }else{
                val fragment = PartnersFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
            }


        }


        buttonNav = findViewById(R.id.bottom_nav)
        buttonNav.setOnNavigationItemSelectedListener(mOnNavigattionItemSelectedListener)

    }

    private val mOnNavigattionItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        menuItem -> when (menuItem.itemId){
            R.id.partnerMenu -> {
                val fragment = PartnersFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.taskMenu-> {
                val fragment = TasksFragment()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.projectMenu-> {
                val fragment = ProjectsFragment()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.rewardMenu-> {
                val fragment = RewardContainer()
                fragment.arguments = bundle

                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.profileMenu -> {
                val fragment = AccountFragment()
                fragment.arguments = bundle
                supportFragmentManager.beginTransaction().replace(R.id.container, fragment, fragment.javaClass.simpleName).commit()
                return@OnNavigationItemSelectedListener true
            }

         }
        false
    }

    fun finishMainActivity(){
        finish()
    }



}

