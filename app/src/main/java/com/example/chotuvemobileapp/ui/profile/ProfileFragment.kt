package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : Fragment() {

    private lateinit var mPager: ViewPager2
    lateinit var firstName: String
    lateinit var lastName: String
    lateinit var email: String
    lateinit var birthDate: String
    private lateinit var userName: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ProfileScreen.alpha = .2F
        ProfileScreen.isClickable = false
        ProfileProgressBar.visibility = View.VISIBLE
        val token = requireActivity().applicationContext
            .getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE).getString("token", "Fail")
        mPager = ProfileViewPager
        ProfileInfoDataSource.getProfileInfo(token!!){
            if(it != null){
                firstName = it.first_name
                lastName = it.last_name
                userName = it.user_name
                email = it.email
                birthDate = it.birthdate
            }
            else{
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
            ProfileScreen.alpha = 1F
            ProfileScreen.isClickable = true
            ProfileProgressBar.visibility = View.GONE
        }
    }

    private inner class ScreenSlidePagerAdapter(fa: Fragment) : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = 3
        override fun createFragment(position: Int): Fragment {
            return when(position){
                0 -> {
                    ProfileDetailsFragment.newInstance(firstName, lastName, email, birthDate)
                }
                else -> ProfileDetailsFragment()
            }
        }
    }
}
