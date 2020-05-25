package com.example.chotuvemobileapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.entities.VideoItem
import com.example.chotuvemobileapp.helpers.VideosAdapter
import kotlinx.android.synthetic.main.fragment_profile_videos.*

class ProfileVideosFragment : Fragment() {
    private lateinit var videos: ArrayList<VideoItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyVideos = ArrayList<VideoItem>()
        for (i in 1..6){
            dummyVideos.add(VideoItem("Video $i", "User $i", "$i/$i/$i"))
        }
        videos = dummyVideos

        ProfileVideoRecyclerView.adapter = VideosAdapter(videos)
        ProfileVideoRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }
}
