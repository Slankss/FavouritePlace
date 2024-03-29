package com.okankukul.favouriteplace.Adapter

import android.app.AlertDialog
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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.Home.Friends.FriendRequestFragment
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.RecyleerFriendsListBinding
import com.okankukul.favouriteplace.databinding.RecylerRowBinding
import com.squareup.picasso.Picasso

class FriendsRecylerAdapter (var friendList : ArrayList<String>,val mcontext : Context,val key : String,
    var requestList : ArrayList<String>,var gelenRequestList : ArrayList<String>) : RecyclerView.Adapter<FriendsRecylerAdapter.PostHolder>()
{
    private lateinit var binding: RecyleerFriendsListBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var fireStore : FirebaseFirestore
    private var currentUsername=""

    class PostHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {

        var txtFriendName : TextView = itemView.findViewById(R.id.txtFriendNname)
        var btnFriendDelete : TextView = itemView.findViewById(R.id.btnFriendDelete)
        var btnAccept : ImageView = itemView.findViewById(R.id.btnAcceptInSearch)
        var btnCancel : ImageView = itemView.findViewById(R.id.btnCancelInSearch)
        var txtGelenIstek : TextView = itemView.findViewById(R.id.txtGelenIstek)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyleer_friends_list,parent,false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        var currentUser = auth.currentUser

        if(currentUser != null){
            currentUsername = currentUser.displayName.toString()
        }

        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.txtFriendName.text = friendList.get(position)

        if(key == R.string.add_friend.toString()){
            var request_id=0


            if(!gelenRequestList.contains(friendList.get(position))){
                holder.txtGelenIstek.visibility = View.GONE
                holder.btnAccept.visibility = View.GONE
                holder.btnCancel.visibility = View.GONE
                holder.btnFriendDelete.visibility = View.VISIBLE
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
            }
            else{
                holder.txtGelenIstek.visibility = View.VISIBLE
                holder.btnAccept.visibility = View.VISIBLE
                holder.btnCancel.visibility = View.VISIBLE
                holder.btnFriendDelete.visibility = View.GONE

                holder.btnAccept.setOnClickListener {
                    acceptRequest(friendList.get(position))
                }

                holder.btnCancel.setOnClickListener {
                    cancelRequest(friendList.get(position))
                }
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
            holder.txtGelenIstek.visibility = View.GONE
            holder.btnAccept.visibility = View.GONE
            holder.btnCancel.visibility = View.GONE
            holder.btnFriendDelete.visibility = View.VISIBLE
            holder.btnFriendDelete.text = "Arkadaşlarımdan çıkar"
            holder.btnFriendDelete.setOnClickListener {

                val builder = AlertDialog.Builder(mcontext)
                builder.setTitle("Arkadaşlarımdan Çıkar")
                builder.setMessage(friendList.get(position)+" adlı kullanıcıyı arkadaşlarınızdan çıkarmak istiyor musunuz?")

                builder.setPositiveButton("Evet") { dialog, which ->
                    deleteFriends(friendList.get(position))
                }
                builder.setNegativeButton("Hayır") { dialog, which ->

                }
                builder.show()
            }
        }

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
                    requestList.add(sendToUsername)
                    this.notifyDataSetChanged()
                }

            }.addOnFailureListener { exception ->
                println(exception.localizedMessage)
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
    fun cancelRequest(senderUsername : String){
        var id = ""
        fireStore.collection("FriendRequests").whereEqualTo("sendToUsername",currentUsername)
            .whereEqualTo("senderUsername",senderUsername)
            .get().addOnSuccessListener {   document ->
                if(document != null)
                {
                    for (item in document){
                        id = item.id

                    }

                    if(!id.isEmpty()){
                        fireStore.collection("FriendRequests").document(id).delete().addOnCompleteListener {
                            if(it.isSuccessful){
                                Toast.makeText(mcontext,"İstek reddedildi",Toast.LENGTH_SHORT).show()
                                gelenRequestList.remove(senderUsername)
                                requestList.remove(senderUsername)
                                this.notifyDataSetChanged()
                            }
                        }.addOnFailureListener {
                            println(it.localizedMessage)
                        }
                    }
                }
            }
    }

    fun acceptRequest(senderUsername : String){

        var id = ""
        fireStore.collection("FriendRequests").whereEqualTo("senderUsername",senderUsername)
            .whereEqualTo("sendToUsername",currentUsername)
            .get().addOnSuccessListener {   document ->
                if(document != null)
                {
                    for (item in document){
                        id = item.id

                    }
                    if(!id.isEmpty()){
                        fireStore.collection("FriendRequests").document(id).delete().addOnCompleteListener {
                            if(it.isSuccessful){
                                requestList.remove(senderUsername)

                                fireStore.collection("Profile").whereEqualTo("username",currentUsername).get()
                                    .addOnSuccessListener { documents ->
                                        if(documents != null){
                                            var sendToId =""
                                            var documents = documents.documents
                                            for(item in documents){
                                                sendToId = item.id
                                            }
                                            fireStore.collection("Profile").document(sendToId).update("friends",FieldValue.arrayRemove(senderUsername)).addOnCompleteListener { task ->
                                                if (task.isSuccessful){
                                                    fireStore.collection("Profile").document(sendToId).update("friends",
                                                        FieldValue.arrayUnion(senderUsername))
                                                }
                                            }                                        }
                                    }
                                fireStore.collection("Profile").whereEqualTo("username",senderUsername).get()
                                    .addOnSuccessListener { documents ->
                                        if(documents != null){
                                            var sendToId =""
                                            var documents = documents.documents
                                            for(item in documents){
                                                sendToId = item.id
                                            }
                                            fireStore.collection("Profile").document(sendToId).update("friends",FieldValue.arrayRemove(currentUsername)).addOnCompleteListener { task ->
                                                if (task.isSuccessful){
                                                    fireStore.collection("Profile").document(sendToId).update("friends",
                                                        FieldValue.arrayUnion(currentUsername))
                                                }
                                            }                                        }
                                    }
                                Toast.makeText(mcontext,senderUsername+" arkadaş eklendi",Toast.LENGTH_LONG).show()
                                this.notifyDataSetChanged()
                            }
                        }.addOnFailureListener {
                            println(it.localizedMessage)
                        }
                    }
                }
            }
    }

    fun deleteFriends(friendName : String){
        fireStore.collection("Profile").whereEqualTo("username",currentUsername).get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    var sendToId =""
                    var documents = documents.documents
                    for(item in documents){
                        sendToId = item.id
                    }
                    fireStore.collection("Profile").document(sendToId).update("friends",FieldValue.arrayRemove(friendName))
                }
            }
        fireStore.collection("Profile").whereEqualTo("username",friendName).get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    var sendToId =""
                    var documents = documents.documents
                    for(item in documents){
                        sendToId = item.id
                    }
                    fireStore.collection("Profile").document(sendToId).update("friends",FieldValue.arrayRemove(currentUsername))

                }
            }
        Toast.makeText(mcontext,friendName+" arkadaş listenizden çıkarıldı",Toast.LENGTH_LONG).show()
        this.notifyDataSetChanged()
    }


}