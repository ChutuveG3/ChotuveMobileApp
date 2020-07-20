package com.example.chotuvemobileapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_messages.*

class MessagesFragment : Fragment() {

    private val database by lazy {FirebaseDatabase.getInstance().reference }
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
    }
}