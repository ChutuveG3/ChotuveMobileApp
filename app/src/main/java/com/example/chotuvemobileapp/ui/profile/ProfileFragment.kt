package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import com.example.chotuvemobileapp.ui.CommentsFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mPager: ViewPager2
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var email: String
    lateinit var birthDate: String
    private lateinit var userName: String
    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
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
        startFragment()
        ProfilePic.setOnClickListener {
            findNavController().navigate(R.id.action_nav_gallery_to_fullSizeImageFragment)
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ProfileDetailsFragment.newInstance(firstName, lastName, email, birthDate, userName)
                1 -> VideoListFragment.newInstance("Me")
                else -> CommentsFragment.newInstance(19)
            }
        }
    }

    private fun startFragment() {
        ProfileAppbar.alpha = .2F
        ProfileScrollView.alpha = .2F
        ProfilePic.isClickable = false
        ProfileProgressBar.visibility = View.VISIBLE

        mPager = ProfileViewPager
        ProfileInfoDataSource.getProfileInfo(preferences = prefs) {
            if (it != null) {
                firstName = it.first_name
                lastName = it.last_name
                userName = it.user_name
                email = it.email
                birthDate = it.birthdate
            } else {
                firstName = ""
                lastName = ""
                userName = ""
                email = ""
                birthDate = ""
            }
            val nameToDisplay = "$firstName $lastName"
            NameTextView.text = nameToDisplay
            UsernameTextView.text = userName
            val pagerAdapter = ScreenSlidePagerAdapter(this)
            mPager.adapter = pagerAdapter
            TabLayoutMediator(ProfileTabLayout, mPager) { tab, position ->
                when (position) {
                    0 -> tab.text = getString(R.string.profile_details)
                    1 -> tab.text = getString(R.string.videos)
                    else -> tab.text = getString(R.string.video_comments)
                }
            }.attach()
            ProfileAppbar.alpha = 1F
            ProfileScrollView.alpha = 1F
            ProfilePic.isClickable = true
            ProfileProgressBar.visibility = View.GONE
            openDrawer.setOnClickListener {
                val home=  activity as HomeActivity
                home.openDrawer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startFragment()
    }
}
