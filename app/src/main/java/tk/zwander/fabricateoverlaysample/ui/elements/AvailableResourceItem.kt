package tk.zwander.fabricateoverlaysample.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData

@Composable
fun AvailableResourceItem(
    data: AvailableResourceItemData,
    onClick: (AvailableResourceItemData) -> Unit
) {
    Card(
        modifier = Modifier.heightIn(min = 48.dp)
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Text(
            text = data.name,
            modifier = Modifier.clickable {
                onClick(data)
            }
                .padding(8.dp)
        )
    }
}