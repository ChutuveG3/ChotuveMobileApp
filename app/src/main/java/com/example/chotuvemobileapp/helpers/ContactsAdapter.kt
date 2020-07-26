package com.example.chotuvemobileapp.helpers

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.ChatActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME

class ContactsAdapter(private val contacts: ArrayList<String>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val friendUser: TextView = itemView.findViewById(R.id.Username)
        val acceptRequest: ImageView = itemView.findViewById(R.id.AcceptRequest)
        val declineRequest : ImageView = itemView.findViewById(R.id.DeclineRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contact = contacts[position]
        holder.acceptRequest.visibility = View.GONE
        holder.declineRequest.visibility = View.GONE
        holder.friendUser.text = contact
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ChatActivity::class.java)
            intent.putExtra(USERNAME, contact)
            it.context.startActivity(intent)
            (it.context as Activity).finish()
        }
    }
}