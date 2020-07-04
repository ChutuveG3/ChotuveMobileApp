package com.example.chotuvemobileapp.helpers

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.UserProfileActivity
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.ui.friends.FriendsViewModel

class FriendsAdapter(private val friends: ArrayList<String>,
                     private val preferences: SharedPreferences,
                     private val viewModel: FriendsViewModel,
                     private val pending: Boolean = false) : RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val friendUser: TextView = itemView.findViewById(R.id.Username)
        val acceptRequest: ImageView = itemView.findViewById(R.id.AcceptRequest)
        val declineRequest : ImageView = itemView.findViewById(R.id.DeclineRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val videoView = inflater.inflate(R.layout.item_user, parent, false)
        return ViewHolder(videoView)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.friendUser.text = friend
        holder.friendUser.setOnClickListener {
            val intent = Intent(it.context, UserProfileActivity::class.java)
            intent.putExtra("user", friend)
            it.context.startActivity(intent)
        }
        if (pending){
            holder.acceptRequest.setOnClickListener {
                holder.itemView.alpha = .2F
                ProfileInfoDataSource.respondRequest(preferences, friend, true){
                    if (it == "Success"){
                        viewModel.addFriend(friend)
                        holder.itemView.visibility = View.GONE
                        holder.itemView.alpha = 1F
                        Toast.makeText(holder.itemView.context, "$friend is now your friend!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            holder.declineRequest.setOnClickListener {
                holder.itemView.alpha = .2F
                ProfileInfoDataSource.respondRequest(preferences, friend, false){
                    if (it == "Success"){
                        viewModel.removePendingFriend(friend)
                        holder.itemView.visibility = View.GONE
                        holder.itemView.alpha = 1F
                    }
                }
            }
        }
        else{
            holder.acceptRequest.visibility = View.GONE
            holder.declineRequest.visibility = View.GONE
        }
    }
}