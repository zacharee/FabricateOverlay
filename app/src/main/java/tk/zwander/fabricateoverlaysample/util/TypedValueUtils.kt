package tk.zwander.fabricateoverlaysample.util

import android.util.TypedValue

object TypedValueUtils {
    fun typedValueTypeToString(type: Int): String {
        return when (type) {
            TypedValue.TYPE_DIMENSION -> "dimen"
            TypedValue.TYPE_INT_BOOLEAN -> "bool"
            TypedValue.TYPE_INT_COLOR_ARGB8 -> "color"
            TypedValue.TYPE_INT_DEC -> "integer"
            else -> throw IllegalArgumentException("Invalid type $type")
        }
    }
}