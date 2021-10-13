package tk.zwander.fabricateoverlaysample.ui.elements

import android.util.TypedValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.util.TypedValueUtils

@Composable
fun CurrentOverlayEntriesItem(
    info: FabricatedOverlayEntry,
    onRemove: (FabricatedOverlayEntry) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .heightIn(min = 48.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp)
                .fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_baseline_delete_24),
                contentDescription = null,
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false)
                    ) {
                        onRemove(info)
                    }
                    .size(48.dp)
                    .align(Alignment.CenterVertically),
                contentScale = ContentScale.Inside
            )

            Spacer(Modifier.size(8.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(text = info.resourceName)
                Text(text = TypedValueUtils.typedValueTypeToString(info.resourceType))
                Text(text = when (info.resourceType) {
                    TypedValue.TYPE_INT_DEC -> info.resourceValue.toString()
                    TypedValue.TYPE_INT_COLOR_ARGB8 -> "0x${"%1$08x".format(info.resourceValue)}"
                    TypedValue.TYPE_INT_BOOLEAN -> (info.resourceValue == 1).toString()
                    TypedValue.TYPE_DIMENSION -> TypedValue.coerceToString(info.resourceType, info.resourceValue)
                    else -> throw IllegalArgumentException("Invalid type ${info.resourceType}")
                })
            }
        }
    }
}