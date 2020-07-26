package com.example.chotuvemobileapp.helpers

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R

class ChatViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
    val user: TextView = itemView.findViewById(R.id.ChatUser)
    val lastMessage: TextView = itemView.findViewById(R.id.ChatLastMessage)
    val timestamp: TextView = itemView.findViewById(R.id.ChatTimeStamp)
}
