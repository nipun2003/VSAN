package com.nipunapps.vsan.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nipunapps.vsan.R
import com.nipunapps.vsan.adapters.VideoAdapter
import com.nipunapps.vsan.ui.viewmodel.MainViewModel
import com.nipunapps.vsan.utils.NetworkProvider
import com.nipunapps.vsan.utils.Status.*
import com.nipunapps.vsan.utils.StorageUtil
import com.nipunapps.vsan.utils.showToast
import java.lang.Exception

class VideoListFragment : Fragment(R.layout.video_list_frag) {
    private lateinit var viewModel: MainViewModel
    private lateinit var adapter: VideoAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var networkProvider : NetworkProvider
    private lateinit var storageUtil: StorageUtil
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
        initialiseView(view)
        subscribeToObserver()
    }

    private fun initialiseView(view: View) {
        recyclerView = view.findViewById(R.id.rvList)
        progressBar = view.findViewById(R.id.progress_bar)
        adapter = VideoAdapter(requireContext())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        networkProvider = NetworkProvider(requireContext())
        storageUtil = StorageUtil(requireContext())
    }


    private fun subscribeToObserver() {
        try {
            viewModel.videos.observe(viewLifecycleOwner, { result ->
                when (result.status) {
                    LOADING -> progressBar.visibility = View.VISIBLE
                    SUCCESS -> {
                        progressBar.visibility = View.GONE
                        result.data?.let { adapter.updateVideos(it) }
                    }
                    ERROR -> {
                        progressBar.visibility = View.GONE
                        if (storageUtil.getBoolean()) {
                            showToast(requireContext(), result.message!!)
                            storageUtil.storeBoolean(value = false)
                        }
                    }
                }
            })
        } catch (e: Exception) {
            Log.e("Nipun", e.message.toString())
        }
    }
}