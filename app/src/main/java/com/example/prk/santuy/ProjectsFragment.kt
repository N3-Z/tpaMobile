package com.example.prk.santuy
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.example.prk.santuy.adapther.ProjectAdapter
import com.example.prk.santuy.models.Project
import com.example.prk.santuy.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_listproject.*
import org.w3c.dom.Text


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ProjectsFragment() : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       val view = inflater.inflate(R.layout.fragment_projects, container,false);
        val user : User = this.arguments?.getSerializable("user") as User

        var projectView_ : RecyclerView = view.findViewById(R.id.projectList_)
        projectView_.setHasFixedSize(true)
        projectView_.layoutManager = LinearLayoutManager(context)

        var projectAdapter = this.arguments?.getSerializable("project") as ProjectAdapter



        projectView_.adapter = projectAdapter
        Log.d("projectyuhu", projectAdapter.itemCount.toString())

        val gotoInsertProject = view.findViewById<ImageButton>(R.id.insertProject)

        gotoInsertProject.setOnClickListener{
            val intent =  Intent(activity, CreateProjectActivity::class.java)
            intent.putExtra("user", user)
            startActivity(intent)
            requireActivity().finish()
        }

       return view
    }

}
