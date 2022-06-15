package com.okankukul.favouriteplace.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.RecyleerFriendsListBinding
import com.okankukul.favouriteplace.databinding.RecylerRowBinding
import com.squareup.picasso.Picasso

class FriendsRecylerAdapter (var friendList : ArrayList<String>,val mcontext : Context,val key : String)
    : RecyclerView.Adapter<FriendsRecylerAdapter.PostHolder>()
{
    private lateinit var binding: RecyleerFriendsListBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var requestList = ArrayList<String>()
    private var currentUsername=""

    class PostHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {

        var txtFriendName : TextView = itemView.findViewById(R.id.txtFriendNname)
        var btnFriendDelete : TextView = itemView.findViewById(R.id.btnFriendDelete)



    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyleer_friends_list,parent,false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        checkFriendRequest()

        var currentUser = auth.currentUser

        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }


        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        if(key == R.string.add_friend.toString()){

            var request_id=0


            if(requestList.contains(friendList.get(position))){
                holder.btnFriendDelete.text = "İsteği geri çek"
                holder.btnFriendDelete.setBackgroundResource(R.drawable.text_box_orange)
                holder.btnFriendDelete.setTextColor(Color.Black.hashCode())

                request_id=R.string.delete_request
            }
            else{
                holder.btnFriendDelete.text = "İstek gönder"
                holder.btnFriendDelete.setBackgroundResource(R.drawable.text_box)
                holder.btnFriendDelete.setTextColor(Color.White.hashCode())

                request_id=R.string.add_request

            }

            holder.btnFriendDelete.setOnClickListener {
                if(request_id == R.string.add_request){
                    sendFriendRequest(friendList.get(position))

                }
                else if(request_id == R.string.delete_request){
                    deleteRequest(friendList.get(position))

                }
            }
        }
        else{
            holder.btnFriendDelete.text = "Arkadaşlarımdan çıkar"

            holder.btnFriendDelete.setOnClickListener {

            }
        }

        holder.txtFriendName.text = friendList.get(position)

    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    fun sendFriendRequest(sendToUsername : String){


        if(!currentUsername.isEmpty()){
            val friendRequestsHashMap = hashMapOf<String,Any>()
            friendRequestsHashMap.put("senderUsername",currentUsername)
            friendRequestsHashMap.put("sendToUsername",sendToUsername)

            fireStore.collection("FriendRequests").add(friendRequestsHashMap).addOnSuccessListener { task ->
                if(task != null){
                    Toast.makeText(mcontext.applicationContext,"İstek Gönderildi", Toast.LENGTH_SHORT).show()
                    this.notifyDataSetChanged()
                }

            }.addOnFailureListener { exception ->
                println(exception.localizedMessage)
            }
        }


    }

    fun checkFriendRequest(){

        // DESCENDING -> DÜŞEN   ASCENDING -> YUKSELEN
        fireStore.collection("FriendRequests").whereEqualTo("senderUsername",currentUsername)
            .addSnapshotListener { snapshot, exception ->

                if(exception != null)
                {
                    println(exception.localizedMessage)
                }
                else{
                    if(snapshot != null){
                        val documents = snapshot.documents

                        requestList.clear()
                        for(document in documents){
                            var sendToUsername= document.get("sendToUsername") as String
                            requestList.add(sendToUsername)
                        }

                    }
                }

            }

    }

    fun deleteRequest(sendToUsername : String){

        var id = ""


        fireStore.collection("FriendRequests").whereEqualTo("senderUsername",currentUsername)
            .whereEqualTo("sendToUsername",sendToUsername)
            .get().addOnSuccessListener {   document ->
                if(document != null)
                {
                    for (item in document){
                        id = item.id

                    }

                    if(!id.isEmpty()){
                        fireStore.collection("FriendRequests").document(id).delete().addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(mcontext,"İstek geri çekildi",Toast.LENGTH_SHORT).show()

                                requestList.remove(sendToUsername)
                                this.notifyDataSetChanged()
                            }
                        }.addOnFailureListener {
                            println(it.localizedMessage)
                        }
                    }
                }
            }



    }


}