package tk.zwander.fabricateoverlaysample.ui.elements

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tk.zwander.fabricateoverlaysample.R

@Composable
fun AppItem(
    info: ApplicationInfo,
    onClick: (ApplicationInfo) -> Unit
) {
    val context = LocalContext.current

    var label by remember { mutableStateOf("") }
    var icon by remember { mutableStateOf<Drawable?>(null) }

    LaunchedEffect(info) {
        label = withContext(Dispatchers.IO) {
            info.loadLabel(context.packageManager).toString()
        }
        icon = withContext(Dispatchers.IO) {
            info.loadIcon(context.packageManager)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
            .heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.clickable {
                onClick(info)
            }.fillMaxSize()
        ) {
            Image(
                painter = icon?.let { BitmapPainter(it.toBitmap().asImageBitmap()) } ?: painterResource(R.drawable.ic_baseline_help_24),
                contentDescription = null
            )

            Spacer(Modifier.size(8.dp))

            Text(text = label)
        }
    }
}