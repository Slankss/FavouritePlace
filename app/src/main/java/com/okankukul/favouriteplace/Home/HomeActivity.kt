package com.okankukul.favouriteplace.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.okankukul.favouriteplace.Adapter.RecylerAdapter
import com.okankukul.favouriteplace.Add.AddActivity
import com.okankukul.favouriteplace.Login_Register.LoginActivity
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private lateinit var recyclerViewAdapter : RecylerAdapter


    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        getData()

        val layoutManager = LinearLayoutManager(this)
        binding.recylerView.layoutManager = layoutManager
        recyclerViewAdapter= RecylerAdapter(postList,this)
        binding.recylerView.adapter = recyclerViewAdapter


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout)
        {
            auth.signOut()
            startActivity(Intent(applicationContext,LoginActivity::class.java))
            finish()

        }
        else if (item.itemId == R.id.addPlace)
        {
            startActivity(Intent(applicationContext,AddActivity::class.java))
        }


        return super.onOptionsItemSelected(item)
    }

    fun getData(){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("Post").orderBy("date", Query.Direction.DESCENDING


        ).addSnapshotListener { snapshot, exception ->
            if(exception != null)
            {
                Toast.makeText(applicationContext,exception.localizedMessage, Toast.LENGTH_LONG).show()
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