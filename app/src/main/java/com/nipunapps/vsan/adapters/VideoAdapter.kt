package com.nipunapps.vsan.adapters

import android.content.Context
import android.content.Intent
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
import com.nipunapps.vsan.ui.PlayerActivity
import com.nipunapps.vsan.utils.Constants

class VideoAdapter(private val context: Context) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {
    private var videos : ArrayList<Video> = ArrayList()

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name = itemView.findViewById<TextView>(R.id.videoTitle)
        val image = itemView.findViewById<ImageView>(R.id.videoImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.video_list,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.name.text = video.title
        try {
            Glide.with(context).load(video.imageLink).into(holder.image)
        }catch (e : Exception){
            Log.e("Nipun",e.message.toString())
        }
        holder.itemView.setOnClickListener {
            Intent(context,PlayerActivity::class.java).apply {
                putExtra(Constants.POSITION,position)
                putExtra(Constants.VIDEO_LIST,videos)
                context.startActivity(this)
            }
        }
    }

    override fun getItemCount(): Int {
        return videos.size
    }

    fun updateVideos(videos : List<Video>){
        this.videos.clear()
        this.videos.addAll(videos)
        notifyDataSetChanged()
    }
}