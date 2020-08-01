package com.example.chotuvemobileapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.helpers.ContactsAdapter
import com.example.chotuvemobileapp.helpers.FriendsAdapter
import com.example.chotuvemobileapp.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.activity_select_friend.*
import kotlinx.android.synthetic.main.fragment_list.*

class SelectFriendActivity : AppCompatActivity() {

    private val prefs by lazy {
        getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val friendsViewModel by lazy {
        ViewModelProvider(this).get(FriendsViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_friend)

        friendsViewModel.setPrefs(prefs)
        ContactsNotFoundImage.visibility = View.GONE
        ContactsNotFoundTextView.visibility = View.GONE
        showLoadingScreen()

        SelectFriendToolbar.setNavigationOnClickListener {
            this.onBackPressed()
        }

        SelectFriendRecyclerView.addItemDecoration(DividerItemDecoration(
            SelectFriendRecyclerView.context,
            resources.configuration.orientation)
        )

        friendsViewModel.friends.observe(this, Observer {
            SelectFriendRecyclerView.adapter = ContactsAdapter(it)
            SelectFriendRecyclerView.layoutManager = LinearLayoutManager(this)
            if (it.isEmpty()){
                ContactsNotFoundImage.visibility = View.VISIBLE
                ContactsNotFoundTextView.visibility = View.VISIBLE
            }
            quitLoadingScreen()
        })

    }

    private fun showLoadingScreen() {
        SelectFriendRecyclerView.isClickable = false
        SelectFriendProgressBar.visibility = View.VISIBLE
        SelectFriendRecyclerView.alpha = 0.2F
    }

    private fun quitLoadingScreen() {
        SelectFriendRecyclerView.isClickable = true
        SelectFriendProgressBar.visibility = View.GONE
        SelectFriendRecyclerView.alpha = 1F
    }
}