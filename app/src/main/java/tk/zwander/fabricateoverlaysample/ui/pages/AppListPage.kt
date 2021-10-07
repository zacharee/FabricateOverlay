package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlaysample.data.LoadedApplicationInfo
import tk.zwander.fabricateoverlaysample.ui.elements.AppItem
import java.util.*

@Composable
fun AppListPage(
    navController: NavController
) {
    var apps by remember { mutableStateOf(listOf<LoadedApplicationInfo>()) }
    val context = LocalContext.current

    LaunchedEffect("app_launch") {
        async {
            apps = TreeSet(
                context.packageManager.getInstalledApplications(0)
                    .filterNot { it.isResourceOverlay }
                    .map {
                        LoadedApplicationInfo(
                            it.loadLabel(context.packageManager).toString(),
                            it.loadIcon(context.packageManager),
                            it
                        )
                    }).toList()
        }
    }

    LazyColumn {
        items(apps.size) {
            AppItem(apps[it]) { info ->
                navController.currentBackStackEntry?.arguments?.putParcelable("appInfo", info.info)

                navController.navigate(
                    route = "list_overlays"
                )
            }
        }
    }
}