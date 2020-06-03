package com.example.chotuvemobileapp.helpers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.CommentItem

class CommentsAdapter(private val mComments: List<CommentItem>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val userPic: ImageView = itemView.findViewById(R.id.CommentAuthorPic)
        val user: TextView = itemView.findViewById(R.id.CommentAuthor)
        val body: TextView = itemView.findViewById(R.id.CommentBody)
        val date: TextView = itemView.findViewById(R.id.CommentDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val videoView = inflater.inflate(R.layout.item_comment, parent, false)
        return ViewHolder(videoView)
    }

    override fun getItemCount(): Int {
        return mComments.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = mComments[position]
        holder.user.text = comment.author
        holder.body.text = comment.body
        holder.date.text = comment.date
    }
}