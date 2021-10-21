package com.nipunapps.vsan.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.nipunapps.vsan.R
import com.nipunapps.vsan.data.remote.dto.Video
import com.nipunapps.vsan.databinding.ActivityPlayerBinding
import kotlinx.coroutines.*
import android.net.Uri
import android.widget.ProgressBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import com.nipunapps.vsan.ui.viewmodel.PlayerViewModel
import com.nipunapps.vsan.utils.*
import com.nipunapps.vsan.utils.PlayBackStatus.*

class PlayerActivity : AppCompatActivity() {
    //Variables
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var controllView: View
    private var videoList: ArrayList<Video> = ArrayList()
    private var isControllerOpen = true
    private lateinit var playerViewModel: PlayerViewModel

    //Views
    private lateinit var skipToR10: ImageView
    private lateinit var skipToF10: ImageView
    private lateinit var play: ImageView
    private lateinit var seekBar: SeekBar
    private lateinit var startTime: TextView
    private lateinit var endTime: TextView
    private lateinit var title: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        controllView = binding.controller?.root ?: layoutInflater.inflate(
            R.layout.player_control,
            binding.root
        )
        playerViewModel = ViewModelProvider(this)[PlayerViewModel::class.java]
        setContentView(binding.root)
        supportActionBar?.hide()
        playerViewModel.curPlayingPosition(intent?.getIntExtra(Constants.POSITION, 0)!!)
        try {
            val bundle = intent?.extras
            videoList =
                bundle?.getParcelableArrayList<Parcelable>(Constants.VIDEO_LIST) as ArrayList<Video>
        } catch (e: Exception) {
            logError(e.message.toString())
        }
        showControl()
        initialiseControllerVariable(controllView)
        subscribeToObserver()
        play.setOnClickListener {
            playerViewModel.handlePlayOrToggle()
        }
        skipToF10.setOnClickListener {
            val nextDuration = playerViewModel.curDuration.value?.plus(10000)
            if (nextDuration != null)
                binding.videoView!!.seekTo(nextDuration.toInt())

        }
        skipToR10.setOnClickListener {
            val nextDuration = playerViewModel.curDuration.value?.minus(10000)
            if (nextDuration != null)
                binding.videoView!!.seekTo(nextDuration.toInt())

        }
        seekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (p2)
                    binding.videoView!!.seekTo(p1)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                p0?.let {
                    binding.videoView!!.seekTo(p0.progress)
                }
            }

        })
    }

    private fun initialiseControllerVariable(view: View) {
        skipToR10 = view.findViewById(R.id.skipr_10)
        skipToF10 = view.findViewById(R.id.skipf_10)
        play = view.findViewById(R.id.play_pause)
        seekBar = view.findViewById(R.id.seekbar_progress)
        startTime = view.findViewById(R.id.start_time)
        endTime = view.findViewById(R.id.endt_time)
        title = view.findViewById(R.id.title)
    }

    private fun showControl() {
        controllView.visibility = View.VISIBLE
        isControllerOpen = true
        launchAutoHideController()
    }

    private fun hideControl(coroutineScope: CoroutineScope? = null) {
        controllView.visibility = View.GONE
        isControllerOpen = false
        coroutineScope?.cancel()
    }

    fun toggleControllerDisplay(view: View) {
        if (isControllerOpen) hideControl() else showControl()
    }

    private fun launchAutoHideController() {
        CoroutineScope(Dispatchers.Main).launch {
            delay(5000)
            hideControl(this)
        }.start()
    }

    private fun subscribeToObserver() {
        try {
            playerViewModel.curPlayingPosition.observe(this, { pos ->
                playerViewModel.curPlayingVideo(videoList[pos])
            })
            playerViewModel.curPlayingVideo.observe(this, { video ->
                prepareMedia(video)
            })
            playerViewModel.maxDuration.observe(this, { duration ->
                seekBar.max = duration.toInt()
                endTime.text = setTimeFormat(duration)
            })
            playerViewModel.curDuration.observe(this, { duration ->
                startTime.text = setTimeFormat(duration)
                seekBar.progress = duration.toInt()
                updateCurDuration()
            })
            playerViewModel.playBackStatus.observe(this, { status ->
                when (status) {
                    PREPARING -> binding.progressBar!!.visibility = View.VISIBLE
                    PREPARED -> {
                        binding.progressBar!!.visibility = View.GONE
                        playerViewModel.curDuration(0)
                        playerViewModel.maxDuration(binding.videoView!!.duration.toLong())
                        playerViewModel.playbackStatus(PLAYING)
                    }
                    PLAYING -> {
                        binding.videoView!!.start()
                        play.setImageResource(R.drawable.ic_pause)
                        playerViewModel.curDuration(binding.videoView!!.currentPosition.toLong())
                    }
                    PAUSED -> {
                        binding.videoView!!.pause()
                        play.setImageResource(R.drawable.ic_play)
                    }
                    STOPPED -> binding.videoView!!.stopPlayback()
                    else -> Unit
                }
            })
        } catch (e: Exception) {
            logError(e.message.toString())
        }
    }

    private fun updateCurDuration() {
        CoroutineScope(Dispatchers.Main).launch {
            playerViewModel.curDuration(binding.videoView!!.currentPosition.toLong())
            delay(1000)
        }
    }

    private fun prepareMedia(video: Video) {
        binding.videoView?.setVideoURI(Uri.parse(video.videoLink))
        binding.videoView?.setOnPreparedListener {
            playerViewModel.playbackStatus(PREPARED)
            title.text = video.title
        }
    }
}