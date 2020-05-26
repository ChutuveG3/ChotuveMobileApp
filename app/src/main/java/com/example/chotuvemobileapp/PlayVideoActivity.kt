package com.example.chotuvemobileapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chotuvemobileapp.entities.CommentItem
import com.example.chotuvemobileapp.helpers.CommentsAdapter
import com.example.chotuvemobileapp.ui.CommentsFragment
import com.example.chotuvemobileapp.ui.EmptyListFragment
import kotlinx.android.synthetic.main.activity_play_video.*

class PlayVideoActivity : AppCompatActivity() {

    private var nComments = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

        VideoTitle.text = intent.getStringExtra("videoTitle")
        VideoAuthor.text = intent.getStringExtra("videoAuthor")

        val description = (intent.getStringExtra("videoDate")!!).plus("\nEsto es una descripción recontra hadcodeada para ver que onda esto de reproducir un video\nViva Perón!!")

        VideoDescription.text = description

        nComments = intent.getIntExtra("comments", 0)

        if (nComments == 0) supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, EmptyListFragment()).commit()
        else supportFragmentManager.beginTransaction().replace(R.id.VideoCommentsFragment, CommentsFragment.newInstance(nComments)).commit()

    }
}
