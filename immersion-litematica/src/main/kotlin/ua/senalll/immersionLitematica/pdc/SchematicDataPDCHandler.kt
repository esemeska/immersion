package ua.senalll.immersionLitematica.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.BlockDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID

object SchematicDataPDCHandler: PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionLitematica.instance, "schematic_data")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun hasData(blockDisplay: BlockDisplay): Boolean {
        return has(blockDisplay.persistentDataContainer)
    }

    fun getData(blockDisplay: BlockDisplay): Vector? {
        val raw = get(blockDisplay.persistentDataContainer) ?: return null
        val parts = raw.split(",")
        if (parts.size != 3) return null

        return try {
            Vector(parts[0].toDouble(), parts[1].toDouble(), parts[2].toDouble())
        } catch (e: NumberFormatException) {
            null
        }
    }

    fun setSchematicData(blockDisplay: BlockDisplay, vector: Vector) {
        val encoded = "${vector.x},${vector.y},${vector.z}"
        set(blockDisplay.persistentDataContainer, encoded)
    }

    fun removeData(blockDisplay: BlockDisplay) {
        remove(blockDisplay.persistentDataContainer)
    }
}