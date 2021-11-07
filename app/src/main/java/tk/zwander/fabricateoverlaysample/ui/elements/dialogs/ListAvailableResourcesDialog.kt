package tk.zwander.fabricateoverlaysample.ui.elements.dialogs

import android.content.pm.ApplicationInfo
import android.util.TypedValue
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.dongliu.apk.parser.ApkFile
import tk.zwander.fabricateoverlay.FabricatedOverlayEntry
import tk.zwander.fabricateoverlaysample.R
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.ui.elements.AvailableResourceItem
import tk.zwander.fabricateoverlaysample.util.getAppResources

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun ListAvailableResourcesDialog(
    info: ApplicationInfo,
    onDismiss: () -> Unit,
    onAddEntry: (FabricatedOverlayEntry) -> Unit
) {
    var showingResDialog by remember { mutableStateOf(false) }
    var resData by remember { mutableStateOf<AvailableResourceItemData?>(null) }
    var resources by remember { mutableStateOf(mapOf<String, List<AvailableResourceItemData>>()) }
    var filter by remember { mutableStateOf("") }

    val context = LocalContext.current

    val transition = updateTransition(
        targetState = resources.isEmpty(),
        label = "progress"
    )

    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stringResource(id = R.string.resources),
                    modifier = Modifier
                        .padding(
                            top = 16.dp,
                            start = 8.dp
                        )
                        .fillMaxWidth(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.size(8.dp))

                TextField(
                    value = filter,
                    onValueChange = {
                        filter = it
                    },
                    label = {
                        Text(stringResource(id = R.string.search))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                            start = 8.dp,
                            end = 8.dp
                        )
                )

                LaunchedEffect("resources") {
                    @Suppress("DeferredResultUnused")
                    async(Dispatchers.IO) {
                        resources = getAppResources(context, ApkFile(info.sourceDir))
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    transition.AnimatedContent {
                        if (it) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                resources.forEach { (k, v) ->
                                    stickyHeader {
                                        Surface {
                                            Text(
                                                text = k,
                                                modifier = Modifier
                                                    .heightIn(min = 32.dp)
                                                    .fillMaxWidth()
                                                    .padding(8.dp),
                                                fontSize = 18.sp
                                            )
                                        }
                                    }

                                    items(v.size) { index ->
                                        val item = v[index]

                                        if (item.resourceName.contains(filter, true)) {
                                            AvailableResourceItem(item) { data ->
                                                resData = data
                                                showingResDialog = true
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text(stringResource(id = R.string.close))
                    }
                }
            }
        }
    }

    if (showingResDialog) {
        when (resData?.type) {
            TypedValue.TYPE_INT_DEC -> AddOverlayIntegerEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_INT_COLOR_ARGB8 -> AddOverlayColorEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_INT_BOOLEAN -> AddOverlayBooleanEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        if (it) 1 else 0
                    ))
                },
                resourceName = resData!!.name
            )

            TypedValue.TYPE_DIMENSION -> AddOverlayDimensionEntryDialog(
                onDismiss = { showingResDialog = false },
                onApply = {
                    showingResDialog = false
                    onAddEntry(FabricatedOverlayEntry(
                        resData!!.name,
                        resData!!.type,
                        it
                    ))
                },
                resourceName = resData!!.name
            )
        }
    }
}