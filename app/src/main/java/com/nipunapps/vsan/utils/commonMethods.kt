package com.nipunapps.vsan.utils

import android.content.Context
import android.util.Log
import android.widget.Toast

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