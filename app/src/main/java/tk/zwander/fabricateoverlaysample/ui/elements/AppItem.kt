package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import tk.zwander.fabricateoverlaysample.data.LoadedApplicationInfo

@Composable
fun AppItem(
    info: LoadedApplicationInfo,
    onClick: (LoadedApplicationInfo) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onClick(info)
                }
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Image(
                painter = BitmapPainter(info.icon.toBitmap().asImageBitmap()),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
                    .align(Alignment.CenterVertically)
            )

            Spacer(Modifier.size(8.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(text = info.label)

                Spacer(Modifier.size(8.dp))

                Text(text = info.info.packageName)
            }
        }
    }
}