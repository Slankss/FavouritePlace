package com.okankukul.favouriteplace.Home.Friends

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Adapter.FriendsRecylerAdapter
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.FragmentFriendsAddBinding


class FriendsAddFragment : Fragment() {

    private lateinit var binding : FragmentFriendsAddBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var findUsername = ""
    private lateinit var recyclerViewAdapter : FriendsRecylerAdapter
    private var currentUsername= ""

    private var userList = ArrayList<String>()
    private var requestList = ArrayList<String>()
    private var gelenRequestList = ArrayList<String>()

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

        checkFriendRequest()
        checkGelenFriendRequest()

        val layoutManager = LinearLayoutManager(view.context)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter= FriendsRecylerAdapter(userList,view.context,R.string.add_friend.toString(),requestList,gelenRequestList)
        binding.recyclerView.adapter = recyclerViewAdapter


        binding.btnFindFriends.setOnClickListener {

            findUsername = binding.txtFindUsernameEditText.text.toString().trim()

            if(check()){
                findFriend()
            }else{
                userList.clear()
                recyclerViewAdapter.notifyDataSetChanged()
            }

        }

    }

    fun check() : Boolean{


        if(findUsername.isEmpty()){
            binding.txtFindUsernameInputLayout.error = "kullanıcı adı girmediniz"
            return false
        }
        else{
            binding.txtFindUsernameInputLayout.error = null
        }

        return true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFriendsAddBinding.inflate(layoutInflater)

        return binding.root
    }

    fun findFriend(){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("Profile").orderBy("username").startAt(findUsername).endAt(findUsername+'\uf8ff').addSnapshotListener { snapshot, exception ->
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

                        userList.clear()

                        for(document in documents)
                        {
                            var username = document.get("username") as String

                            userList.add(username)
                        }
                        checkFriendList()

                    }
                    else{
                        userList.clear()
                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }
        }

    }

    fun checkFriendList() {


        var currentUser = auth.currentUser
        var currentUsername=""
        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }

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

                        for(document in documents)
                        {
                            var friendUsernameList = document.get("friends") as ArrayList<String>

                            for(user in userList){
                                if(friendUsernameList.contains(user)){
                                    userList.remove(user)
                                }
                            }

                        }
                        recyclerViewAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }

    fun checkFriendRequest(){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("FriendRequests").whereEqualTo("senderUsername",currentUsername).get()
            .addOnSuccessListener { documents ->
                if(documents != null){

                    var documents =documents.documents

                    requestList.clear()
                    for(item in documents){

                        var sendToUsername = item.get("sendToUsername") as String
                        requestList.add(sendToUsername)

                    }
                    recyclerViewAdapter.notifyDataSetChanged()
                }
            }


    }

    fun checkGelenFriendRequest(){

        fireStore.collection("FriendRequests").whereEqualTo("sendToUsername",currentUsername).get()
            .addOnSuccessListener { documents ->
                if(documents != null){

                    var documents = documents.documents
                    if(documents != null){

                        gelenRequestList.clear()
                        for(item in documents)
                        {
                            var senderUsername = item.get("senderUsername") as String
                            println(senderUsername)
                            gelenRequestList.add(senderUsername
                            )
                        }
                    }
                }
            }
    }

}