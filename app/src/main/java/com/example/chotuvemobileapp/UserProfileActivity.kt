package com.example.chotuvemobileapp

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.data.repositories.FriendsDataSource
import com.example.chotuvemobileapp.data.repositories.ProfileInfoDataSource
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.helpers.Utilities.SUCCESS_MESSAGE
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import com.example.chotuvemobileapp.ui.profile.FullSizeImageActivity
import com.example.chotuvemobileapp.ui.profile.ProfileDetailsFragment
import com.example.chotuvemobileapp.ui.profile.VideoListFragment
import com.example.chotuvemobileapp.viewmodels.UserProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*

class UserProfileActivity : AppCompatActivity() {

    private val prefs by lazy {
        applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val username by lazy{
        intent.getStringExtra(USERNAME)
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(UserProfileViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)
        showLoadingScreen()
        viewModel.setPrefs(prefs)
        viewModel.setUser(username)
        openDrawer.setImageDrawable(getDrawable(R.drawable.ic_baseline_arrow_back_ios_36))
        viewModel.userInfo.observe(this, Observer {
            if (it != null){
                val nameToDisplay = "${it.first_name} ${it.last_name}"
                val drawable = when(it.friendship){
                    "yes"-> getDrawable(R.drawable.ic_account_check)
                    "pending" -> getDrawable(R.drawable.ic_account_clock)
                    else -> getDrawable(R.drawable.ic_account_plus)
                }
                AddFriendButton.setImageDrawable(drawable)
                AddFriendButton.setOnClickListener {_ ->
                    if (it.friendship == "no") {
                        showLoadingScreen()
                        FriendsDataSource.addFriend(prefs, username) { str ->
                            if (str == SUCCESS_MESSAGE) {
                                Toast.makeText(applicationContext,"Request sent to $username!", Toast.LENGTH_LONG).show()
                                AddFriendButton.setImageDrawable(getDrawable(R.drawable.ic_account_clock))
                            }
                            else Toast.makeText(applicationContext, getString(R.string.internal_error), Toast.LENGTH_LONG).show()
                            quitLoadingScreen()
                        }
                    }
                }

                NameTextView.text = nameToDisplay
                UsernameTextView.text = it.user_name
                ProfileViewPager.adapter = ScreenSlidePagerAdapter(this)
                TabLayoutMediator(ProfileTabLayout, ProfileViewPager) { tab, position ->
                    when (position) {
                        0 -> tab.text = getString(R.string.profile_details)
                        else -> tab.text = getString(R.string.videos)
                    }
                }.attach()
                if (it.profile_img_url != null) Glide.with(this).load(Uri.parse(it.profile_img_url)).centerCrop().into(ProfilePic)
                quitLoadingScreen()
                ProfilePic.setOnClickListener {_ ->
                    val intent = Intent(applicationContext, FullSizeImageActivity::class.java)
                    intent.putExtra(Utilities.PIC_URL, it.profile_img_url)
                    val options = ActivityOptions.makeSceneTransitionAnimation(this, ProfilePicWrapper, "profilePic")
                    startActivity(intent, options.toBundle())
                }
            }
        })

        if (username == prefs.getString(Utilities.USERNAME, "")!!) AddFriendButton.visibility = View.GONE
        openDrawer.setOnClickListener {
            super.onBackPressed()
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ProfileDetailsFragment.newInstance(
                    viewModel.userInfo.value!!.first_name,
                    viewModel.userInfo.value!!.last_name,
                    viewModel.userInfo.value!!.email,
                    viewModel.userInfo.value!!.birthdate,
                    username)
                else -> VideoListFragment.newInstance(username)
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