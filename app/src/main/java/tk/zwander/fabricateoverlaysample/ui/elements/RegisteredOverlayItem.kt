package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import tk.zwander.fabricateoverlay.FabricatedOverlay
import tk.zwander.fabricateoverlay.OverlayAPI
import tk.zwander.fabricateoverlay.OverlayInfo
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.dialogs.RemoveOverlayDialog

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
            .heightIn(min = 48.dp)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    change()
                }
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(8.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(R.string.remove_overlay),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false)
                    ) {
                        showingRemoveDialog = true
                    }
                    .align(Alignment.CenterVertically)
                    .size(48.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Inside
            )

            Spacer(Modifier.size(8.dp))

            Text(
                text = "${info.packageName}:${info.overlayName}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .fillMaxHeight()
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
        RemoveOverlayDialog(
            info = info,
            onDismiss = { showingRemoveDialog = false },
            onChange = onChange
        )
    }
}