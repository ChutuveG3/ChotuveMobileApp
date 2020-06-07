package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.profile_view_fragment.*

class ProfileDetailsFragment : Fragment() {

    private val prefs by lazy {
        requireActivity().applicationContext.getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }
    private val viewModel by lazy {
        ProfileViewModel.getInstance(prefs)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirstNameText.text = viewModel.getUserInfo().value!!.first_name
        LastNameText.text = viewModel.getUserInfo().value!!.last_name
        EmailText.text = viewModel.getUserInfo().value!!.email
        DOBText.text = viewModel.getUserInfo().value!!.birthdate

        GoToEditProfileButton.setOnClickListener{
            startActivity(Intent(context, EditProfileActivity::class.java))
        }
    }
}
