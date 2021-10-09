package tk.zwander.fabricateoverlaysample

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.lsposed.hiddenapibypass.HiddenApiBypass
import tk.zwander.fabricateoverlay.ShizukuUtils
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
            MaterialTheme(
                colors = darkColors()
            ) {
                Surface {
                    var appInfoArg by remember {
                        mutableStateOf<ApplicationInfo?>(null)
                    }
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            HomePage(navController)
                        }
                        composable("app_list") {
                            AppListPage(navController)
                        }
                        composable(
                            route = "list_overlays"
                        ) {
                            navController.previousBackStackEntry?.arguments?.getParcelable<ApplicationInfo>("appInfo")?.let {
                                appInfoArg = it
                            }

                            CurrentOverlaysListPage(
                                navController,
                                appInfoArg!!
                            )
                        }
                    }
                }
            }
        }
    }
}