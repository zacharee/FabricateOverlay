package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.util.TypedValue

@SuppressLint("PrivateApi")
class FabricatedOverlay(
    val overlayName: String,
    val targetPackage: String,
    val sourcePackage: String = "com.android.shell"
) {
    companion object {
        private val oiClass = Class.forName("android.content.om.OverlayIdentifier")

        fun generateOverlayIdentifier(
            overlayName: String,
            sourcePackage: String = "com.android.shell"
        ): Any {
            return oiClass.getConstructor(String::class.java, String::class.java)
                .newInstance(sourcePackage, overlayName)
        }
    }

    val entries = hashMapOf<String, FabricatedOverlayEntry>()
    val identifier = generateOverlayIdentifier(overlayName, sourcePackage)

    fun setInteger(name: String, value: Int) {
        val formattedName = formatName(name, "integer")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_DEC,
            value
        )
    }

    fun setBoolean(name: String, value: Boolean) {
        val formattedName = formatName(name, "bool")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_BOOLEAN,
            if (value) 1 else 0
        )
    }

    fun setDimension(name: String, value: Int) {
        val formattedName = formatName(name, "dimen")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_DIMENSION,
            value
        )
    }

    fun setAttribute(name: String, value: Int) {
        val formattedName = formatName(name, "attr")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_ATTRIBUTE,
            value
        )
    }

    fun setColor(name: String, value: Int) {
        val formattedName = formatName(name, "color")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_COLOR_ARGB8,
            value
        )
    }

    private fun formatName(name: String, type: String): String {
        return if (name.contains(":") && name.contains("/")) {
            name
        } else {
            "$targetPackage:$type/$name"
        }
    }
}