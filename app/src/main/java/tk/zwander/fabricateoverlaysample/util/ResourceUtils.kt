package tk.zwander.fabricateoverlaysample.util

import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import net.dongliu.apk.parser.AbstractApkFile
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.parser.ResourceTableParser
import net.dongliu.apk.parser.struct.AndroidConstants
import net.dongliu.apk.parser.struct.resource.ResourcePackage
import net.dongliu.apk.parser.struct.resource.ResourceTable
import tk.zwander.fabricateoverlaysample.data.AvailableResourceItemData
import tk.zwander.fabricateoverlaysample.data.ResourceItemData
import java.nio.ByteBuffer
import java.util.*
import kotlin.collections.ArrayList

@Suppress("UNCHECKED_CAST")
val ResourceTable.packageMap: Map<Short, ResourcePackage>
    get() = ResourceTable::class.java
        .getDeclaredField("packageMap")
        .apply { isAccessible = true }
        .get(this) as Map<Short, ResourcePackage>

fun AbstractApkFile.getResourceTable(): ResourceTable {
    val data = getFileData(AndroidConstants.RESOURCE_FILE) ?: return ResourceTable()
    val buffer = ByteBuffer.wrap(data)
    val parser = ResourceTableParser(buffer)

    parser.parse()

    return parser.resourceTable
}

suspend fun getAppResources(
    apk: ApkFile
): Map<String, List<AvailableResourceItemData>> = coroutineScope {
    val table = apk.getResourceTable()
    val list = TreeMap<String, MutableList<AvailableResourceItemData>>()

    table.packageMap.forEach { (k, v) ->
        val (pkgCode, resPkg) = k.toInt() to v

        val integerIndex = resPkg.typeSpecMap.filter { it.value.name == "integer" }.entries.elementAtOrNull(0)
        val colorIndex = resPkg.typeSpecMap.filter { it.value.name == "color" }.entries.elementAtOrNull(0)
        val booleanIndex = resPkg.typeSpecMap.filter { it.value.name == "bool" }.entries.elementAtOrNull(0)
        val dimensionIndex = resPkg.typeSpecMap.filter { it.value.name == "dimen" }.entries.elementAtOrNull(0)

        val integerStart = integerIndex?.run { (key.toInt() shl 16) or (pkgCode shl 24) }
        val colorStart = colorIndex?.run { (key.toInt() shl 16) or (pkgCode shl 24) }
        val booleanStart = booleanIndex?.run { (key.toInt() shl 16) or (pkgCode shl 24) }
        val dimensionStart = dimensionIndex?.run { (key.toInt() shl 16) or (pkgCode shl 24) }

        val integerSize = integerIndex?.value?.entryFlags?.size
        val colorSize = colorIndex?.value?.entryFlags?.size
        val booleanSize = booleanIndex?.value?.entryFlags?.size
        val dimensionSize = dimensionIndex?.value?.entryFlags?.size

        val loopRange: suspend CoroutineScope.(start: Int, end: Int, type: Int) -> Unit = { start: Int, end: Int, type: Int ->
            for (i in start until end) {
                try {
                    val r = table.getResourcesById(i.toLong())
                    if (r.isEmpty()) continue

                    val t = r[0].type.name

                    if (list[t] == null) {
                        list[t] = ArrayList()
                    }

                    list[t]!!.add(AvailableResourceItemData(
                        "${apk.apkMeta.packageName}:${r[0].type.name}/${r[0].resourceEntry.key}",
                        type
                    ))
                } catch (e: Resources.NotFoundException) {
                }
            }
        }

        integerStart?.let {
            loopRange(it, it + integerSize!!, TypedValue.TYPE_INT_DEC)
        }

        colorStart?.let {
            loopRange(it, it + colorSize!!, TypedValue.TYPE_INT_COLOR_ARGB8)
        }

        booleanStart?.let {
            loopRange(it, it + booleanSize!!, TypedValue.TYPE_INT_BOOLEAN)
        }

        dimensionStart?.let {
            loopRange(it, it + dimensionSize!!, TypedValue.TYPE_DIMENSION)
        }
    }

    list.forEach { (_, v) ->
        v.sort()
    }

    list
}