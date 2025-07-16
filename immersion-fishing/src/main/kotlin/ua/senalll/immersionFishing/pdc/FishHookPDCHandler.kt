package ua.senalll.immersionFishing.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.FishHook
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionFishing.ImmersionFishing
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID

object FishHookPDCHandler: PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionFishing.instance, "player_uuid")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun setUUIDToHook(fishHook: FishHook, uuid: UUID){
        set(fishHook.persistentDataContainer, uuid.toString())
    }

    fun removeUUID(fishHook: FishHook){
        remove(fishHook.persistentDataContainer)
    }

    fun hasPlayer(fishHook: FishHook): Boolean{
        return has(fishHook.persistentDataContainer)
    }

    fun getUUID(fishHook: FishHook): UUID{
        return UUID.fromString(get(fishHook.persistentDataContainer).toString())
    }
}