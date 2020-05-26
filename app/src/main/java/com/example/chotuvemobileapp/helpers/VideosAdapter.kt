package com.example.chotuvemobileapp.helpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.PlayVideoActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.VideoItem

class VideosAdapter(private val mVideos: List<VideoItem>) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val videoThumbnail: ImageView = itemView.findViewById<ImageView>(R.id.VideoThumbnail)
        val userPic: ImageView = itemView.findViewById<ImageView>(R.id.OwnersProfilePic)
        val title: TextView = itemView.findViewById<TextView>(R.id.VideoTitle)
        val user: TextView = itemView.findViewById<TextView>(R.id.VideoUser)
        val date: TextView = itemView.findViewById<TextView>(R.id.VideoDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val videoView = inflater.inflate(R.layout.item_video, parent, false)
        return ViewHolder(videoView)
    }

    override fun getItemCount(): Int {
        return mVideos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = mVideos[position]
        holder.title.text = video.title
        holder.user.text = video.user
        holder.date.text = video.date
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, PlayVideoActivity::class.java)
            intent.putExtra("videoTitle", video.title)
            intent.putExtra("videoAuthor", video.user)
            intent.putExtra("videoDate", video.date)
            intent.putExtra("comments", position)
            it.context.startActivity(intent)
        }
    }
}