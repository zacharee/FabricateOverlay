package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.ListAvailableResourcesDialog

@Composable
fun CurrentOverlaysListPage(
    info: ApplicationInfo
) {
    var showingResDialog by remember { mutableStateOf(false) }
    val overlays = remember { mutableStateListOf<FabricatedOverlayEntry>() }

    Column {
        Button(
            onClick = {
                showingResDialog = true
            }
        ) {
            Text(stringResource(R.string.add))
        }

        LazyColumn {

        }
    }

    if (showingResDialog) {
        ListAvailableResourcesDialog(
            info = info,
            onDismiss = { showingResDialog = false },
            onAddEntry = { overlays.add(it) }
        )
    }
}