package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
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
        OverlayAPI.getInstance(this) { api ->
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