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
import com.okankukul.favouriteplace.databinding.RecylerRowBinding
import com.squareup.picasso.Picasso

class RecylerAdapter (val postList: ArrayList<Post>,val mcontext : Context)
    : RecyclerView.Adapter<RecylerAdapter.PostHolder>()
{
    private lateinit var binding: RecylerRowBinding
    private lateinit var auth : FirebaseAuth


    class PostHolder(itemView : View) : RecyclerView.ViewHolder(itemView)  {

        var imgPlace : ImageView = itemView.findViewById(R.id.imgPlace)
        var txtPlaceName : TextView = itemView.findViewById(R.id.txtPlaceName)
        var txtPlaceAdress : TextView = itemView.findViewById(R.id.txtPlaceAdress)
        var btnGoEditPage : ImageView = itemView.findViewById(R.id.btnGoEditPage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyler_row,parent,false)
        auth = FirebaseAuth.getInstance()
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {


        // işlemler
        holder.txtPlaceName.text = postList.get(position).placeName.replaceFirstChar {
            it.uppercase()
        }
        holder.txtPlaceAdress.text = postList.get(position).location

        auth.currentUser?.let {
            if(postList.get(position).username == it.displayName.toString()){
                holder.btnGoEditPage.visibility = View.VISIBLE

                holder.btnGoEditPage.setOnClickListener {

                }
            }
            else{
                holder.btnGoEditPage.visibility = View.GONE
            }
        }

       Picasso.get().load(postList.get(position).gorselUrl).into(holder.imgPlace)
        // indirme işlemi
    }

    override fun getItemCount(): Int {
        return postList.size
    }



}