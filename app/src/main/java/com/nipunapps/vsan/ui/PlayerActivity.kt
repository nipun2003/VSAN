package com.nipunapps.vsan.ui

import android.annotation.SuppressLint
import android.app.PictureInPictureParams
import android.content.Intent
import android.content.res.Configuration
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.databinding.ActivityPlayerBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.nipunapps.vsan.utils.*
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.util.Log
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerControlView
import com.nipunapps.vsan.BuildConfig
import com.nipunapps.vsan.data.remote.dto.ExoItem
import com.nipunapps.vsan.data.room.VideoItem
import com.nipunapps.vsan.data.room.VideoRepository
import java.io.File

class PlayerActivity : AppCompatActivity() {

    //Variables
    private lateinit var binding: ActivityPlayerBinding
    private var videoItem: ArrayList<ExoItem> = ArrayList()
    private var player: SimpleExoPlayer? = null
    private var playbackPosition = 0L
    private var currentWindow = 0
    private var currentPlayingPosition = 0
    private val playbackStateListener: Player.EventListener = playbackStateListener()
    private lateinit var storageUtil: StorageUtil
    private var currentPlayItem: ExoItem? = null
    private lateinit var videoRepository: VideoRepository

    private var ASPECT_RATIO: MutableLiveData<Int> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        storageUtil = StorageUtil(this)
        restartPlayer(intent)
    }

    private fun restartPlayer(intent: Intent?){
        videoRepository = VideoRepository(this)
        videoItem.clear()
        if (intent?.data != null) {
            currentPlayItem = ExoItem(getFileName(intent?.data!!), intent?.data!!)
            videoItem.add(currentPlayItem!!)
        } else {
            currentPlayingPosition = intent?.getIntExtra(Constants.POSITION, 0)!!
            val bundle = intent?.extras
            val videoList =
                bundle?.getParcelableArrayList<Parcelable>(Constants.VIDEO_LIST) as ArrayList<VideoItem>
            videoList?.let {
                getVideosFromVideoItems(it).forEach { video ->
                    val item = video.toExoItem()
                    getExternalFilesDir(Constants.VIDEO_DIRECTORY)?.listFiles()?.forEach { file ->
                        if (video.title.equals(
                                file.name.substring(
                                    0,
                                    file.name.lastIndexOf('.')
                                )
                            )
                        ) {
                            item.offlineUri = FileProvider.getUriForFile(
                                this,
                                BuildConfig.APPLICATION_ID + ".provider",
                                file
                            )
                            Log.e("Found", "true")
                        }
                    }
                    videoItem.add(item)
                }
            }
            currentPlayItem = videoItem[currentPlayingPosition]
            Log.e("Nipun",currentPlayItem!!.title)
        }
        ASPECT_RATIO.postValue(storageUtil.getInt(Constants.ASPECT_RATIO_KEY))
        binding.songTitle.text = currentPlayItem?.title
        playbackPosition = storageUtil.getLong(currentPlayItem!!.onlineUri.toString())
        Log.e("Playback", "" + playbackPosition)
        Log.e("Found", currentPlayItem!!.onlineUri.toString())
        initialisePlayer()
        ASPECT_RATIO.observe(this, { ratio ->
            binding.videoView.resizeMode = ratio
            storageUtil.storeInteger(Constants.ASPECT_RATIO_KEY, ratio)
        })
        binding.aspectRatio.setOnClickListener {
            ASPECT_RATIO.postValue(if (ASPECT_RATIO.value == 4) 0 else ASPECT_RATIO.value?.plus(1))
        }
    }

    private fun initialisePlayer() {
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also { exoPlayer ->
                binding.videoView.apply {
                    player = exoPlayer
                    setControllerVisibilityListener {
                        binding.controllContainer.visibility = it
                    }
                }
                try {
                    exoPlayer.setMediaItem(
                        currentPlayItem!!.toMediaItem()
                    )
                    exoPlayer.seekTo(currentWindow, playbackPosition)
                    exoPlayer.playWhenReady = true
                    exoPlayer.addListener(playbackStateListener)
                    exoPlayer.prepare()
                } catch (e: Exception) {
                    exoPlayer.setMediaItem((currentPlayItem!!.onlineUri).toMediaItem())
                    exoPlayer.seekTo(currentWindow, playbackPosition)
                    exoPlayer.playWhenReady = true
                    exoPlayer.addListener(playbackStateListener)
                    exoPlayer.prepare()
                }
            }
    }

    private fun playbackStateListener() = object : Player.EventListener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                ExoPlayer.STATE_IDLE -> binding.progressBar.visibility = View.VISIBLE
                ExoPlayer.STATE_BUFFERING -> binding.progressBar.visibility = View.VISIBLE
                ExoPlayer.STATE_READY -> {
                    binding.progressBar.visibility = View.GONE
                }
                ExoPlayer.STATE_ENDED -> {
                    finish()
                }
                else -> binding.progressBar.visibility = View.GONE
            }
        }

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (mediaItem != null && mediaItem.playbackProperties != null) {
                val item = mediaItem.playbackProperties!!.tag as ExoItem
                binding.songTitle.text = item.title
            }
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode) {
                setPictureInPicture()
            }
        } else {
            player?.pause()
        }
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode) {
                releasePlayer()
                finishAndRemoveTask()
            }
        } else {
            player?.pause()
        }
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setPictureInPicture()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        releasePlayer()
        restartPlayer(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setPictureInPicture() {
        val aspectRation = Rational(binding.videoView.width, binding.videoView.height)
        val params = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRation)
            .build()
        enterPictureInPictureMode(params)
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration?
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        if (isInPictureInPictureMode) {
            binding.videoView.hideController()
        } else {
            binding.videoView.showController()
        }
    }

    override fun onStop() {
        super.onStop()
        if (player?.isPlaying == true) {
            releasePlayer()
            finishAndRemoveTask()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.run {
            playbackPosition = this.currentPosition
            if (playbackPosition / 5000 == (player!!.duration) / 5000) {
                playbackPosition = 0
            }
            storageUtil.storeLong(currentPlayItem!!.onlineUri.toString(), playbackPosition)
            currentWindow = this.currentWindowIndex
            playWhenReady = this.playWhenReady
            removeListener(playbackStateListener)
            release()
        }
        player = null
    }

    @SuppressLint("Range")
    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val cursor =
                    contentResolver.query(uri, null, null, null)
                cursor.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        result =
                            cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    }
                }
            }
        }

        if (result == null) {
            result = uri.path?.let {
                it.substring(it.lastIndexOf('/') + 1)
            }

        }
        return result ?: ""
    }
}