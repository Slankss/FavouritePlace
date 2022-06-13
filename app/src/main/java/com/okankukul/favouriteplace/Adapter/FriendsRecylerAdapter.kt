package com.okankukul.favouriteplace.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.RecyleerFriendsListBinding
import com.okankukul.favouriteplace.databinding.RecylerRowBinding
import com.squareup.picasso.Picasso

class FriendsRecylerAdapter (var friendList : ArrayList<String>,val mcontext : Context)
    : RecyclerView.Adapter<FriendsRecylerAdapter.PostHolder>()
{
    private lateinit var binding: RecyleerFriendsListBinding
    private lateinit var auth : FirebaseAuth


    class PostHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {


        var txtFriendName : TextView = itemView.findViewById(R.id.txtFriendNname)
        var btnFriendDelete : TextView = itemView.findViewById(R.id.btnFriendDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyleer_friends_list,parent,false)
        auth = FirebaseAuth.getInstance()
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.txtFriendName.text = friendList.get(position)
        println("recyler içinde : "+friendList.get(position))


    }

    override fun getItemCount(): Int {
        return friendList.size
    }



}