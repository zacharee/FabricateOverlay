package tk.zwander.fabricateoverlaysample.data

data class AvailableResourceItemData(
    val name: String,
    val resourceName: String,
    val type: Int,
    val values: Array<String>
) : Comparable<AvailableResourceItemData> {
    override fun compareTo(other: AvailableResourceItemData): Int {
        return name.compareTo(other.name)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailableResourceItemData

        if (name != other.name) return false
        if (type != other.type) return false
        if (!values.contentEquals(other.values)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + type
        result = 31 * result + values.contentHashCode()
        return result
    }
}