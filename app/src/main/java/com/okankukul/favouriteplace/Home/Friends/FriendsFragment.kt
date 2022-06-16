package com.okankukul.favouriteplace.Home.Friends

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Adapter.FriendsRecylerAdapter
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentFriendsBinding


class FriendsFragment : Fragment() {

    private lateinit var binding : FragmentFriendsBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore

    private lateinit var recyclerViewAdapter : FriendsRecylerAdapter
    var currentUsername=""

    private var friendList = ArrayList<String>()
    private var requestList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        var currentUser = auth.currentUser

        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFriends(view)
        getFriendRequest()


        val emptyList = ArrayList<String>()
        val layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter= FriendsRecylerAdapter(friendList,view.context,R.string.friend_list.toString(),emptyList,emptyList)
        binding.recyclerView.adapter = recyclerViewAdapter

        binding.btnGoAddFriend.setOnClickListener {
            activity?.let {
                val transaction = it.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.navHost,FriendsAddFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

        binding.btnGoRequestPage.setOnClickListener {
            activity?.let {
                val transaction = it.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.navHost,FriendRequestFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendsBinding.inflate(layoutInflater)

        return binding.root
    }

    fun getFriends(view : View){



        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("Profile").whereEqualTo("username",currentUsername).addSnapshotListener { snapshot, exception ->
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


                        friendList.clear()

                        for(document in documents)
                        {
                            var friendUsernameList = document.get("friends") as ArrayList<String>

                            for(item in friendUsernameList){
                                friendList.add(item)
                            }
                        }

                        recyclerViewAdapter.notifyDataSetChanged() // verileri yenile demek
                    }
                }
            }
        }

    }

    fun getFriendRequest() {

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("FriendRequests").whereEqualTo("sendToUsername", currentUsername).get()
            .addOnSuccessListener { documents ->
                if (documents != null) {

                    var documents = documents.documents

                    requestList.clear()
                    for (item in documents) {

                        var sendToUsername = item.get("sendToUsername") as String
                        requestList.add(sendToUsername)

                    }
                    binding.txtNumberOfRequest.text = requestList.size.toString()
                }
            }


    }


}