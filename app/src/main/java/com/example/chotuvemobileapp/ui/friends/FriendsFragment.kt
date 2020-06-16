package com.example.chotuvemobileapp.ui.friends

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.ui.ListFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_friends.*

class FriendsFragment : Fragment() {

    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(FriendsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_friends, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FriendsToolbar.setNavigationOnClickListener {
            val home=  activity as HomeActivity
            home.openDrawer()
        }
        viewModel.setPrefs(prefs)
        FriendsToolbar.title = prefs.getString("username", "")
        FriendsViewPager.adapter = FriendsPagerAdapter(this)
        viewModel.friends.observe(viewLifecycleOwner, Observer {
            TabLayoutMediator(FriendsTabLayout, FriendsViewPager) {tab, position ->
                when (position){
                    0 -> tab.text = getString(R.string.profile_friends)
                    else -> tab.text = getString(R.string.friends_pending)
                }
            }.attach()
        })
    }

    private inner class FriendsPagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ListFragment.newInstance(false)
                else -> ListFragment.newInstance(true)
            }
        }
    }
}
