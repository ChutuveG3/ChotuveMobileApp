package com.example.chotuvemobileapp.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.ChatActivity
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.SelectFriendActivity
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.helpers.ChatViewHolder
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_messages.*
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class MessagesFragment : Fragment() {
    private val database by lazy {FirebaseDatabase.getInstance().reference}
    private val chatsReference by lazy {database.child("chats").child(username)}
    private val username by lazy {
        requireActivity().applicationContext
            .getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            .getString(USERNAME, "")!!
    }

    val options by lazy {
        FirebaseRecyclerOptions
            .Builder<ChatItem>()
            .setQuery(chatsReference, ChatItem::class.java)
            .build()
    }
    val adapter by lazy {
        object : FirebaseRecyclerAdapter<ChatItem, ChatViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
                val context = parent.context
                val inflater = LayoutInflater.from(context)
                val messageView = inflater.inflate(R.layout.item_chat, parent, false)
                return ChatViewHolder(messageView)
            }

            override fun onBindViewHolder(holder: ChatViewHolder, position: Int, model: ChatItem) {
                holder.user.text = model.user
                holder.lastMessage.text = model.lastMessage
                holder.timestamp.text = Utilities.parseTimestamp(LocalDateTime.ofInstant(
                    Instant.ofEpochSecond(model.timestamp),
                    TimeZone.getDefault().toZoneId()))
                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, ChatActivity::class.java)
                    intent.putExtra("ChatId", model.messagesId)
                    intent.putExtra(USERNAME, model.user)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.startListening()

        newChatButton.setOnClickListener {
            val intent = Intent(requireContext(), SelectFriendActivity::class.java)
            startActivity(intent)
        }

        MessagesToolbar.setNavigationOnClickListener{
            val home=  activity as HomeActivity
            home.openDrawer()
        }

        //val chatQuery = chatsReference.orderByChild("timestamp")

        ChatsRecyclerView.adapter = adapter
        ChatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}