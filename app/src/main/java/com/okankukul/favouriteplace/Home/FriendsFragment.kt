package com.okankukul.favouriteplace.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentFriendsBinding
import com.okankukul.favouriteplace.databinding.FragmentHomeBinding


class FriendsFragment : Fragment() {

    private lateinit var binding : FragmentFriendsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(layoutInflater)


        return binding.root
    }


}