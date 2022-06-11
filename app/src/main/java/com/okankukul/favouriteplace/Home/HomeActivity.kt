package com.okankukul.favouriteplace.Home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.okankukul.favouriteplace.Adapter.RecylerAdapter
import com.okankukul.favouriteplace.Login_Register.LoginActivity
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityHomeBinding
import javax.annotation.meta.When


class HomeActivity : AppCompatActivity() {


    private lateinit var auth : FirebaseAuth
    lateinit var bottom : BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()
        loadFragment(HomeFragment())
        bottom = findViewById(R.id.bottomNavigation) as BottomNavigationView

        bottom.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> loadFragment(HomeFragment())
                R.id.add -> loadFragment(AddFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment : Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.navHost,fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

}