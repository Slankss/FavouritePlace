package com.okankukul.favouriteplace.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
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
        }
        else{
            holder.btnAccept.visibility = View.VISIBLE
        }

    }

    override fun getItemCount(): Int {
        return requestList.size
    }
}