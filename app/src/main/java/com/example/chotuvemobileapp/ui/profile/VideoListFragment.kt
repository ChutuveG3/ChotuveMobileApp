package com.example.chotuvemobileapp.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.videos.VideoDataSource
import com.example.chotuvemobileapp.entities.VideoItem
import com.example.chotuvemobileapp.helpers.VideosAdapter
import kotlinx.android.synthetic.main.fragment_profile_videos.*

class VideoListFragment : Fragment() {
    private var videos = ArrayList<VideoItem>()
    private var nVideos: Int = 0
    private var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nVideos = it.getInt("videos", 0)
            user = it.getString("user")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar = VideoListProgressBar
        progressBar.visibility = View.VISIBLE
        VideoListRecyclerView.alpha = 0.2F

        val recyclerView = VideoListRecyclerView
        VideoDataSource.getVideosFrom(user){
            videos = it
            recyclerView.adapter = VideosAdapter(videos)
            progressBar.visibility = View.GONE
            recyclerView.alpha = 1F
        }
        recyclerView.adapter = VideosAdapter(videos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    companion object {
        @JvmStatic
        fun newInstance(i: Int, user: String? = null) =
            VideoListFragment().apply {
                arguments = Bundle().apply {
                    putInt("videos", i)
                    putString("user", user)
                }
            }
    }
}
