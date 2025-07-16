package ua.senalll.immersionCore.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.pdc.PersistentDataHandler

class VanishPDCHandler: PersistentDataHandler<Boolean> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "vanished")
    override val type: PersistentDataType<Byte, Boolean> = PersistentDataType.BOOLEAN

    fun isVanished(player: Player): Boolean{
        if (!has(player.persistentDataContainer)) return false
        return get(player.persistentDataContainer) as Boolean
    }

    fun removeVanished(player: Player){
        if (isVanished(player)) {
            remove(player.persistentDataContainer)
        }
    }

    fun setVanished(player: Player, bool: Boolean){
        set(player.persistentDataContainer, bool)
    }
}