package com.nipunapps.vsan.data.room

import android.content.Context
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.util.Log
import androidx.lifecycle.LiveData
import com.nipunapps.vsan.data.remote.VideoService
import com.nipunapps.vsan.utils.Resource
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.Exception

class VideoRepository(private val context: Context) {
    private val videoDao = VideoDatabase.getDataBase(context).getVideoDao()
    private val service = VideoService.create()

    suspend fun initialiseVideos(){
        service.getVideos().forEach {
            val videoItem = VideoItem(it)
            insert(videoItem)
        }
    }

    suspend fun insert(videoItem: VideoItem){
        try {
            Log.e("ID",videoItem.metaData.id.toString())
            videoDao.insert(VideoEntity(Json.encodeToString(videoItem),videoItem.metaData.id))
        }catch (e : Exception){
            Log.e("Insertion",e.message.toString())
        }
    }
    suspend fun delete(videoItem: VideoItem){
        videoDao.insert(VideoEntity(Json.encodeToString(videoItem),videoItem.metaData.id))
    }

    suspend fun update(videoItem: VideoItem){
        videoDao.update(Json.encodeToString(videoItem),videoItem.metaData.id)
    }

    private fun getAllVideos() : List<VideoEntity> = videoDao.getAllVideos()

    fun getVideos() : Resource<ArrayList<VideoItem>>{
        val videos : Resource<ArrayList<VideoItem>> = try {
            val videoItems = ArrayList<VideoItem>()
            getAllVideos().forEach { videoEntity ->
                Log.e("FirstTime","First Time : " +videoEntity.id.toString())
                val item = Json.decodeFromString<VideoItem>(videoEntity.string)
                videoItems.add(item)
            }
            Resource.success(videoItems)
        }catch (e : Exception){
            Resource.error(e.message.toString(),null)
        }
        return videos
    }

    fun getLiveSnapshot() : LiveData<List<VideoEntity>> = videoDao.getLiveSnapshot()
}