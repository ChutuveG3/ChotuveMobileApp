package com.example.chotuvemobileapp.ui.friends

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.helpers.SearchedUsersAdapter
import kotlinx.android.synthetic.main.search_friends_fragment.*

class SearchFriendsFragment : Fragment() {

    private val prefs by lazy {
        requireActivity().getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(FriendsViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.search_friends_fragment, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotFoundText.visibility = View.GONE
        val username = prefs.getString("username", null)
        SearchFriendsExplainText.text = "Search by username, yours is $username"

        SearchFriendsToolbar.setOnClickListener {
            SearchFriendSearchView.isIconified = false
        }

        SearchFriendSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                SearchFriendSearchView.clearFocus()
                if (!query.isNullOrEmpty()){
                    ProfileInfoDataSource.getProfileInfo(prefs, query){
                        when(it){
                            null -> {
                                SearchFriendsRecyclerView.visibility = View.INVISIBLE
                                NotFoundText.visibility = View.VISIBLE
                            }
                            else -> {
                                val users = ArrayList<String>()
                                users.add(it.user_name)
                                SearchFriendsRecyclerView.adapter = SearchedUsersAdapter(users, prefs, viewModel)
                                SearchFriendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                                NotFoundText.visibility = View.GONE
                                SearchFriendsRecyclerView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }
}