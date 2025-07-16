package ua.senalll.immersionLitematica.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.BlockDisplay
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID

object SchematicUUIDPDCHandler: PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionLitematica.instance, "schematic_uuid")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun hasUUID(blockDisplay: BlockDisplay): Boolean {
        return has(blockDisplay.persistentDataContainer)
    }

    fun getUUID(blockDisplay: BlockDisplay): UUID {
        return UUID.fromString(get(blockDisplay.persistentDataContainer).toString())
    }

    fun setSchematicUUID(blockDisplay: BlockDisplay, uuid: UUID) {
        set(blockDisplay.persistentDataContainer, uuid.toString())
    }

    fun removeUUID(blockDisplay: BlockDisplay) {
        remove(blockDisplay.persistentDataContainer)
    }
}