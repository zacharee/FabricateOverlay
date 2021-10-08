package tk.zwander.fabricateoverlaysample.ui.elements

import android.content.pm.ApplicationInfo
import android.util.TypedValue
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.dongliu.apk.parser.ApkFile
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.util.getAppResources

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListAvailableResourcesDialog(
    info: ApplicationInfo,
    onDismiss: () -> Unit,
    onAddEntry: (FabricatedOverlayEntry) -> Unit
) {
    var showingResDialog by remember { mutableStateOf(false) }
    var resData by remember { mutableStateOf<AvailableResourceItemData?>(null) }
    var resources by remember { mutableStateOf(mapOf<String, List<AvailableResourceItemData>>()) }

    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface {
            LaunchedEffect("resources") {
                async(Dispatchers.IO) {
                    resources = getAppResources(context, ApkFile(info.sourceDir))
                }
            }

            if (resources.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    resources.forEach { (k, v) ->
                        stickyHeader {
                            Surface {
                                Text(
                                    text = k,
                                    modifier = Modifier
                                        .heightIn(min = 32.dp)
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    fontSize = 18.sp
                                )
                            }
                        }

                        items(v.size) {
                            AvailableResourceItem(v[it]) { data ->
                                resData = data
                                showingResDialog = true
                            }
                        }
                    }
                }
            }
        }
    }

    if (showingResDialog) {
        when (resData?.type) {
            TypedValue.TYPE_INT_DEC -> AddOverlayIntegerEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_INT_COLOR_ARGB8 -> AddOverlayColorEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_INT_BOOLEAN -> AddOverlayBooleanEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        if (it) 1 else 0
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_DIMENSION -> AddOverlayDimensionEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )
        }
    }
}