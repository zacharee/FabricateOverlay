package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R

@Composable
fun CurrentOverlayEntriesItem(
    info: FabricatedOverlayEntry,
    onRemove: (FabricatedOverlayEntry) -> Unit
) {
    Card {
        Row {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = null,
                modifier = Modifier.clickable {
                    onRemove(info)
                }
            )

            Column {
                Text(text = info.resourceName)
                Text(text = info.resourceType.toString())
                Text(text = info.resourceValue.toString())
            }
        }
    }
}