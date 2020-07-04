package com.example.chotuvemobileapp.helpers

import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.UserProfileActivity
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.ui.friends.FriendsViewModel

class SearchedUsersAdapter(private val users: ArrayList<String>,
                           private val preferences: SharedPreferences,
                           private val viewModel: FriendsViewModel) : RecyclerView.Adapter<SearchedUsersAdapter.ViewHolder>() {
    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView){
        val friendUser: TextView = itemView.findViewById(R.id.UsernameSearch)
        val addButton: Button = itemView.findViewById(R.id.AddFriend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val userView = inflater.inflate(R.layout.item_searched_user, parent, false)
        return ViewHolder(userView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.friendUser.text = user
        holder.friendUser.setOnClickListener {
            val intent = Intent(it.context, UserProfileActivity::class.java)
            intent.putExtra("user", user)
            it.context.startActivity(intent)
        }
        holder.addButton.setOnClickListener {
            ProfileInfoDataSource.addFriend(preferences, user) {
                if (it == "Success") {
                    viewModel.removePendingFriend(user)
                    Toast.makeText(holder.itemView.context, "Request sent to $user!", Toast.LENGTH_LONG).show()
                    holder.addButton.visibility = View.GONE
                } else Toast.makeText(holder.itemView.context, "Something went wrong, try again", Toast.LENGTH_LONG).show()
            }
        }
    }
}