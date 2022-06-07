package com.okankukul.favouriteplace.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.okankukul.favouriteplace.Adapter.RecylerAdapter
import com.okankukul.favouriteplace.Add.AddActivity
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var recyclerViewAdapter : RecylerAdapter

    var postList = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoAdd.setOnClickListener {
            startActivity(Intent(applicationContext,AddActivity::class.java))
        }

        val layoutManager = LinearLayoutManager(this)
        binding.recylerView.layoutManager = layoutManager
        recyclerViewAdapter= RecylerAdapter(postList)
        binding.recylerView.adapter = recyclerViewAdapter

    }


}