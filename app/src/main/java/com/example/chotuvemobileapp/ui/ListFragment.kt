package com.example.chotuvemobileapp.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.helpers.FriendsAdapter
import com.example.chotuvemobileapp.ui.friends.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_list.*

class ListFragment : Fragment() {

    private var users = ArrayList<String>()
    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val friendsViewModel by lazy {
        ViewModelProvider(requireActivity()).get(FriendsViewModel::class.java)
    }
    private var pending: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pending = it.getBoolean("type")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendsViewModel.setPrefs(prefs)
        showLoadingScreen()
        val variableToObserve = if (pending) friendsViewModel.pendingFriends else friendsViewModel.friends
        ListRecyclerView.adapter = FriendsAdapter(users, prefs, friendsViewModel, pending)
        ListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        variableToObserve.observe(viewLifecycleOwner, Observer {
            users.addAll(it)
            ListRecyclerView.adapter!!.notifyItemRangeInserted(0, it.count())
            quitLoadingScreen()
        })
    }

    private fun showLoadingScreen() {
        ListRecyclerView.isClickable = false
        ListProgressBar.visibility = View.VISIBLE
        ListRecyclerView.alpha = 0.2F
    }

    private fun quitLoadingScreen() {
        ListRecyclerView.isClickable = true
        ListProgressBar.visibility = View.GONE
        ListRecyclerView.alpha = 1F
    }
    companion object {
        @JvmStatic
        fun newInstance(pending: Boolean) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean("type", pending)
                }
            }
    }
}