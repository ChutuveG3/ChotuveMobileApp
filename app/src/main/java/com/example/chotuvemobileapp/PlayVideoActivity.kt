package com.example.chotuvemobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.entities.CommentItem
import com.example.chotuvemobileapp.helpers.CommentsAdapter
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity() {

    private lateinit var comments: ArrayList<CommentItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        val dummyComments = ArrayList<CommentItem>()
        for (i in 1..16){
            dummyComments.add(CommentItem("Soy el usuario $i y opino que este video es una poronga, que quilombo es armar todo esto lpm", "User $i", "$i/$i/$i"))
        }
        comments = dummyComments

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")

        val description = (intent.getStringExtra("videoDate")!!).plus("\nEsto es una descripción recontra hadcodeada para ver que onda esto de reproducir un video\nViva Perón!!")

        VideoDescription.text = description
        VideoCommentsRecyclerView.adapter = CommentsAdapter(comments)
        VideoCommentsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
}
