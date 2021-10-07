package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import tk.zwander.fabricateoverlaysample.data.ResourceItemData

@Composable
fun ResourceItem(
    data: ResourceItemData,
    onClick: (ResourceItemData) -> Unit
) {
    Row {
        Text(data.name)
        Text(data.value)
    }
}