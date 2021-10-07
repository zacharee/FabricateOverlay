package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import android.util.TypedValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import net.dongliu.apk.parser.ApkFile
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.ui.elements.*
import tk.zwander.fabricateoverlaysample.util.getAppResources

@Composable
fun ListAvailableResourcesDialog(
    info: ApplicationInfo,
    onDismiss: () -> Unit,
    onAddEntry: (FabricatedOverlayEntry) -> Unit
) {
    var showingResDialog by remember { mutableStateOf(false) }
    var resData by remember { mutableStateOf<AvailableResourceItemData?>(null) }

    Dialog(
        onDismissRequest = onDismiss
    ) {
        var resources by remember { mutableStateOf(listOf<AvailableResourceItemData>()) }

        LaunchedEffect("resources") {
            resources = getAppResources(ApkFile(info.sourceDir))
        }

        LazyColumn {
            items(resources.size) {
                AvailableResourceItem(resources[it]) { data ->
                    resData = data
                    showingResDialog = true
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