package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.ServiceConnection
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import org.lsposed.hiddenapibypass.HiddenApiBypass
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayAPI
import tk.zwander.fabricateoverlay.ShizukuService
import tk.zwander.fabricateoverlay.ShizukuUtils

@SuppressLint("PrivateApi")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HiddenApiBypass.setHiddenApiExemptions("L")

        if (!ShizukuUtils.shizukuAvailable) {
            finish()
        }

        if (ShizukuUtils.hasShizukuPermission(this)) {
            init()
        } else {
            ShizukuUtils.requestShizukuPermission(this) { granted ->
                if (granted) {
                    init()
                } else {
                    finish()
                }
            }
        }

        setContentView(R.layout.activity_main)
    }

    private fun init() {
        FabricatedOverlayAPI.getInstance(this) { api ->
            api.unregisterFabricatedOverlay(FabricatedOverlay.generateOverlayIdentifier("ExampleOverlay"))

            val exampleOverlay = FabricatedOverlay(
                "${packageName}.ExampleOverlay",
                "com.android.systemui"
            )

            exampleOverlay.setInteger("quick_settings_num_columns", 3)

            api.registerFabricatedOverlay(exampleOverlay)
        }
    }
}