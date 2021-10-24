package com.nipunapps.vsan.data.remote.dto
import android.net.Uri
class ExoItem(val title: String,val onlineUri : Uri) {
    var offlineUri : Uri? = null
}