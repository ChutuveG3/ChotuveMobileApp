package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.users.ProfileInfoDataSource
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.profile_view_fragment.*

class ProfileFragment : Fragment() {

    private lateinit var mPager: ViewPager2
    var firstName: String = ""
    var lastName: String = ""
    var email: String = ""
    var birthDate: String = ""
    var userName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = requireActivity().applicationContext
            .getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE).getString("token", "Fail")
        ProfileInfoDataSource.getProfileInfo(token!!){
            if(it != null){
                firstName = it.first_name
                lastName = it.last_name
                userName = it.user_name
                email = it.email
                birthDate = it.birthdate
            }
        }
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
        mPager = ProfileViewPager
        val pagerAdapter = ScreenSlidePagerAdapter(this)
        mPager.adapter = pagerAdapter

        val nameToDisplay = "Nombre Apellido"
        NameTextView.text = nameToDisplay
        UsernameTextView.text = "usuario"

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
