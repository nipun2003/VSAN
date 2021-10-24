package com.nipunapps.vsan.networkmanager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.DownloadManager.*
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Build
import androidx.core.content.getSystemService
import java.io.File
import java.util.*
import android.widget.Toast

import com.nipunapps.vsan.ui.MainActivity

import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.content.FileProvider
import com.nipunapps.vsan.BuildConfig
import com.nipunapps.vsan.R
import com.nipunapps.vsan.utils.Constants
import com.nipunapps.vsan.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class DownloadReq(private val context: Context) {
    //    val destFile = File(Constants.app_directory)
    private fun makeDownloadRequest(url: String, fileName: String): DownloadManager.Request {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Request(Uri.parse(url))
                .setNotificationVisibility(Request.VISIBILITY_VISIBLE)// Visibility of the download Notification
                .setTitle(fileName)// Title of the Download Notification
                .setDescription("Downloading")// Description of the Download Notification
                .setDestinationInExternalFilesDir(
                    context,
                    "/${Constants.VIDEO_DIRECTORY}",
                    fileName
                )
                .setRequiresCharging(false)// Set if charging is required to begin the download
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)
        } else {
            Request(Uri.parse(url))
                .setNotificationVisibility(Request.VISIBILITY_HIDDEN)// Visibility of the download Notification
                .setTitle(fileName)// Title of the Download Notification
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                .setDescription("Downloading")// Description of the Download Notification
                .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                .setAllowedOverRoaming(true)
        }
    }

    private var downloadID = 0L

    @SuppressLint("Range")
    suspend fun startDownload(url: String, title: String,view : View) {
        val progressBar : ProgressBar = view.findViewById(R.id.progress_bar_custom)
        withContext(Dispatchers.Main) {
            progressBar.isIndeterminate = true
            progressBar.visibility = View.VISIBLE
            showToast(context,"Download started")
        }
        val request = makeDownloadRequest(url, title)
        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)
        var finishDownload = false
        var progress = 0
        while (!finishDownload) {
            val cursor = downloadManager.query(Query().setFilterById(downloadID))
            if (cursor.moveToFirst()) {
                val status: Int = cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS))
                when (status) {
                    STATUS_FAILED -> {
                        finishDownload = true
                        withContext(Dispatchers.Main) {
                            progressBar.visibility = View.GONE
                            showToast(context, "Downloading Error")
                        }
                    }
                    STATUS_PAUSED -> {
                        withContext(Dispatchers.Main) {
                            showToast(context, "Downloading Paused")
                        }
                    }
                    STATUS_PENDING -> {
                    }
                    STATUS_RUNNING -> {
                        val total =
                            cursor.getLong(cursor.getColumnIndex(COLUMN_TOTAL_SIZE_BYTES))
                        if (total >= 0) {
                            val download = cursor.getLong(
                                cursor.getColumnIndex(
                                    COLUMN_BYTES_DOWNLOADED_SO_FAR
                                )
                            )
                            progress = (((download * 100L) / total.toInt()).toInt())
                        }
                        withContext(Dispatchers.Main){
                            progressBar.isIndeterminate = false
                            progressBar.visibility = View.VISIBLE
                            progressBar.progress = progress
                        }
                    }
                    STATUS_SUCCESSFUL -> {
                        progress = 100
                        finishDownload = true
                        withContext(Dispatchers.Main) {
                            view.visibility = View.GONE
                            showToast(context, "Downloading finished")
                        }
                    }
                }
            }
        }

    }
}