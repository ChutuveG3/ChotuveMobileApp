package com.example.chotuvemobileapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.ui.profile.VideoListFragment
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AddVideo.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_addVideoFragment)
        }
        SearchIcon.setOnSearchClickListener {
            HomeToolbar.chotuveLogo.visibility = View.GONE
        }
        SearchIcon.setOnCloseListener {
            HomeToolbar.chotuveLogo.visibility = View.VISIBLE
            return@setOnCloseListener false
        }

        childFragmentManager.beginTransaction().replace(R.id.HomeVideosFragment, VideoListFragment.newInstance()).commit()
    }
}
