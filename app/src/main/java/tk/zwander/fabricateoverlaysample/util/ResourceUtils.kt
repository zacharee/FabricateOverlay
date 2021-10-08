package tk.zwander.fabricateoverlaysample.util

import android.content.Context
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
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
import java.nio.ByteBuffer
import java.util.*
import java.util.regex.Pattern
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
    context: Context,
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

                    val fqrn = "${apk.apkMeta.packageName}:${r[0].type.name}/${r[0].resourceEntry.key}"

                    list[t]!!.add(AvailableResourceItemData(
                        fqrn,
                        type,
                        context.getCurrentResourceValue(apk.apkMeta.packageName, fqrn)
                    ))
                } catch (e: NotFoundException) {
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

fun Context.getCurrentResourceValue(packageName: String, fqrn: String): Array<String> {
    val res = packageManager.getResourcesForApplication(packageName)

    try {
        val value = TypedValue()
        res.getValue(fqrn, value, false /* resolveRefs */)
        val valueString = value.coerceToString()
        res.getValue(fqrn, value, true /* resolveRefs */)
        val resolvedString = value.coerceToString()

        return arrayOf(
            if (valueString == resolvedString) {
                resolvedString.toString()
            } else {
                "$valueString -> $resolvedString"
            }
        )
    } catch (e: NotFoundException) {}

    return try {
        val regex = Pattern.compile("(.*?):(.*?)/(.*?)");
        val matcher = regex.matcher(fqrn);

        val pkg = matcher.group(1)
        val type = matcher.group(2)
        val name = matcher.group(3)
        val resid = res.getIdentifier(name, type, pkg)
        if (resid == 0) {
            throw NotFoundException()
        }
        val array = res.obtainTypedArray(resid)
        val tv = TypedValue()

        val items = ArrayList<String>(array.length())

        for (i in 0 until array.length()) {
            array.getValue(i, tv)
            items.add(tv.coerceToString().toString())
        }
        array.recycle()

        items.toTypedArray()
    } catch (e: NotFoundException) {
        throw IllegalStateException("Unable to retrieve resource.")
    }
}