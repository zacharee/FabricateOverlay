package tk.zwander.fabricateoverlaysample.ui.elements

import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo

@ExperimentalFoundationApi
@Composable
fun RegisteredOverlayList(
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var registeredOverlays by remember { mutableStateOf(mapOf<String, List<OverlayInfo>>()) }

    fun getOverlays() {
        OverlayAPI.getInstance(context) { api ->
            scope.launch {
                registeredOverlays = withContext(Dispatchers.IO) {
                    api.getAllOverlays(0).mapNotNull { (key, value) ->
                        val filtered = value.filter { item -> item.isFabricated && item.overlayName?.contains(context.packageName) == true }
                        if (filtered.isEmpty()) null
                        else (context.packageManager.run {
                            try {
                                getApplicationInfo(key, 0).loadLabel(this)
                            } catch (nameNotFoundException: PackageManager.NameNotFoundException) {
                                //package has been uninstalled before uninstalling related overlays
                                key
                            }
                        }.toString() to filtered)
                    }.toMap().toSortedMap { o1, o2 -> o1.compareTo(o2, true) }
                }
            }
        }
    }

    getOverlays()

    LazyColumn(
        modifier = modifier
    ) {
        registeredOverlays.forEach { (key, value) ->
            val sorted = value.sortedBy { info -> "${info.packageName}:${info.overlayName}" }

            stickyHeader {
                RegisteredOverlayHeaderItem(key)
            }

            items(count = sorted.size) {
                RegisteredOverlayItem(sorted[it]) {
                    getOverlays()
                }
            }
        }
    }
}