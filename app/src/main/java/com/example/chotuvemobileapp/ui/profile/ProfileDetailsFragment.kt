package com.example.chotuvemobileapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.helpers.Utilities.BIRTH_DATE
import com.example.chotuvemobileapp.helpers.Utilities.EMAIL
import com.example.chotuvemobileapp.helpers.Utilities.FIRST_NAME
import com.example.chotuvemobileapp.helpers.Utilities.LAST_NAME
import com.example.chotuvemobileapp.helpers.Utilities.USERNAME
import kotlinx.android.synthetic.main.profile_view_fragment.*

class ProfileDetailsFragment : Fragment() {

    private var firstName: String = ""
    private var lastName: String = ""
    var email: String = ""
    private var birthDate: String = ""
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            firstName = it.getString(FIRST_NAME, "")
            lastName = it.getString(LAST_NAME, "")
            email = it.getString(EMAIL, "")
            birthDate = it.getString(BIRTH_DATE, "")
            username = it.getString(USERNAME, "")
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
                    putString(FIRST_NAME, firstName)
                    putString(LAST_NAME, lastName)
                    putString(EMAIL, email)
                    putString(BIRTH_DATE, birthDate)
                    putString(USERNAME, username)
                }
            }
    }
}
