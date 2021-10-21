package com.nipunapps.vsan.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nipunapps.vsan.data.remote.VideoService
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.ui.MainActivity
import com.nipunapps.vsan.utils.NetworkProvider
import com.nipunapps.vsan.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _videos = MutableLiveData<Resource<List<Video>>>(Resource.loading(null))
    private val service = VideoService.create()
    private val _network = MutableLiveData(true)
    val network = _network
    val videos = _videos
    init{

    }

    fun fetchVideos(){
        _videos.postValue(Resource.loading(null))
        viewModelScope.launch {
            _videos.postValue(service.getVideos())
        }
    }

    fun setNoNetworkError(){
        _videos.postValue(Resource.error("No Network Connection",null))
    }
}