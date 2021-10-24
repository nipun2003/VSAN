package com.nipunapps.vsan.data.room

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import com.nipunapps.vsan.utils.Constants.VIDEO_DATABASE
import com.nipunapps.vsan.utils.Constants.VIDEO_METADATA

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(videoItem: VideoEntity)

    @Delete
    suspend fun delete(videoItem: VideoEntity)

    @Query("UPDATE $VIDEO_DATABASE SET $VIDEO_METADATA=:metdata WHERE id=:id")
    suspend fun update(metdata: String,id : Int)

    @Query("Select * from $VIDEO_DATABASE order by id ASC")
    fun getAllVideos() : List<VideoEntity>
}