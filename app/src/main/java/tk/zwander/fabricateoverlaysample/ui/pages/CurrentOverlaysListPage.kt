package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.CurrentOverlayEntriesItem
import tk.zwander.fabricateoverlaysample.ui.elements.ListAvailableResourcesDialog

@Composable
fun CurrentOverlaysListPage(
    navController: NavController,
    info: ApplicationInfo
) {
    var showingResDialog by remember { mutableStateOf(false) }
    var showingSaveDialog by remember { mutableStateOf(false) }
    val overlays = remember { mutableStateListOf<FabricatedOverlayEntry>() }
    val context = LocalContext.current

    Column {
        Row {
            Button(
                onClick = {
                    showingResDialog = true
                }
            ) {
                Text(stringResource(R.string.add))
            }

            Button(
                onClick = {
                    showingSaveDialog = true
                }
            ) {
                Text(stringResource(id = R.string.save))
            }
        }

        LazyColumn {
            items(overlays.size) { index ->
                CurrentOverlayEntriesItem(info = overlays[index]) {
                    overlays.remove(it)
                }
            }
        }
    }

    if (showingResDialog) {
        ListAvailableResourcesDialog(
            info = info,
            onDismiss = { showingResDialog = false },
            onAddEntry = { overlays.add(it) }
        )
    }

    if (showingSaveDialog) {
        var name by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showingSaveDialog = false },
            title = { Text(stringResource(id = R.string.save)) },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.overlay_name)) }
                )
            },
            buttons = {
                Row {
                    Button(
                        onClick = { showingSaveDialog = false }
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    Button(
                        onClick = {
                            OverlayAPI.getInstance(context) { api ->
                                api.registerFabricatedOverlay(FabricatedOverlay(
                                    "${context.packageName}.${info.packageName}.${name}",
                                    info.packageName
                                ).apply {
                                    overlays.forEach { overlay ->
                                        entries[overlay.resourceName] = overlay
                                    }
                                })

                                showingSaveDialog = false

                                navController.popBackStack("main", false)
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        )
    }
}