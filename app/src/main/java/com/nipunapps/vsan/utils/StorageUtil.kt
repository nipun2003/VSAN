package com.nipunapps.vsan.utils

import android.content.Context
import android.content.SharedPreferences

class StorageUtil(context: Context) {
    val pref: SharedPreferences = context.getSharedPreferences(Constants.storage_name,Context.MODE_PRIVATE)

    fun storeBoolean(key : String = Constants.FETCHKEY , value : Boolean){
        val editor = pref.edit()
        editor.putBoolean(key,value)
        editor.apply()
    }

    fun getBoolean(key: String = Constants.FETCHKEY) : Boolean{
        return pref.getBoolean(key,true)
    }

    fun storeInteger(key : String = Constants.FILE_SORT_KEY,value: Int){
        val editor = pref.edit()
        editor.putInt(key,value)
        editor.apply()
    }
    fun getInt(key: String = Constants.FILE_SORT_KEY) : Int{
        return pref.getInt(key,0)
    }

    fun clearPreference(){
        val editor = pref.edit()
        editor.clear()
        editor.apply()
    }
}