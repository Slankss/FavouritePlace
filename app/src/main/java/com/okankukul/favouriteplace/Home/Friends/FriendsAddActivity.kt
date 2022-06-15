package com.okankukul.favouriteplace.Home.Friends

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Adapter.FriendsRecylerAdapter
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.ActivityFriendsAddBinding

class FriendsAddActivity : AppCompatActivity() {

    private lateinit var binding : ActivityFriendsAddBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var findUsername = ""
    private lateinit var recyclerViewAdapter : FriendsRecylerAdapter
    private var currentUsername= ""

    private var userList = ArrayList<String>()
    private var requestList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFriendsAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        var currentUser = auth.currentUser
        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }

        checkFriendRequest()

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        recyclerViewAdapter= FriendsRecylerAdapter(userList,this,R.string.add_friend.toString(),requestList)
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
}