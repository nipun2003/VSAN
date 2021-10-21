package com.nipunapps.vsan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.nipunapps.vsan.R
import com.nipunapps.vsan.ui.viewmodel.MainViewModel
import com.nipunapps.vsan.utils.NetworkProvider
import com.nipunapps.vsan.utils.StorageUtil

class MainActivity : AppCompatActivity() {

    private lateinit var networkProvider: NetworkProvider
    private lateinit var viewModel : MainViewModel
    private lateinit var storageUtil : StorageUtil
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        storageUtil = StorageUtil(this)
        networkProvider = NetworkProvider(this)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        networkProvider.observe(this, { connection ->
            if (connection) {
                viewModel.fetchVideos()
                storageUtil.storeBoolean(value = true)
            }else {
                if (storageUtil.getBoolean())
                    viewModel.setNoNetworkError()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        storageUtil.clearPreference()
    }
}