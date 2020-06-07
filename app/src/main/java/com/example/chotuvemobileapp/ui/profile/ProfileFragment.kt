package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.ui.CommentsFragment
import com.example.chotuvemobileapp.viewmodels.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mPager: ViewPager2
    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ProfileViewModel.getInstance(prefs)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoadingScreen()
        mPager = ProfileViewPager
        if (viewModel.userInfo.value == null) viewModel.getUserInfo()
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            val nameToDisplay = "${it.first_name} ${it.last_name}"
            NameTextView.text = nameToDisplay
            UsernameTextView.text = it.user_name
            val pagerAdapter = ScreenSlidePagerAdapter(this)
            mPager.adapter = pagerAdapter
            TabLayoutMediator(ProfileTabLayout, mPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.profile_details)
                    1 -> tab.text = getString(R.string.videos)
                    else -> tab.text = getString(R.string.video_comments)
                }
            }.attach()
            quitLoadingScreen()
        })
        ProfilePic.setOnClickListener {
            findNavController().navigate(R.id.action_nav_gallery_to_fullSizeImageFragment)
        }
        openDrawer.setOnClickListener {
            val home = activity as HomeActivity
            home.openDrawer()
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ProfileDetailsFragment()
                1 -> VideoListFragment.newInstance("Me")
                else -> CommentsFragment.newInstance(19)
            }
        }
    }

    private fun quitLoadingScreen() {
        ProfileAppbar.alpha = 1F
        ProfileScrollView.alpha = 1F
        ProfilePic.isClickable = true
        ProfileProgressBar.visibility = View.GONE
    }

    private fun showLoadingScreen() {
        ProfileAppbar.alpha = .2F
        ProfileScrollView.alpha = .2F
        ProfilePic.isClickable = false
        ProfileProgressBar.visibility = View.VISIBLE
    }
}
