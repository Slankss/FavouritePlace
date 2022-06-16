package com.okankukul.favouriteplace.Home.Friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Adapter.FriendRequestAdapter
import com.okankukul.favouriteplace.Adapter.FriendsRecylerAdapter
import com.okankukul.favouriteplace.Adapter.SendToFriendRequestFragment
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentFriendRequestBinding
import com.okankukul.favouriteplace.databinding.FragmentFriendsBinding


class FriendRequestFragment : Fragment() {

    private lateinit var binding : FragmentFriendRequestBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var currentUsername= ""

    private lateinit var recyclerViewAdapter : FriendRequestAdapter

    private var requestList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var currentUser = auth.currentUser
        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }

        getFriendRequest()

        val layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter= FriendRequestAdapter(requestList,view.context,R.string.sender_friend_request.toString())
        binding.recyclerView.adapter = recyclerViewAdapter

        binding.btnSendToRequest.setOnClickListener {
            activity?.let {
                val transaction = it.supportFragmentManager.beginTransaction()
                transaction.replace(R.id.navHost,SendToFriendRequestFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendRequestBinding.inflate(layoutInflater)
        return binding.root
    }

    fun getFriendRequest(){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("FriendRequests").whereEqualTo("sendToUsername",currentUsername).get()
            .addOnSuccessListener { documents ->
                if(documents != null){

                    var documents =documents.documents

                    requestList.clear()
                    for(item in documents){

                        var sendToUsername = item.get("senderUsername") as String
                        requestList.add(sendToUsername)

                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }


    }


}