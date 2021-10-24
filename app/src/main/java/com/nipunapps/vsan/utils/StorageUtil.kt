package com.nipunapps.vsan.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.nipunapps.vsan.data.remote.dto.Video
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StorageUtil(context: Context) {
    val pref: SharedPreferences =
        context.getSharedPreferences(Constants.storage_name, Context.MODE_PRIVATE)

    fun storeBoolean(key: String = Constants.FETCHKEY, value: Boolean) {
        val editor = pref.edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun storeVideo(key: String = Constants.VIDEO_STORE_KEY, value: List<Video>) {
        val editor = pref.edit()
        val json = Json.encodeToString(value)
        Log.e("json", json)
        editor.putString(key, json)
        editor.apply()
    }

    fun getVideo(key: String =Constants.VIDEO_STORE_KEY): List<Video> {
        return try {
            pref.getString(key,"Not found")?.let { Json.decodeFromString(it) }!!
        }catch (e : Exception){
            emptyList()
        }

    }

    fun getBoolean(key: String = Constants.FETCHKEY): Boolean {
        Log.e("boolean","${pref.getBoolean(key,false)}")
        return pref.getBoolean(key, true)
    }

    fun storeInteger(key: String = Constants.FILE_SORT_KEY, value: Int) {
        val editor = pref.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getInt(key: String = Constants.FILE_SORT_KEY): Int {
        return pref.getInt(key, 0)
    }

    fun storeLong(key: String = Constants.FILE_SORT_KEY, value: Long) {
        val editor = pref.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLong(key: String = Constants.FILE_SORT_KEY): Long {
        return pref.getLong(key, 0)
    }

    fun clearPreference() {
        storeBoolean(value = true)
    }
}