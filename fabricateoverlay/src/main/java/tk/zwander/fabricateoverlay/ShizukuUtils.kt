package tk.zwander.fabricateoverlay

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

/**
 * Some convenience functions for handling using Shizuku.
 */
object ShizukuUtils {
    /**
     * Checks if Shizuku is available. If the Shizuku Manager app
     * is either uninstalled OR isn't running, this will return
     * false.
     */
    val shizukuAvailable: Boolean
        get() = Shizuku.pingBinder()

    /**
     * Checks if the current app has permission to use Shizuku.
     * This works on Shizuku <11 and Shizuku >=11.
     *
     * @param context a Context object.
     */
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

    /**
     * Request permission to use Shizuku if it's not already granted. This works
     * for all versions of the Shizuku API.
     *
     * @param activity a [ComponentActivity] (used for registering a permission callback).
     * @param callback invoked when the permission grant result is received.
     */
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