package com.example.chotuvemobileapp.helpers

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.ChatActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.entities.ChatMessageItem
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.parseTimestamp
import kotlin.coroutines.coroutineContext

class ChatMessagesAdapter (private val messages: ArrayList<ChatMessageItem>) : RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>(){
    var messageList = messages

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val message: TextView = itemView.findViewById(R.id.MessageText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val messageView = inflater.inflate(R.layout.item_message, parent, false)
        return ViewHolder(messageView)
    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]
        holder.message.text = message.message
    }

    fun addMessage(newMessage : ChatMessageItem) {
        messageList.add(newMessage)
        notifyItemInserted(messageList.size)
    }
}