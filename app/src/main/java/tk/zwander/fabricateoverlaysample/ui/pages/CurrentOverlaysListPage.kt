package tk.zwander.fabricateoverlaysample.ui.pages

import androidx.compose.runtime.Composable
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry

@Composable
fun CurrentOverlaysListPage(
    overlays: List<FabricatedOverlayEntry>,
    onRemoveOverlay: (entry: FabricatedOverlayEntry) -> Unit,
    onLaunchEditor: (initial: FabricatedOverlayEntry) -> Unit
) {

}