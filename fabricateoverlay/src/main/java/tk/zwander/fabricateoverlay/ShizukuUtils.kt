package tk.zwander.fabricateoverlay

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

object ShizukuUtils {
    val shizukuAvailable: Boolean
        get() = Shizuku.pingBinder()

    fun hasShizukuPermission(context: Context): Boolean {
        if (!shizukuAvailable) {
            return false
        }

        return if (Shizuku.getVersion() >= 11 && !Shizuku.isPreV11()) {
            Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
        } else {
            context.checkCallingOrSelfPermission(ShizukuProvider.PERMISSION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestShizukuPermission(activity: ComponentActivity, callback: (granted: Boolean) -> Unit) {
        if (Shizuku.getVersion() >= 11 && !Shizuku.isPreV11()) {
            Shizuku.addRequestPermissionResultListener(object : Shizuku.OnRequestPermissionResultListener {
                override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
                    Shizuku.removeRequestPermissionResultListener(this)
                    callback(grantResult == PackageManager.PERMISSION_GRANTED)
                }
            })
            Shizuku.requestPermission(100)
        } else {
            val permCallback = activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                callback(result)
            }

            permCallback.launch(ShizukuProvider.PERMISSION)
        }
    }
}