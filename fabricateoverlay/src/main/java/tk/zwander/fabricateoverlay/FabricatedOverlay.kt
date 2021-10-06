package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint
import android.util.TypedValue

/**
 * This class represents a fabricated overlay. Fabricated overlays
 * contain entries of overlayed resources, such as integers, booleans,
 * and colors.
 *
 * @see [FabricatedOverlayEntry]
 *
 * @param overlayName the name of this fabricated overlay. It's
 *                  recommended that you prepend this with the package
 *                  name of your app to avoid potential conflicts.
 *                  (e.g, "tk.zwander.fabricatedoverlaysample.ExampleOverlay")
 * @param targetPackage the package name of the app to be overlayed.
 * @param sourcePackage in most cases this should stay as "com.android.shell".
 *                      If your app has its own Java root access implementation
 *                      (or your app is a privileged app), you can change this to
 *                      match your application package.
 */
@SuppressLint("PrivateApi")
class FabricatedOverlay(
    val overlayName: String,
    val targetPackage: String,
    val sourcePackage: String = "com.android.shell"
) {
    companion object {
        private val oiClass = Class.forName("android.content.om.OverlayIdentifier")

        /**
         * Generate the OverlayIdentifier instance for this fabricated overlay.
         * This is used if you need to unregister a fabricated overlay.
         */
        fun generateOverlayIdentifier(
            overlayName: String,
            sourcePackage: String = "com.android.shell"
        ): Any {
            return oiClass.getConstructor(String::class.java, String::class.java)
                .newInstance(sourcePackage, overlayName)
        }
    }

    /**
     * The overlay entries. Names should be unique. If a resource value
     * is set it will replace any previous one with the same name.
     */
    val entries = hashMapOf<String, FabricatedOverlayEntry>()

    /**
     * A convenience value to get this instance's overlay identifier.
     */
    val identifier = generateOverlayIdentifier(overlayName, sourcePackage)

    /**
     * Set an integer overlay value.
     *
     * @param name a String representing the resource name that should be overlayed.
     *              The format can either be just the name, or be the fully qualified
     *              name (e.g, "quick_settings_num_columns" vs. "com.android.systemui:integer/quick_settings_num_columns").
     * @param value the integer value to apply.
     */
    fun setInteger(name: String, value: Int) {
        val formattedName = formatName(name, "integer")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_DEC,
            value
        )
    }

    /**
     * Set a boolean overlay value.
     *
     * @param name a String representing the resource name that should be overlayed.
     *              The format can either be just the name, or be the fully qualified
     *              name (e.g, "show_navbar" vs. "com.android.systemui:bool/show_navbar").
     * @param value the boolean value to apply.
     */
    fun setBoolean(name: String, value: Boolean) {
        val formattedName = formatName(name, "bool")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_BOOLEAN,
            if (value) 1 else 0
        )
    }

    /**
     * Set a dimension overlay value.
     *
     * @param name a String representing the resource name that should be overlayed.
     *              The format can either be just the name, or be the fully qualified
     *              name (e.g, "status_bar_size" vs. "com.android.systemui:dimen/status_bar_size").
     * @param value the dimension value to apply. Remember to use [TypedValue.applyDimension] on your dimension
     *              and then convert to an integer.
     */
    fun setDimension(name: String, value: Int) {
        val formattedName = formatName(name, "dimen")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_DIMENSION,
            value
        )
    }

    /**
     * Set an attribute overlay value.
     *
     * @param name a String representing the resource name that should be overlayed.
     *              The format can either be just the name, or be the fully qualified
     *              name (e.g, "selectedItemBackground" vs. "com.android.systemui:attr/selectedItemBackground").
     * @param value the resource ID of the attr value to apply.
     */
    fun setAttribute(name: String, value: Int) {
        val formattedName = formatName(name, "attr")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_ATTRIBUTE,
            value
        )
    }

    /**
     * Set a color overlay value.
     *
     * @param name a String representing the resource name that should be overlayed.
     *              The format can either be just the name, or be the fully qualified
     *              name (e.g, "status_bar_color" vs. "com.android.systemui:color/status_bar_color").
     * @param value the color value to apply. This should be a color integer.
     */
    fun setColor(name: String, value: Int) {
        val formattedName = formatName(name, "color")

        entries[formattedName] = FabricatedOverlayEntry(
            formattedName,
            TypedValue.TYPE_INT_COLOR_ARGB8,
            value
        )
    }

    /**
     * A helper method to format the given resource name if needed.
     */
    private fun formatName(name: String, type: String): String {
        return if (name.contains(":") && name.contains("/")) {
            name
        } else {
            "$targetPackage:$type/$name"
        }
    }
}