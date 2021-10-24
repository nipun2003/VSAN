package com.nipunapps.vsan.data.room

import com.nipunapps.vsan.data.remote.dto.Video
import kotlinx.serialization.Serializable
import android.net.Uri

@Serializable
class VideoItem( val metaData : Video) : java.io.Serializable {
    var offlineUri : String? = null
    var maxDuration : Long = 0L
    var currentDuration : Long = 0L
    var aspectRatio = 0
    var downloadProgress = 0
}