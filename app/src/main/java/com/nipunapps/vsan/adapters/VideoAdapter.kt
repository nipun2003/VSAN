package com.nipunapps.vsan.adapters

import android.content.Context
import android.content.Intent
import android.telephony.mbms.DownloadRequest
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nipunapps.vsan.R
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.data.room.VideoItem
import com.nipunapps.vsan.networkmanager.DownloadReq
import com.nipunapps.vsan.networkmanager.PermissionReq
import com.nipunapps.vsan.ui.PlayerActivity
import com.nipunapps.vsan.utils.Constants
import com.nipunapps.vsan.utils.loadImage
import com.nipunapps.vsan.utils.toVideo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoAdapter(private val context: Context) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    private var videos: ArrayList<VideoItem> = ArrayList()
    private val downloadRequest = DownloadReq(context)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image = itemView.findViewById<ImageView>(R.id.videoImage)
        val download: RelativeLayout = itemView.findViewById(R.id.download_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.video_list, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoItem = videos[position]
        val video = videoItem.metaData
        holder.download.bringToFront()
        val title = "${video.title}${video.videoLink.substring(video.videoLink.lastIndexOf('.'))}"
        CoroutineScope(Dispatchers.IO).launch {
            if(title.isOfflineAvailable()){
                CoroutineScope(Dispatchers.Main).launch {
                    holder.download.visibility = View.GONE
                }
            }
        }
        holder.download.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                downloadRequest.startDownload(video.videoLink, title,it)
            }
        }
        loadImage(context, video.imageLink, holder.image)
        holder.itemView.setOnClickListener {
            Intent(context, PlayerActivity::class.java).apply {
                putExtra(Constants.POSITION, position)
                putExtra(Constants.VIDEO_LIST, videos)
                context.startActivity(this)
            }
        }
    }

    private fun  String.isOfflineAvailable() :Boolean{
        var found = false
        context.getExternalFilesDir(Constants.VIDEO_DIRECTORY)?.listFiles()?.forEach { file ->
            if (this.equals(file.name)) {
                found = true
            }
        }
        return found
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun updateVideos(videos: ArrayList<VideoItem>) {
        this.videos.clear()
        this.videos.addAll(videos)
        notifyDataSetChanged()
    }
}