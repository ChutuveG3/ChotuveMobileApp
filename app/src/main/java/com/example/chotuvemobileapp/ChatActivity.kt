package com.example.chotuvemobileapp

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.data.repositories.ChatsDataSource
import com.example.chotuvemobileapp.data.repositories.ProfileInfoDataSource
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.entities.ChatMessageItem
import com.example.chotuvemobileapp.helpers.ChatMessageViewHolder
import com.example.chotuvemobileapp.helpers.Utilities.PIC_URL
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.getChatId
import com.example.chotuvemobileapp.helpers.Utilities.parseTime
import com.example.chotuvemobileapp.helpers.Utilities.sizeInPx
import com.example.chotuvemobileapp.ui.profile.FullSizeImageActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat.*
import java.time.LocalDateTime
import java.time.ZoneId

class ChatActivity : AppCompatActivity() {
    /*
    * DB Structure
    *
    * messages
    *   chat_id_1:
    *       msj1: {
    *               'user': ...
    *               'message': ...
    *               'timestamp': ...
    *            }
    *       msj2: { ... }
    *   chat_id_2
    *   ....
    *
    * chats
    *   chat_id_1: {
    *       user: ...
    *       lastMessage: ...,
    *       'timestamp': ...
    *
    *   }
    *
    *   chat_id_2 { ... }
    * */

    private val database by lazy { FirebaseDatabase.getInstance().reference }

    private val prefs by lazy { getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE) }

    private val ownUsername by lazy { prefs.getString(USERNAME, "")!! }

    private val destinationUsername by lazy { intent.getStringExtra(USERNAME)}

    private val chatId by lazy { getChatId(ownUsername, destinationUsername) }

    private val messagesReference by lazy { database.child("messages").child(chatId) }

    private val messageQuery by lazy { messagesReference.orderByChild("timestamp") }

    private val options by lazy {
        FirebaseRecyclerOptions
            .Builder<ChatMessageItem>()
            .setQuery(messageQuery, ChatMessageItem::class.java)
            .build()
    }
    private val adapter by lazy {
        object : FirebaseRecyclerAdapter<ChatMessageItem, ChatMessageViewHolder>(options) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ChatMessageViewHolder {
                val context = parent.context
                val inflater = LayoutInflater.from(context)
                val messageView = inflater.inflate(R.layout.item_message, parent, false)
                return ChatMessageViewHolder(messageView)
            }

            override fun onBindViewHolder(
                holder: ChatMessageViewHolder,
                position: Int,
                model: ChatMessageItem
            ) {
                holder.message.text = model.message
                holder.messageTimestamp.text = parseTime(model.timestamp)
                val density = resources.displayMetrics.density
                val constraintSet = ConstraintSet()
                constraintSet.clone(holder.layout)

                val messageBubbleConstraintSet = ConstraintSet()
                messageBubbleConstraintSet.clone(holder.messageBubble)

                if (model.user != ownUsername) {
                    holder.messageBubble.background =
                        holder.itemView.context.getDrawable(R.drawable.message_incoming_bubble)
                    holder.layout.setPadding(
                        sizeInPx(0, density),
                        sizeInPx(0, density),
                        sizeInPx(10, density),
                        sizeInPx(0, density)
                    )

                    holder.messageBubble.setPadding(
                        sizeInPx(20, density),
                        sizeInPx(4, density),
                        sizeInPx(10, density),
                        sizeInPx(6, density)
                    )
                    constraintSet.setHorizontalBias(R.id.MessageBubble, 0f)
                    messageBubbleConstraintSet.setHorizontalBias(R.id.MessageTimestamp, 0f)
                    messageBubbleConstraintSet.applyTo(holder.messageBubble)
                    constraintSet.applyTo(holder.layout)
                }
                else{
                    holder.messageBubble.background =
                        holder.itemView.context.getDrawable(R.drawable.message_outgoing_bubble)
                    holder.layout.setPadding(
                        sizeInPx(10, density),
                        sizeInPx(0, density),
                        sizeInPx(0, density),
                        sizeInPx(0, density)
                    )
                    holder.messageBubble.setPadding(
                        sizeInPx(10, density),
                        sizeInPx(4, density),
                        sizeInPx(20, density),
                        sizeInPx(6, density)
                    )
                    constraintSet.setHorizontalBias(R.id.MessageBubble, 1f)
                    messageBubbleConstraintSet.setHorizontalBias(R.id.MessageTimestamp, 1f)
                    messageBubbleConstraintSet.applyTo(holder.messageBubble)
                    constraintSet.applyTo(holder.layout)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        adapter.startListening()

        var picUrl = intent.getStringExtra(PIC_URL)

        MessageEditText.clearFocus()
        ChatToolbar.setNavigationOnClickListener { this.onBackPressed() }
        ChatToolbar.title = destinationUsername

        ChatToolbar.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(USERNAME, destinationUsername)
            startActivity(intent)
        }

        if (picUrl != null){
            Glide
                .with(this)
                .load(Uri.parse(picUrl))
                .centerCrop()
                .into(UserPicChat)
        }
        else {
            ProfileInfoDataSource.getProfileInfo(prefs, destinationUsername) {
                if (it?.profile_img_url != null) {
                    Glide
                        .with(this)
                        .load(Uri.parse(it.profile_img_url))
                        .centerCrop()
                        .into(UserPicChat)
                    picUrl = it.profile_img_url
                }
            }
        }
        MessagesRecyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        MessagesRecyclerView.layoutManager = layoutManager

        RecyclerWrapper.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            MessagesRecyclerView.smoothScrollToPosition(MessagesRecyclerView.adapter!!.itemCount)
        }

        MessageSendButton.setOnClickListener{
            val messageText = MessageEditText.text.toString().trim()

            if (messageText.isBlank()) return@setOnClickListener

            val timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
            val newMessage = ChatMessageItem(ownUsername, messageText, timestamp)
            messagesReference.push().setValue(newMessage)
            val ownChat = ChatItem(chatId, destinationUsername, messageText, -timestamp, picUrl)
            val destinationChat = ChatItem(chatId, ownUsername, messageText, -timestamp)
            database
                .child("chats")
                .child(ownUsername)
                .child(destinationUsername)
                .setValue(ownChat)
            database
                .child("chats")
                .child(destinationUsername)
                .child(ownUsername)
                .setValue(destinationChat)
            MessageEditText.text.clear()
            MessagesRecyclerView.smoothScrollToPosition(MessagesRecyclerView.adapter!!.itemCount)
            ChatsDataSource.sendMessage(prefs, destinationUsername, messageText)
        }

        UserPicChat.setOnClickListener {
            val intent = Intent(this, FullSizeImageActivity::class.java)
            intent.putExtra(PIC_URL, picUrl)
            val options = ActivityOptions.makeSceneTransitionAnimation(this, UserPicWrapperChat, "profilePic")
            startActivity(intent, options.toBundle())
        }
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
    override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
}