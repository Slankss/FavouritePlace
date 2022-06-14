package com.okankukul.favouriteplace.Home.Profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.okankukul.favouriteplace.Adapter.RecylerAdapter
import com.okankukul.favouriteplace.Login_Register.LoginActivity
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var recyclerViewAdapter : RecylerAdapter
    var currentUserEmail =""
    var currentUserName = ""

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()



        var currentUser = auth.currentUser
        currentUserEmail =""
        currentUserName = ""
        if(currentUser != null){
            currentUserEmail = currentUser.email.toString()
            currentUserName = currentUser.displayName.toString()
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData(view)

        binding.txtProfileName.text = currentUserName

        val layoutManager = LinearLayoutManager(view.context)
        binding.myRecylerView.layoutManager = layoutManager
        recyclerViewAdapter= RecylerAdapter(postList,view.context)
        binding.myRecylerView.adapter = recyclerViewAdapter

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            activity?.let {
                startActivity(Intent(it.applicationContext,LoginActivity::class.java))
                it.finish()
            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()

        return binding.root
    }
    fun getData(view : View){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN

        fireStore.collection("Post").whereEqualTo("userEmail",currentUserEmail).orderBy("date", Query.Direction.DESCENDING

        ).addSnapshotListener { snapshot, exception ->
            if(exception != null)
            {
                println(exception.localizedMessage)
            }
            else
            {
                if(snapshot != null)
                {
                    if(!snapshot.isEmpty) // boş değilse
                    {
                        val documents = snapshot.documents

                        postList.clear()
                        for(document in documents)
                        {
                            var userEmail = document.get("userEmail") as String
                            var username = document.get("userName") as String
                            var placeAdress = document.get("location") as String
                            var placeName = document.get("placeName") as String
                            var imageUrl = document.get("imageUrl") as String


                            val  indirilenPost= Post(placeName,placeAdress,username,userEmail,imageUrl)
                            postList.add(indirilenPost)
                        }
                        recyclerViewAdapter.notifyDataSetChanged() // verileri yenile demek
                    }
                }
            }
        }

    }



}