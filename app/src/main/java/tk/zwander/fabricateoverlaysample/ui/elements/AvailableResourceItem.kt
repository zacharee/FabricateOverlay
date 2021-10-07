package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData

@Composable
fun AvailableResourceItem(
    data: AvailableResourceItemData,
    onClick: (AvailableResourceItemData) -> Unit
) {
    Text(data.name)
}