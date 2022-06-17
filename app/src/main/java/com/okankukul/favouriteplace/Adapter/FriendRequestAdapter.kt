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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.RecyleerFriendsListBinding
import com.okankukul.favouriteplace.databinding.RecylerFriendRequestBinding

class FriendRequestAdapter (var requestList : ArrayList<String>, val mcontext : Context,
                            var key : String
                            ) : RecyclerView.Adapter<FriendRequestAdapter.PostHolder>() {
    private lateinit var binding: RecylerFriendRequestBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fireStore: FirebaseFirestore
    private var currentUsername = ""

    class PostHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var txtSenderName: TextView = itemView.findViewById(R.id.txtSenderName)
        var btnAccept : ImageView = itemView.findViewById(R.id.btnAccept)
        var btnCancel : ImageView = itemView.findViewById(R.id.btnCancel)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyler_friend_request, parent, false)

        auth = FirebaseAuth.getInstance()
        fireStore = FirebaseFirestore.getInstance()

        var currentUser = auth.currentUser

        if (currentUser != null) {
            currentUsername = currentUser.displayName.toString()
        }
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.txtSenderName.text = requestList.get(position)
        if(key == R.string.sendTo_friend_request.toString()){
            holder.btnAccept.visibility = View.GONE
            holder.btnCancel.setOnClickListener {
                deleteRequest(requestList.get(position))
            }

        }
        else{
            holder.btnAccept.visibility = View.VISIBLE

            holder.btnCancel.setOnClickListener {
                cancelRequest(requestList.get(position))
            }

            holder.btnAccept.setOnClickListener {
                acceptRequest(requestList.get(position))
            }
        }

    }

    override fun getItemCount(): Int {
        return requestList.size
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
                                Toast.makeText(mcontext,"İstek reddedildi", Toast.LENGTH_SHORT).show()
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
                                            }
                                        }
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
                                            }

                                        }
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