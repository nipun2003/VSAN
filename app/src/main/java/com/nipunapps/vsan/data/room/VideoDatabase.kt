package com.nipunapps.vsan.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nipunapps.vsan.utils.Constants

@Database(entities = arrayOf(VideoEntity::class),version = 1,exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {
    abstract fun getVideoDao() : VideoDao
    companion object{

        @Volatile
        private var INSTANCE : VideoDatabase? = null
        fun getDataBase(context: Context) : VideoDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VideoDatabase::class.java,
                    Constants.VIDEO_DATABASE
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}