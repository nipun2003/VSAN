package com.nipunapps.vsan.ui.fragment

import android.os.Bundle
import android.util.LayoutDirection
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nipunapps.vsan.R
import com.nipunapps.vsan.adapters.VideoAdapter
import com.nipunapps.vsan.data.room.VideoItem
import com.nipunapps.vsan.ui.viewmodel.MainViewModel
import com.nipunapps.vsan.utils.Resource
import com.nipunapps.vsan.utils.Status.*
import com.nipunapps.vsan.utils.loadImage
import com.nipunapps.vsan.utils.showToast
import com.nipunapps.vsan.utils.toVideo
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.Exception

class VideoListFragment : Fragment(R.layout.video_list_frag) {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: VideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var bannerImage: ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initialiseView(view)
        subscribeToObserver()
    }

    private fun initialiseView(view: View) {
        recyclerView = view.findViewById(R.id.rvList)
        progressBar = view.findViewById(R.id.progress_bar)
        bannerImage = view.findViewById(R.id.banner_image)
        adapter = VideoAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    private fun subscribeToObserver() {
        try {
            viewModel.videos.observe(viewLifecycleOwner, { result ->
                when (result.status) {
                    LOADING -> progressBar.visibility = View.VISIBLE
                    SUCCESS -> {
                        if (result.data?.isEmpty() == true){
                            viewModel.fetchVideos()
                        }
                        else {
                            result.data?.let {
                                if (it.isNotEmpty()) {
                                    loadImage(requireContext(), it[0].metaData.imageLink, bannerImage)
                                    progressBar.visibility = View.GONE
                                }
                                adapter.updateVideos(it)
                            }
                        }
                    }
                    ERROR -> {
                        progressBar.visibility = View.GONE
                        showToast(requireContext(), result.message!!)

                    }
                }
            })
        } catch (e: Exception) {
            Log.e("Nipun", e.message.toString())
        }
    }
}