package com.example.chotuvemobileapp.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.CommentItem
import com.example.chotuvemobileapp.helpers.CommentsAdapter
import kotlinx.android.synthetic.main.fragment_comments.*

class CommentsFragment : Fragment() {

    private var nComments = 0
    private lateinit var comments: ArrayList<CommentItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nComments = it.getInt("comments", 0)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comments, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dummyComments = ArrayList<CommentItem>()
        for (i in 1..nComments){
            dummyComments.add(CommentItem("Soy el usuario $i y opino que este video es una poronga, que quilombo es armar todo esto lpm", "User $i", "$i/$i/$i"))
        }
        comments = dummyComments
        CommentsRecyclerView.adapter = CommentsAdapter(comments)
        CommentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        @JvmStatic
        fun newInstance(i: Int) =
            CommentsFragment().apply {
                arguments = Bundle().apply {
                    putInt("comments", i)
                }
            }
    }
}
