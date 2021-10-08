package tk.zwander.fabricateoverlaysample.ui.elements

import android.util.TypedValue
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import tk.zwander.fabricateoverlaysample.R

@Composable
fun AddOverlayIntegerEntryDialog(
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
    resourceName: String
) {
    AddOverlayEntryDialog(
        onDismiss,
        onApply = { value, _ -> onApply(value.toInt()) },
        resourceName,
        TypedValue.TYPE_INT_DEC
    )
}

@Composable
fun AddOverlayColorEntryDialog(
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
    resourceName: String
) {
    AddOverlayEntryDialog(
        onDismiss,
        onApply = { value, _ -> onApply(Integer.parseUnsignedInt(value.substring(2), 16)) },
        resourceName,
        TypedValue.TYPE_INT_COLOR_ARGB8,
        "0xaarrggbb"
    )
}

@Composable
fun AddOverlayBooleanEntryDialog(
    onDismiss: () -> Unit,
    onApply: (Boolean) -> Unit,
    resourceName: String
) {
    AddOverlayEntryDialog(
        onDismiss,
        onApply = { value, _ -> onApply(value.toBoolean()) },
        resourceName,
        TypedValue.TYPE_INT_BOOLEAN
    )
}

@Composable
fun AddOverlayDimensionEntryDialog(
    onDismiss: () -> Unit,
    onApply: (Int) -> Unit,
    resourceName: String
) {
    AddOverlayEntryDialog(
        onDismiss,
        onApply = { value, add ->
            val type = when (add) {
                "dp" -> TypedValue.COMPLEX_UNIT_DIP
                "px" -> TypedValue.COMPLEX_UNIT_PX
                "in" -> TypedValue.COMPLEX_UNIT_IN
                "pt" -> TypedValue.COMPLEX_UNIT_PT
                "mm" -> TypedValue.COMPLEX_UNIT_MM
                "sp" -> TypedValue.COMPLEX_UNIT_SP
                else -> throw IllegalArgumentException("Invalid dimension $add")
            }

            onApply(
                TypedValue::class.java
                    .getMethod("createComplexDimension", Int::class.java, Int::class.java)
                    .invoke(null, value.toInt(), type) as Int
            )
        },
        resourceName,
        TypedValue.TYPE_DIMENSION
    )
}

@Composable
fun AddOverlayEntryDialog(
    onDismiss: () -> Unit,
    onApply: (String, String) -> Unit,
    resourceName: String,
    resourceType: Int,
    labelExtras: String? = null
) {
    var value by remember { mutableStateOf("") }
    var valueAppend by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(resourceName)
        },
        text = {
            when (resourceType) {
                in listOf(
                    TypedValue.TYPE_INT_DEC,
                    TypedValue.TYPE_INT_COLOR_ARGB8
                ) -> {
                    TextField(
                        value = value,
                        onValueChange = {
                            value = it.run {
                                if (resourceType == TypedValue.TYPE_INT_COLOR_ARGB8) {
                                    filter { f ->
                                        f.isDigit() || f in listOf(
                                            'x',
                                            'a',
                                            'b',
                                            'c',
                                            'd',
                                            'e',
                                            'f'
                                        )
                                    }
                                } else {
                                    this
                                }
                            }
                        },
                        label = {
                            Text("${stringResource(R.string.value)}${if (labelExtras != null) " ($labelExtras)" else ""}")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = when (resourceType) {
                                TypedValue.TYPE_INT_DEC -> KeyboardType.Number
                                else -> KeyboardType.Text
                            }
                        )
                    )
                }

                TypedValue.TYPE_DIMENSION -> {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextField(
                            value = value,
                            onValueChange = {
                                value = it
                            },
                            label = {
                                Text("${stringResource(R.string.value)}${if (labelExtras != null) " ($labelExtras)" else ""}")
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        var expanded by remember { mutableStateOf(false) }

                        if (valueAppend.isBlank()) {
                            valueAppend = "dp"
                        }

                        Spacer(Modifier.size(8.dp))

                        Box {
                            Text(
                                text = valueAppend,
                                modifier = Modifier
                                    .clickable {
                                        expanded = true
                                    }
                                    .widthIn(min = 48.dp)
                                    .heightIn(min = 48.dp)
                            )

                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                listOf(
                                    "px",
                                    "dp",
                                    "pt",
                                    "in",
                                    "mm",
                                    "sp"
                                ).forEach { unit ->
                                    DropdownMenuItem(
                                        onClick = {
                                            expanded = false
                                            valueAppend = unit
                                        }
                                    ) {
                                        Text(unit)
                                    }
                                }
                            }
                        }
                    }
                }

                TypedValue.TYPE_INT_BOOLEAN -> {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = stringResource(id = R.string.enabled),
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )

                            Spacer(Modifier.weight(1f))

                            Checkbox(
                                checked = value == "true",
                                onCheckedChange = {
                                    value = it.toString()
                                },
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        },
        buttons = {
            Row {
                Button(
                    onClick = {
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        if (value.isNotBlank()) {
                            onApply(value, valueAppend)
                        } else {
                            Toast.makeText(
                                context,
                                context.resources.getString(R.string.please_enter_value),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Text(stringResource(R.string.apply))
                }
            }
        }
    )
}