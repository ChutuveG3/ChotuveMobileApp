package com.example.chotuvemobileapp.helpers

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.ChatActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.parseTimestamp

class ChatsAdapter (private val chats: ArrayList<ChatItem>) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val user: TextView = itemView.findViewById(R.id.ChatUser)
        val lastMessage: TextView = itemView.findViewById(R.id.ChatLastMessage)
        val timestamp: TextView = itemView.findViewById(R.id.ChatTimeStamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val videoView = inflater.inflate(R.layout.item_chat, parent, false)
        return ViewHolder(videoView)
    }

    override fun getItemCount(): Int = chats.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = chats[position]
        holder.user.text = chat.user
        holder.lastMessage.text = chat.lastMessage
        holder.timestamp.text = parseTimestamp(chat.timestamp)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra("ChatId", chat.messagesId)
            intent.putExtra(USERNAME, chat.user)
            it.context.startActivity(intent)
        }
    }
}