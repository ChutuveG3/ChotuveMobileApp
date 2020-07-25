package com.example.chotuvemobileapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.entities.ChatMessageItem
import com.example.chotuvemobileapp.helpers.ChatMessagesAdapter
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.buttonEffect
import com.example.chotuvemobileapp.helpers.Utilities.getChatId
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_chat.*
import java.time.LocalDateTime

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
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val messagesReference by lazy { database.getReference("messages") }
    private val chatReference by lazy {
        messagesReference.child(getChatId(username, "otro"))
    }
    private val username by lazy {
        applicationContext
            .getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            .getString(Utilities.USERNAME, "")!!
    }
    private val adapter = ChatMessagesAdapter(ArrayList())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        MessagesRecyclerView.adapter = adapter
        MessagesRecyclerView.layoutManager = LinearLayoutManager(applicationContext)

        MessageSendButton.setOnClickListener{
            val messageText = MessageEditText.text.toString()
            val newMessage = ChatMessageItem(username, messageText, LocalDateTime.now())

            if (messageText.isNullOrBlank()) return@setOnClickListener

            chatReference.push().setValue(newMessage)
            // Esto no debe ir aca.
            adapter.addMessage(newMessage)
            MessageEditText.setText("")
        }

        // Esto me esta crasheando.

//        val chatListener = object : ChildEventListener {
//            override fun onCancelled(error: DatabaseError) =
//                Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
//
//            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
//            }
//
//            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
//                val newMessage : ChatMessageItem = snapshot.value as ChatMessageItem
//                adapter.addMessage(newMessage)
//            }
//
//            override fun onChildRemoved(snapshot: DataSnapshot) {
//            }
//        }
//        chatReference.addChildEventListener(chatListener)

        // Mepa que aca va messagesReference en vez de chatReference.
    }
}