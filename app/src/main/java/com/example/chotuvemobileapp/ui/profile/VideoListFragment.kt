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
    private var loadedAllVideos = false
    private val pageSize = 20
    private val progressBar by lazy { VideoListProgressBar }
    private val recyclerView by lazy { VideoListRecyclerView }
    private val prefs by lazy {
        requireActivity().getSharedPreferences(getString(R.string.shared_preferences_file), Context.MODE_PRIVATE)
    }

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

        progressBar.visibility = View.VISIBLE
        recyclerView.alpha = 0.2F

        VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user){
            videos.addAll(it)
            recyclerView.adapter!!.notifyItemRangeInserted(recyclerView.adapter!!.itemCount, it.count())
            progressBar.visibility = View.GONE
            recyclerView.alpha = 1F
        }
        recyclerView.adapter = VideosAdapter(videos)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.addOnScrollListener(
            object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(!recyclerView.canScrollVertically(1) && !loadedAllVideos){
                        currentPage += 1
                        VideoDataSource.getVideosFrom(prefs, currentPage, pageSize, user){
                            videos.addAll(it)
                            recyclerView.adapter!!.notifyItemRangeInserted(recyclerView.adapter!!.itemCount, it.count())
                            if (it.isEmpty()) loadedAllVideos = true
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
