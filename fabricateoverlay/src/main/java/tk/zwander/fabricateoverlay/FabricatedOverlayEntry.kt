package tk.zwander.fabricateoverlay

/**
 * A resource entry for a [FabricatedOverlay].
 *
 * @param resourceName the name of the resource to overlay. Should
 *   be fully qualified (e.g., "com.android.systemui:integer/quick_settings_num_columns").
 * @param resourceType the type of resource, as determined by [android.util.TypedValue].
 * @param resourceValue the value of the resource. Android 12's framework limits this
 *   to resources that can be represented as integers.
 */
data class FabricatedOverlayEntry(
    var resourceName: String,
    var resourceType: Int,
    var resourceValue: Int
)