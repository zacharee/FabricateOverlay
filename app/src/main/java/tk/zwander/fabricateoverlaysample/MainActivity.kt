package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.ShizukuUtils
import tk.zwander.fabricateoverlaysample.ui.pages.AddOverlayListPage
import tk.zwander.fabricateoverlaysample.ui.pages.AppListPage
import tk.zwander.fabricateoverlaysample.ui.pages.CurrentOverlaysListPage
import tk.zwander.fabricateoverlaysample.ui.pages.HomePage

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
    }

    private fun init() {
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "main") {
                composable("main") {
                    HomePage(navController)
                }
                composable("app_list") {
                    AppListPage(navController)
                }
                composable(
                    route = "add_overlay",
                ) {
                    AddOverlayListPage(
                        navController,
                        navController.previousBackStackEntry?.arguments!!.getParcelable("appInfo")!!
                    )
                }
                composable(
                    route = "list_overlays"
                ) {
                    CurrentOverlaysListPage(
                        navController.previousBackStackEntry?.arguments!!.getParcelable("appInfo")!!
                    )
                }
            }
        }

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