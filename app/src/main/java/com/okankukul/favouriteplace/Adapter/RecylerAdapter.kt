package com.okankukul.favouriteplace.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import androidx.recyclerview.widget.RecyclerView
import com.okankukul.favouriteplace.Model.Post
import com.okankukul.favouriteplace.R
import com.okankukul.favouriteplace.databinding.RecylerRowBinding
import com.squareup.picasso.Picasso

class RecylerAdapter (val postList: ArrayList<Post>)
    : RecyclerView.Adapter<RecylerAdapter.PostHolder>()
{
    private lateinit var binding: RecylerRowBinding

    class PostHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

        var imgPlace : ImageView = itemView.findViewById(R.id.imgPlace)
        var txtPlaceName : TextView = itemView.findViewById(R.id.txtPlaceName)
        var txtPlaceAdress : TextView = itemView.findViewById(R.id.txtPlaceAdress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyler_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {


        // işlemler
        holder.txtPlaceName.text = postList.get(position).placeName.replaceFirstChar {
            it.uppercase()
        }
        holder.txtPlaceAdress.text = postList.get(position).location


       Picasso.get().load(postList.get(position).gorselUrl).into(holder.imgPlace)
        // indirme işlemi
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}