@file:Suppress("unused")

package tk.zwander.fabricateoverlay

import android.annotation.SuppressLint

data class OverlayInfo(
    val packageName: String,
    val overlayName: String?,
    val targetPackageName: String,
    val targetOverlayableName: String?,
    val category: String?,
    val baseCodePath: String,
    val state: Int,
    val userId: Int,
    val priority: Int,
    val isMutable: Boolean,
    val isFabricated: Boolean
) {
    companion object {
        const val STATE_UNKNOWN = -1
        const val STATE_MISSING_TARGET = 0
        const val STATE_NO_IDMAP = 1
        const val STATE_DISABLED = 2
        const val STATE_ENABLED = 3
        const val STATE_OVERLAY_IS_BEING_REPLACED = 5
        const val STATE_ENABLED_IMMUTABLE = 6

        @SuppressLint("PrivateApi")
        private val platformClass = Class.forName("android.content.om.OverlayInfo")
    }

    constructor(platformInfo: Any) : this(
        platformClass.getField("packageName")
            .get(platformInfo)!!.toString(),
        platformClass.getField("overlayName")
            .get(platformInfo)?.toString(),
        platformClass.getField("targetPackageName")
            .get(platformInfo)!!.toString(),
        platformClass.getField("targetOverlayableName")
            .get(platformInfo)?.toString(),
        platformClass.getField("category")
            .get(platformInfo)?.toString(),
        platformClass.getField("baseCodePath")
            .get(platformInfo)!!.toString(),
        platformClass.getField("state")
            .getInt(platformInfo),
        platformClass.getField("userId")
            .getInt(platformInfo),
        platformClass.getField("priority")
            .getInt(platformInfo),
        platformClass.getField("isMutable")
            .getBoolean(platformInfo),
        platformClass.getField("isFabricated")
            .getBoolean(platformInfo)
    )

    val isEnabled: Boolean
        get() = state in listOf(STATE_ENABLED, STATE_ENABLED_IMMUTABLE)
}