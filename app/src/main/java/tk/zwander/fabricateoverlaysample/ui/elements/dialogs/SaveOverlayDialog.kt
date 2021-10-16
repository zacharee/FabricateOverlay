package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlaysample.R

@Composable
fun SaveOverlayDialog(
    info: ApplicationInfo,
    overlayEntries: List<FabricatedOverlayEntry>,
    navController: NavController,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.save)) },
        text = {
            TextField(
                value = name,
                onValueChange = {
                    name = it.filter { char ->
                        (char.isLetterOrDigit() || char == '.' || char == '_')
                    }.replace(Regex("(_+)\\1"), "_")
                        .replace(Regex("(\\.+)\\1"), ".")
                        .replace("_.", "_")
                        .replace("._", ".")
                },
                label = { Text(stringResource(id = R.string.overlay_name)) }
            )
        },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(stringResource(id = R.string.cancel))
                }

                TextButton(
                    onClick = {
                        OverlayAPI.getInstance(context) { api ->
                            api.registerFabricatedOverlay(
                                FabricatedOverlay(
                                    "${context.packageName}.${info.packageName}.${name}",
                                    info.packageName
                                ).apply {
                                    overlayEntries.forEach { overlay ->
                                        entries[overlay.resourceName] = overlay
                                    }
                                })

                            onDismiss()

                            navController.popBackStack("main", false)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(stringResource(id = R.string.save))
                }
            }
        }
    )
}