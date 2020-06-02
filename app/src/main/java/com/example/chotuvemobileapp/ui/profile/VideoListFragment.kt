package com.example.chotuvemobileapp.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chotuvemobileapp.R
import com.example.chotuvemobileapp.data.videos.VideoDataSource
import com.example.chotuvemobileapp.entities.VideoItem
import com.example.chotuvemobileapp.helpers.VideosAdapter
import kotlinx.android.synthetic.main.fragment_profile_videos.*

class VideoListFragment : Fragment() {
    private var videos = ArrayList<VideoItem>()
    private var user: String? = null
    private var currentPage = 1
    private val pageSize = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
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

        val prefs = requireActivity().getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)

        val recyclerView = VideoListRecyclerView
        VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user){
            videos = it
            recyclerView.adapter = VideosAdapter(videos)
            progressBar.visibility = View.GONE
            recyclerView.alpha = 1F
        }
        recyclerView.adapter = VideosAdapter(videos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(!recyclerView.canScrollVertically(1)){
                        currentPage += 1
                        VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user){
                            videos.addAll(it)
                            recyclerView.adapter!!.notifyItemRangeInserted(recyclerView.adapter!!.itemCount, it.count())
                        }
                    }
                }
            }
        )
    }

    companion object {
        @JvmStatic
        fun newInstance(user: String? = null) =
            VideoListFragment().apply {
                arguments = Bundle().apply {
                    putString("user", user)
                }
            }
    }
}
