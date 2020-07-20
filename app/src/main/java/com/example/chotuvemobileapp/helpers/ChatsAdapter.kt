package com.example.chotuvemobileapp.helpers

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R
import kotlinx.android.synthetic.main.item_chat.view.*

class ChatsAdapter () : RecyclerView.Adapter<ChatsAdapter.ViewHolder>(){
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val user: TextView = itemView.findViewById(R.id.ChatUser)
        val lastMessage: TextView = itemView.findViewById(R.id.ChatLastMessage)
        val timestamp: TextView = itemView.findViewById(R.id.ChatTimeStamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}