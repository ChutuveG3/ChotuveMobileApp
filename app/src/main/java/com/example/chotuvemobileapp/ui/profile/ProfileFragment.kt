package com.example.chotuvemobileapp.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.example.chotuvemobileapp.HomeActivity
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.helpers.Utilities
import com.example.chotuvemobileapp.viewmodels.ProfileViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
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
        viewModel.setPrefs(prefs)
        viewModel.userInfo.observe(viewLifecycleOwner, Observer {
            val nameToDisplay = "${it.first_name} ${it.last_name}"
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
                val intent = Intent(requireContext(), FullSizeImageActivity::class.java)
                intent.putExtra(Utilities.PIC_URL, it.profile_img_url)
                startActivity(intent)
            }
        })
        openDrawer.setOnClickListener {
            val home = activity as HomeActivity
            home.openDrawer()
        }
        AddFriendButton.setOnClickListener {
            val intent = Intent(context, EditProfileActivity::class.java)
            intent.putExtra(Utilities.FIRST_NAME, viewModel.userInfo.value!!.first_name)
            intent.putExtra(Utilities.LAST_NAME, viewModel.userInfo.value!!.last_name)
            intent.putExtra(Utilities.EMAIL, viewModel.userInfo.value!!.email)
            intent.putExtra(Utilities.BIRTH_DATE, viewModel.userInfo.value!!.birthdate)
            intent.putExtra(Utilities.PIC_URL, viewModel.userInfo.value?.profile_img_url)
            startActivityForResult(intent, Utilities.REQUEST_CODE_EDIT_PROFILE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Utilities.REQUEST_CODE_EDIT_PROFILE && resultCode == Activity.RESULT_OK){
            viewModel.updateUserInfo(
                data!!.getStringExtra(Utilities.FIRST_NAME)!!,
                data.getStringExtra(Utilities.LAST_NAME)!!,
                data.getStringExtra(Utilities.BIRTH_DATE)!!,
                data.getStringExtra(Utilities.EMAIL)!!,
                data.getStringExtra(Utilities.PIC_URL))
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 2
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> ProfileDetailsFragment.newInstance(
                    viewModel.userInfo.value!!.first_name,
                    viewModel.userInfo.value!!.last_name,
                    viewModel.userInfo.value!!.email,
                    viewModel.userInfo.value!!.birthdate,
                    viewModel.userInfo.value!!.user_name)
                else -> VideoListFragment.newInstance("Me")
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

    override fun onDestroy() {
        super.onDestroy()
        viewModelStore.clear()
    }
}
