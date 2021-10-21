package com.nipunapps.vsan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.utils.PlayBackStatus
import com.nipunapps.vsan.utils.PlayBackStatus.*
import com.nipunapps.vsan.utils.logError
import kotlinx.coroutines.launch

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    private val _plabackStatus = MutableLiveData(PREPARING)
    val playBackStatus: LiveData<PlayBackStatus> = _plabackStatus
    private val _maxDuration = MutableLiveData<Long>()
    val maxDuration: LiveData<Long> = _maxDuration
    private val _curDuration = MutableLiveData<Long>()
    val curDuration: LiveData<Long> = _curDuration
    private val _curPlayingPosition = MutableLiveData<Int>()
    val curPlayingPosition: LiveData<Int> = _curPlayingPosition
    private val _curPlayingVideo = MutableLiveData<Video>()
    val curPlayingVideo: LiveData<Video> = _curPlayingVideo

    fun playbackStatus(playBackStatus: PlayBackStatus) {
        _plabackStatus.postValue(playBackStatus)
    }

    fun maxDuration(duration: Long) {
        _maxDuration.postValue(duration)
    }

    fun curDuration(duration: Long) {
        if (duration < 0)
            _curDuration.postValue(0)
        else _curDuration.postValue(duration)
    }

    fun curPlayingPosition(position: Int) {
        _curPlayingPosition.postValue(position)
    }

    fun curPlayingVideo(video: Video) {
        _curPlayingVideo.postValue(video)
    }

    fun handlePlayOrToggle() {
        when (playBackStatus.value) {
            PLAYING -> _plabackStatus.postValue(PAUSED)
            PAUSED -> _plabackStatus.postValue(PLAYING)
            else -> Unit
        }
    }
}