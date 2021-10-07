package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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

    fun change() {
        OverlayAPI.getInstance(context) { api ->
            api.setEnabled(FabricatedOverlay.generateOverlayIdentifier(info.overlayName!!, info.packageName), !info.isEnabled, 0)
            onChange()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                change()
            }.fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.ic_baseline_delete_24),
                contentDescription = stringResource(R.string.remove_overlay),
                modifier = Modifier.clickable {
                    OverlayAPI.getInstance(context) { api ->
                        api.unregisterFabricatedOverlay(
                            FabricatedOverlay.generateOverlayIdentifier(
                                info.overlayName!!
                            )
                        )
                        onChange()
                    }
                }.align(Alignment.CenterVertically)
            )

            Text(
                text = "${info.packageName}:${info.overlayName}",
                modifier = Modifier.align(Alignment.CenterVertically)
            )

            Checkbox(
                checked = info.isEnabled,
                onCheckedChange = {
                    change()
                },
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}