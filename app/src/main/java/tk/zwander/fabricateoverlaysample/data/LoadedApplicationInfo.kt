package tk.zwander.fabricateoverlaysample.data

import android.content.pm.ApplicationInfo
import android.graphics.drawable.Drawable

class LoadedApplicationInfo(
    val label: String,
    val icon: Drawable,
    val info: ApplicationInfo
) : Comparable<LoadedApplicationInfo> {
    override fun compareTo(other: LoadedApplicationInfo): Int {
        val labelComp = label.compareTo(other.label, true)

        return if (labelComp != 0) labelComp else info.packageName.compareTo(other.info.packageName)
    }
}