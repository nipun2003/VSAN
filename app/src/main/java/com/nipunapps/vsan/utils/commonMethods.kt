package com.nipunapps.vsan.utils

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import android.widget.ImageView
import com.google.android.exoplayer2.MediaItem
import com.nipunapps.vsan.data.remote.dto.ExoItem
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.data.room.VideoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun logError(message: String) {
    Log.e("Nipun", message)
}

fun setTimeFormat(time: Long): String {
    var s = time / 1000
    var m = s / 60
    s %= 60
    if (m > 59) {
        val h = m / 60
        m %= 60
        return String.format("%d:%02d:%02d", h, m, s)
    }
    return String.format("%02d:%02d", m, s)
}

fun loadImage(context: Context, uri: String, target: ImageView) {
    try {
        Glide.with(context).load(uri).thumbnail(0.2f).into(target)
    } catch (e: Exception) {
        logError(e.message.toString())
    }
}

fun Video.toExoItem(): ExoItem = ExoItem(this.title, Uri.parse(this.videoLink))
fun Uri.toMediaItem(): MediaItem = MediaItem.fromUri(this)
fun ExoItem.toMediaItem(): MediaItem =
    MediaItem.Builder().setUri(this.offlineUri ?: this.onlineUri).setTag(this).build()

fun MediaMetadataCompat.toVideo(): Video = Video(
    METADATA_KEY_DISPLAY_TITLE,
    METADATA_KEY_MEDIA_ID.toInt(),
    METADATA_KEY_MEDIA_URI,
    METADATA_KEY_DISPLAY_ICON_URI
)

fun Video.toMeTaData(): MediaMetadataCompat = MediaMetadataCompat.Builder()
    .putString(METADATA_KEY_ARTIST, this.title)
    .putString(METADATA_KEY_MEDIA_ID, this.id.toString())
    .putString(METADATA_KEY_TITLE, this.title)
    .putString(METADATA_KEY_DISPLAY_ICON_URI, this.imageLink)
    .putString(METADATA_KEY_MEDIA_URI, this.videoLink)
    .putString(METADATA_KEY_DISPLAY_TITLE, this.title)
    .putString(METADATA_KEY_ALBUM_ART_URI, this.imageLink)
    .putString(METADATA_KEY_DISPLAY_SUBTITLE, this.title)
    .build()

fun getVideosFromVideoItems(videoItems: ArrayList<VideoItem>) : ArrayList<Video>{
    val videos : ArrayList<Video> = ArrayList()
    videoItems.forEach {
        videos.add(it.metaData)
    }
    return videos
}