package ua.senalll.immersionCore.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.pdc.PersistentDataHandler

class NickNamePDCHandler : PersistentDataHandler<Boolean>  {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "nickname_changing")
    override val type: PersistentDataType<Byte, Boolean> = PersistentDataType.BOOLEAN


    fun isChangingNickName(player: Player): Boolean{
        if (!has(player.persistentDataContainer)) return false
        return get(player.persistentDataContainer) as Boolean
    }

    fun setChangingNickName(player: Player, bool: Boolean){
        set(player.persistentDataContainer, bool)
        if (!bool) {
            remove(player.persistentDataContainer)
        }
    }

}