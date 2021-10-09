package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(overlays.size) { index ->
                CurrentOverlayEntriesItem(info = overlays[index]) {
                    overlays.remove(it)
                }
            }
        }

        Spacer(Modifier.size(8.dp))

        Row(
            modifier = Modifier.padding(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    showingResDialog = true
                },
                modifier = Modifier.weight(1f)
                    .height(48.dp)
            ) {
                Text(
                    text = stringResource(R.string.add),
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.size(4.dp))

            OutlinedButton(
                onClick = {
                    showingSaveDialog = true
                },
                modifier = Modifier.weight(1f)
                    .height(48.dp),
                enabled = overlays.isNotEmpty()
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontSize = 18.sp
                )
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
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = { showingSaveDialog = false },
                        modifier = Modifier.weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.cancel))
                    }

                    TextButton(
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
                        },
                        modifier = Modifier.weight(1f)
                            .height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.save))
                    }
                }
            }
        )
    }
}