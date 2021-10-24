package com.nipunapps.vsan.ui

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build.VERSION_CODES
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.nipunapps.vsan.R
import com.nipunapps.vsan.networkmanager.NetworkProvider
import com.nipunapps.vsan.ui.viewmodel.MainViewModel
import com.nipunapps.vsan.utils.StorageUtil
import java.io.File
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.nipunapps.vsan.data.room.VideoRepository
import com.nipunapps.vsan.networkmanager.PermissionReq
import com.nipunapps.vsan.utils.logError
import com.nipunapps.vsan.utils.showToast

class MainActivity : AppCompatActivity() {

    private lateinit var networkProvider: NetworkProvider
    private lateinit var viewModel: MainViewModel
    private lateinit var storageUtil: StorageUtil
    private lateinit var videoRepository: VideoRepository

    @RequiresApi(VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        storageUtil = StorageUtil(this)
        networkProvider = NetworkProvider(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        videoRepository = VideoRepository(this)
        networkProvider.observe(this, { connection ->
            if (connection) {
                viewModel.fetchData()
                viewModel.fetchVideos()
            } else {
                showToast(this, "No network connection")
            }
        })
        videoRepository.getLiveSnapshot().observe(this,{
            viewModel.fetchVideos()
        })
        makeAppDirectory()
    }

    override fun onResume() {
        super.onResume()
        if(storageUtil.getInt("new") != 1){
            finishAffinity()
        }
        storageUtil.storeInteger("new",1)
    }

    private fun makeAppDirectory() {
        val file = File("/storage/emulated/0/VSAN")
        file.mkdir()
        val list: ArrayList<Uri> = ArrayList()
        list.add(Uri.parse(file.path))
    }

    override fun onDestroy() {
        super.onDestroy()
        storageUtil.clearPreference()
    }
}