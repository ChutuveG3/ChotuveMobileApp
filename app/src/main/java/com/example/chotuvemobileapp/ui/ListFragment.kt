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
import com.example.chotuvemobileapp.helpers.Utilities
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
    private lateinit var message: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pending = it.getBoolean(Utilities.TYPE)
            message = it.getString(Utilities.NOT_FOUND_MESSAGE, "")
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
        FriendsNotFoundTextView.visibility = View.GONE
        NotFoundImage.visibility = View.GONE
        showLoadingScreen()
        val variableToObserve = if (pending) friendsViewModel.pendingFriends else friendsViewModel.friends
        ListRecyclerView.adapter = FriendsAdapter(users, prefs, friendsViewModel, pending)
        ListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        variableToObserve.observe(viewLifecycleOwner, Observer {
            users = it
            ListRecyclerView.adapter = FriendsAdapter(users, prefs, friendsViewModel, pending)
            if (it.isEmpty()){
                NotFoundImage.setImageDrawable(requireContext().getDrawable(R.drawable.ic_campana))
                NotFoundImage.alpha = 0.5F
                NotFoundImage.visibility = View.VISIBLE
                ListRecyclerView.visibility = View.GONE
                FriendsNotFoundTextView.text = message
                FriendsNotFoundTextView.visibility = View.VISIBLE
            }
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
        fun newInstance(pending: Boolean, message: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Utilities.TYPE, pending)
                    putString(Utilities.NOT_FOUND_MESSAGE, message)
                }
            }
    }
}