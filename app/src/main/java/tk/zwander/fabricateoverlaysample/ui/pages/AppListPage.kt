package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
        async(Dispatchers.IO) {
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

    if (apps.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } else {
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
}