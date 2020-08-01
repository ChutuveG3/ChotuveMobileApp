package com.example.chotuvemobileapp.ui.home

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.ChatActivity
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.SelectFriendActivity
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.helpers.ChatViewHolder
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.PIC_URL
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.ui.profile.FullSizeImageActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_messages.*
import kotlinx.android.synthetic.main.item_chat.*
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
            .setQuery(chatsReference.orderByChild("timestamp"), ChatItem::class.java)
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
                    Instant.ofEpochSecond(kotlin.math.abs(model.timestamp)),
                    TimeZone.getDefault().toZoneId()))
                if (model.picUrl != null) {
                    Glide
                        .with(holder.itemView.context)
                        .load(Uri.parse(model.picUrl))
                        .centerCrop()
                        .into(holder.userPic)
                }
                else holder.userPic.setImageDrawable(resources.getDrawable(R.drawable.ic_baseline_person_45, null))

                holder.userPic.setOnClickListener {
                    val intent = Intent(requireContext(), FullSizeImageActivity::class.java)
                    intent.putExtra(PIC_URL, model.picUrl)
                    val options = ActivityOptions.makeSceneTransitionAnimation(requireActivity(), CardViewChat, "profilePic")
                    startActivity(intent, options.toBundle())
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(it.context, ChatActivity::class.java)
                    intent.putExtra("ChatId", model.messagesId)
                    intent.putExtra(USERNAME, model.user)
                    intent.putExtra(PIC_URL, model.picUrl)
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

        showLoadingScreen()
        NoChatsImageView.visibility = View.GONE
        NoChatsTextView.visibility = View.GONE

        newChatButton.setOnClickListener {
            val intent = Intent(requireContext(), SelectFriendActivity::class.java)
            startActivity(intent)
        }

        MessagesToolbar.setNavigationOnClickListener{
            val home=  activity as HomeActivity
            home.openDrawer()
        }

        chatsReference.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {}
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()){
                    NoChatsImageView.visibility = View.VISIBLE
                    NoChatsTextView.visibility = View.VISIBLE
                    quitLoadingScreen()
                }
            }
        })

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                quitLoadingScreen()
            }
        })

        ChatsRecyclerView.adapter = adapter
        ChatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ChatsRecyclerView.addItemDecoration(DividerItemDecoration(ChatsRecyclerView.context, resources.configuration.orientation))
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }

    private fun showLoadingScreen(){
        newChatButton.alpha = .2F
        newChatButton.isEnabled = false
        ChatsRecyclerView.alpha = .2F
        ChatsRecyclerView.isEnabled = false
        ChatsProgressBar.visibility = View.VISIBLE
    }

    private fun quitLoadingScreen(){
        newChatButton.alpha = 1F
        newChatButton.isEnabled = true
        ChatsRecyclerView.alpha = 1F
        ChatsRecyclerView.isEnabled = true
        ChatsProgressBar.visibility = View.GONE
    }
}