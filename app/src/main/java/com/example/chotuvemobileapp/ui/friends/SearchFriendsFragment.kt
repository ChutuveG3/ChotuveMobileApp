package com.example.chotuvemobileapp.ui.friends

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.repositories.FriendsDataSource
import com.example.chotuvemobileapp.helpers.SearchedUsersAdapter
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.viewmodels.FriendsViewModel
import kotlinx.android.synthetic.main.fragment_list.*
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        NotFoundText.visibility = View.GONE
        SadFaceImage.visibility = View.GONE
        val username = prefs.getString(USERNAME, null)
        val textToShow = "Search by username, yours is $username"
        SearchFriendsExplainText.text = textToShow

        SearchFriendsToolbar.setOnClickListener {
            SearchFriendSearchView.isIconified = false
        }

        SearchFriendSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                SearchFriendSearchView.clearFocus()
                if (!query.isNullOrEmpty()){
                    FriendsDataSource.getSimilarUsers(prefs, query){
                        when {
                            it == null || it.count() == 0 -> {
                                SearchFriendsRecyclerView.visibility = View.INVISIBLE
                                SadFaceImage.visibility = View.VISIBLE
                                NotFoundText.visibility = View.VISIBLE
                            }
                            else -> {
                                SearchFriendsRecyclerView.adapter = SearchedUsersAdapter(it, prefs, viewModel)
                                SearchFriendsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
                                NotFoundText.visibility = View.GONE
                                SearchFriendsRecyclerView.visibility = View.VISIBLE
                                SearchFriendsRecyclerView.addItemDecoration(DividerItemDecoration(
                                    SearchFriendsRecyclerView.context,
                                    resources.configuration.orientation)
                                )
                            }
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                NotFoundText.visibility = View.GONE
                SadFaceImage.visibility = View.GONE
                return true
            }
        })
    }
}