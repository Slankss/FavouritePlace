package com.okankukul.favouriteplace.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recyler_row,parent,false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        // işlemler


       // Picasso.get().load(postList.get(position).gorselUrl).into(holder.itemView.recycler_row_imgview)
        // indirme işlemi
    }

    override fun getItemCount(): Int {
        return postList.size
    }

}