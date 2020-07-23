package com.example.chotuvemobileapp.ui.home

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.ChatItem
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_messages.*

class MessagesFragment : Fragment() {

    private val database by lazy {FirebaseDatabase.getInstance().reference }
    private val username by lazy{
        requireActivity().applicationContext
            .getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
            .getString(USERNAME, "")!!
    }
    private val chatsReference by lazy {database.child("users").child(username)}
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MessagesToolbar.setNavigationOnClickListener{
            val home=  activity as HomeActivity
            home.openDrawer()
        }

        val chats = chatsReference.orderByChild("timestamp")
        val chatListener = object : ChildEventListener {
            override fun onCancelled(error: DatabaseError) =
                Toast.makeText(requireContext(), getString(R.string.internal_error), Toast.LENGTH_LONG).show()

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        }
    }
}