package com.nipunapps.vsan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.data.remote.repo.GetVideos
import com.nipunapps.vsan.data.room.VideoEntity
import com.nipunapps.vsan.data.room.VideoItem
import com.nipunapps.vsan.data.room.VideoRepository
import com.nipunapps.vsan.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _videos = MutableLiveData<Resource<ArrayList<VideoItem>>>(Resource.loading(null))
    private val videoRepository = VideoRepository(application)
    val videos = _videos
    init{
        fetchData()
    }

    fun fetchData(){
        CoroutineScope(Dispatchers.Unconfined).launch {
           videoRepository.initialiseVideos()
        }
    }
     fun fetchVideos(){
         CoroutineScope(Dispatchers.IO).launch {
           _videos.postValue(videoRepository.getVideos())
        }
    }
}