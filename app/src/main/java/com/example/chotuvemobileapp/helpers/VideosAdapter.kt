package com.example.chotuvemobileapp.helpers

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.PlayVideoActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.UserProfileActivity
import com.example.chotuvemobileapp.entities.VideoItem
import com.example.chotuvemobileapp.helpers.Utilities.DATE_FORMAT_LONG
import com.example.chotuvemobileapp.helpers.Utilities.DATE_FORMAT_SHORT
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.parseNumber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VideosAdapter(private val mVideos: List<VideoItem>) : RecyclerView.Adapter<VideosAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val videoThumbnail: ImageView = itemView.findViewById(R.id.VideoThumbnail)
        val title: TextView = itemView.findViewById(R.id.VideoTitle)
        val user: TextView = itemView.findViewById(R.id.VideoUser)
        val date: TextView = itemView.findViewById(R.id.VideoDate)
        val views: TextView = itemView.findViewById(R.id.VideoViews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val videoView = inflater.inflate(R.layout.item_video, parent, false)
        return ViewHolder(videoView)
    }

    override fun getItemCount() =  mVideos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = mVideos[position]
        holder.title.text = video.title
        holder.user.text = video.user
        holder.views.text = parseNumber(video.views)
        val dateToDisplay = LocalDateTime.parse(video.date, DateTimeFormatter.ofPattern(DATE_FORMAT_LONG))
            .format(DateTimeFormatter.ofPattern(DATE_FORMAT_SHORT))
        holder.date.text = dateToDisplay
        Glide.with(holder.itemView.context).load(Uri.parse(video.url)).centerCrop().into(holder.videoThumbnail)
        holder.videoThumbnail.setOnClickListener {
            val intent = Intent(it.context, PlayVideoActivity::class.java)
            intent.putExtra("videoTitle", video.title)
            intent.putExtra("videoAuthor", video.user)
            intent.putExtra("videoDate", dateToDisplay)
            intent.putExtra("url", video.url)
            intent.putExtra("videoId", video.id)
            it.context.startActivity(intent)
        }
        holder.user.setOnClickListener {
            val intent = Intent(it.context, UserProfileActivity::class.java)
            intent.putExtra(USERNAME, video.user)
            it.context.startActivity(intent)
        }
    }
}