package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo

@Composable
fun RegisteredOverlayList() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var registeredOverlays by remember { mutableStateOf(mapOf<String, List<OverlayInfo>>()) }

    fun getOverlays() {
        OverlayAPI.getInstance(context) { api ->
            scope.launch {
                registeredOverlays = withContext(Dispatchers.IO) {
                    api.getAllOverlays(0).mapNotNull { (key, value) ->
                        val filtered = value.filter { item -> item.isFabricated && item.overlayName?.contains(context.packageName) == true }

                        if (filtered.isEmpty()) null else (key to filtered)
                    }.toMap()
                }
            }
        }
    }

    getOverlays()

    LazyColumn {
        registeredOverlays.forEach { (key, value) ->
            item {
                RegisteredOverlayHeaderItem(context.packageManager.run {
                    getApplicationInfo(key, 0).loadLabel(this) }.toString())
            }

            items(value.size) {
                RegisteredOverlayItem(value[it]) {
                    getOverlays()
                }
            }
        }
    }
}