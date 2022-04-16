package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.R

@Composable
fun RemoveOverlayDialog(
    info: OverlayInfo,
    onDismiss: () -> Unit,
    onChange: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                        .heightIn(min = 48.dp)
                ) {
                    Text(stringResource(id = R.string.no))
                }

                TextButton(
                    onClick = {
                        OverlayAPI.getInstance(context) { api ->
                            api.unregisterFabricatedOverlay(
                                FabricatedOverlay.generateOverlayIdentifier(
                                    info.overlayName,
                                    OverlayAPI.servicePackage ?: "com.android.shell"
                                )
                            )
                            onDismiss()
                            onChange()
                        }
                    },
                    modifier = Modifier.weight(1f)
                        .heightIn(min = 48.dp)
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