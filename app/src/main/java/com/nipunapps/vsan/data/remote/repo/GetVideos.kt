package com.nipunapps.vsan.data.remote.repo

import android.content.Context
import com.nipunapps.vsan.data.remote.VideoService
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.utils.Resource
import com.nipunapps.vsan.utils.StorageUtil
import com.nipunapps.vsan.utils.logError

class GetVideos(context: Context) {
    private val service = VideoService.create()
    private val storageUtil = StorageUtil(context)
    suspend fun getVideos(){
        storageUtil.storeVideo(value = service.getVideos())
    }

    fun fetchVideos():Resource<List<Video>>{
        val videos: Resource<List<Video>> = try {
            Resource.success(storageUtil.getVideo())
        }catch (e : Exception){
            Resource.error(e.message.toString(),null)
        }
        return videos
    }
}