package ua.senalll.immersionCore.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID

class RelationPDCHandler : PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "making_friend")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun setMakingFriend(player: Player, friend: Player) {
        val list = getMakingFriendList(player)
        if (!list.contains(friend.uniqueId)) {
            list.add(friend.uniqueId)
            val serialized = list.joinToString(";") { it.toString() }
            set(player.persistentDataContainer, serialized)
        }
    }

    fun removeMakingFriend(player: Player, friendToRemove: Player) {
        val list = getMakingFriendList(player)
        if (list.remove(friendToRemove.uniqueId)) {
            if (list.isEmpty()) {
                remove(player.persistentDataContainer)
            } else {
                val serialized = list.joinToString(";") { it.toString() }
                set(player.persistentDataContainer, serialized)
            }
        }
    }

    fun getMakingFriendList(player: Player): MutableList<UUID> {
        val data = get(player.persistentDataContainer)
        return if (data != null && data.isNotEmpty()) {
            data.split(";").map { UUID.fromString(it) }.toMutableList()
        } else {
            mutableListOf()
        }
    }
}