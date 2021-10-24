package com.nipunapps.vsan.networkmanager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nipunapps.vsan.utils.logError
import com.nipunapps.vsan.utils.showToast

class PermissionReq() {
    companion object{
        var permission = false
        fun getPermission(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(Environment.isExternalStorageManager()){
                    permission = true
                    return
                }
                showToast(context, "Allow us to write in your external storage")
                try {
                    Log.e("Inside", "inside")
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(String.format("package:%s", context.packageName))
                    context.startActivity(intent)
                } catch (e: Exception) {
                    logError(e.message.toString())
                }
            } else {
                Dexter.withContext(context)
                    .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    ).withListener(object : MultiplePermissionsListener {
                        @RequiresApi(Build.VERSION_CODES.R)
                        override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                            permission = true
                        }

                        override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest?>?,
                            token: PermissionToken?
                        ) {
                            permission = false
                            token?.continuePermissionRequest()
                        }
                    }).check()
            }
        }
    }
}