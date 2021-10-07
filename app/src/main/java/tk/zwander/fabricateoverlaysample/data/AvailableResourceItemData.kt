package tk.zwander.fabricateoverlaysample.data

data class AvailableResourceItemData(
    val name: String,
    val type: Int
) : Comparable<AvailableResourceItemData> {
    override fun compareTo(other: AvailableResourceItemData): Int {
        return name.compareTo(other.name)
    }
}