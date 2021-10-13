package tk.zwander.fabricateoverlaysample.ui.pages

import android.content.pm.ApplicationInfo
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.ui.elements.CurrentOverlayEntriesItem
import tk.zwander.fabricateoverlaysample.ui.elements.dialogs.ListAvailableResourcesDialog
import tk.zwander.fabricateoverlaysample.ui.elements.dialogs.SaveOverlayDialog

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CurrentOverlayEntriesListPage(
    navController: NavController,
    info: ApplicationInfo
) {
    var showingResDialog by remember { mutableStateOf(false) }
    var showingSaveDialog by remember { mutableStateOf(false) }
    val overlayEntries = remember { mutableStateListOf<FabricatedOverlayEntry>() }

    Column {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(overlayEntries.size) { index ->
                CurrentOverlayEntriesItem(info = overlayEntries[index]) {
                    overlayEntries.remove(it)
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
                modifier = Modifier
                    .weight(1f)
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
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                enabled = overlayEntries.isNotEmpty()
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
            onAddEntry = { overlayEntries.add(it) }
        )
    }

    if (showingSaveDialog) {
        SaveOverlayDialog(
            info = info,
            overlayEntries = overlayEntries,
            onDismiss = { showingSaveDialog = false },
            navController = navController
        )
    }
}