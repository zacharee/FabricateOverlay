package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.R

@Composable
fun RegisteredOverlayItem(
    info: OverlayInfo,
    onChange: () -> Unit
) {
    val context = LocalContext.current
    var showingRemoveDialog by remember { mutableStateOf(false) }

    fun change() {
        OverlayAPI.getInstance(context) { api ->
            api.setEnabled(
                FabricatedOverlay.generateOverlayIdentifier(
                    info.overlayName,
                    info.packageName
                ), !info.isEnabled, 0
            )
            onChange()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    change()
                }
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(R.string.remove_overlay),
                modifier = Modifier
                    .clickable {
                        showingRemoveDialog = true
                    }
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.size(8.dp))

            Text(
                text = "${info.packageName}:${info.overlayName}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            Spacer(Modifier.size(8.dp))

            Checkbox(
                checked = info.isEnabled,
                onCheckedChange = {
                    change()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }

    if (showingRemoveDialog) {
        AlertDialog(
            onDismissRequest = { showingRemoveDialog = false },
            buttons = {
                Row {
                    Button(
                        onClick = {
                            showingRemoveDialog = false
                        }
                    ) {
                        Text(stringResource(id = R.string.no))
                    }

                    Button(
                        onClick = {
                            OverlayAPI.getInstance(context) { api ->
                                api.unregisterFabricatedOverlay(
                                    FabricatedOverlay.generateOverlayIdentifier(
                                        info.overlayName
                                    )
                                )
                                showingRemoveDialog = false
                                onChange()
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.yes))
                    }
                }
            },
            text = {
                Text(stringResource(id = R.string.delete_confirmation))
            }
        )
    }
}