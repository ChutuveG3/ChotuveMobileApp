package com.example.chotuvemobileapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chotuvemobileapp.R
import kotlinx.android.synthetic.main.profile_view_fragment.*

class ProfileDetailsFragment : Fragment() {

    private var firstName: String = ""
    private var lastName: String = ""
    var email: String = ""
    private var birthDate: String = ""
    private var username = ""
    private var ownProfile = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            firstName = it.getString("firstName", "")
            lastName = it.getString("lastName", "")
            email = it.getString("email", "")
            birthDate = it.getString("birthDate", "")
            username = it.getString("username", "")
        }
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

        FirstNameText.text = firstName
        LastNameText.text = lastName
        EmailText.text = email
        DOBText.text = birthDate
    }

    companion object {
        @JvmStatic
        fun newInstance(firstName: String, lastName: String, email: String, birthDate: String, username: String) =
            ProfileDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("email", email)
                    putString("birthDate", birthDate)
                    putString("username", username)
                }
            }
    }
}
