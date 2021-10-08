package tk.zwander.fabricateoverlaysample.ui.elements

import android.util.TypedValue
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
import tk.zwander.fabricateoverlaysample.util.TypedValueUtils

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
                Text(text = TypedValueUtils.typedValueTypeToString(info.resourceType))
                Text(text = when (info.resourceType) {
                    TypedValue.TYPE_INT_DEC -> info.resourceValue.toString()
                    TypedValue.TYPE_INT_COLOR_ARGB8 -> info.resourceValue.toUInt().toString(16)
                    TypedValue.TYPE_INT_BOOLEAN -> (info.resourceValue == 1).toString()
                    TypedValue.TYPE_DIMENSION -> TypedValue.coerceToString(info.resourceType, info.resourceValue)
                    else -> throw IllegalArgumentException("Invalid type ${info.resourceType}")
                })
            }
        }
    }
}