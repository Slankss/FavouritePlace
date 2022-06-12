package com.okankukul.favouriteplace.Home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
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
                R.id.add -> loadFragment(AddFragment(this))
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