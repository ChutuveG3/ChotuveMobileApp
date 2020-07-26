package com.example.chotuvemobileapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.entities.ChatMessageItem
import com.example.chotuvemobileapp.helpers.ChatMessageViewHolder
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.helpers.Utilities.getChatId
import com.example.chotuvemobileapp.helpers.Utilities.sizeInPx
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

    private val ownUsername by lazy {
        getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            .getString(USERNAME, "")!!
    }

    private val destinationUsername by lazy { intent.getStringExtra(USERNAME)}

    private val chatId by lazy {
        getChatId(ownUsername, destinationUsername)
    }

    private val messagesReference by lazy {
        database.child("messages").child(chatId)
    }

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
                if (model.user != ownUsername) {
                    val density = resources.displayMetrics.density
                    holder.message.background =
                        holder.itemView.context.getDrawable(R.drawable.message_incoming_bubble)
                    holder.message.setPadding(
                        sizeInPx(20, density),
                        sizeInPx(4, density),
                        sizeInPx(10, density),
                        sizeInPx(6, density)
                    )
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(holder.layout)
                    constraintSet.setHorizontalBias(R.id.MessageText, 0f)
                    constraintSet.applyTo(holder.layout)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        ChatToolbar.setNavigationOnClickListener { this.onBackPressed() }

        ChatToolbar.title = destinationUsername

        MessagesRecyclerView.adapter = adapter
        MessagesRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.startListening()

        MessageSendButton.setOnClickListener{
            val messageText = MessageEditText.text.toString()

            if (messageText.isBlank()) return@setOnClickListener

            val timestamp = LocalDateTime.now().atZone(ZoneId.systemDefault()).toEpochSecond()
            val newMessage = ChatMessageItem(ownUsername, messageText, timestamp)
            messagesReference.push().setValue(newMessage)
            val ownChat = ChatItem(chatId, destinationUsername, messageText, timestamp)
            val destinationChat = ChatItem(chatId, ownUsername, messageText, timestamp)
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
        }
    }
}