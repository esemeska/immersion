package ua.senalll.immersionLitematica.pdc

import org.bukkit.NamespacedKey

import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID

object OutlinePDCHandler: PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionLitematica.instance, "outline_uuid")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun hasUUID(textDisplay: TextDisplay): Boolean {
        return has(textDisplay.persistentDataContainer)
    }

    fun getUUID(textDisplay: TextDisplay,): UUID {
        return UUID.fromString(get(textDisplay.persistentDataContainer).toString())
    }

    fun setOutlineUUID(textDisplay: TextDisplay, uuid: UUID) {
        set(textDisplay.persistentDataContainer, uuid.toString())
    }

    fun removeUUID(textDisplay: TextDisplay) {
        remove(textDisplay.persistentDataContainer)
    }
}