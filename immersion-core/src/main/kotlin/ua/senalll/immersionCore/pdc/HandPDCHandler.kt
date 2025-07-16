package ua.senalll.immersionCore.pdc

import org.bukkit.NamespacedKey
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.pdc.PersistentDataHandler
import java.util.UUID
import kotlin.String

object IsHandPDCHandler : PersistentDataHandler<Boolean> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "isHand")
    override val type: PersistentDataType<Byte, Boolean> = PersistentDataType.BOOLEAN

    fun isHand(entity: Entity): Boolean {
        return get(entity.persistentDataContainer) ?: false
    }

    fun setIsHand(entity: Entity, value: Boolean = true) {
        set(entity.persistentDataContainer, value)
    }
}

object PlayerHasHandPDCHandler : PersistentDataHandler<Boolean> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "hasHand")
    override val type: PersistentDataType<Byte, Boolean> = PersistentDataType.BOOLEAN

    fun playerHasHand(player: Player): Boolean {
        return get(player.persistentDataContainer) ?: false
    }

    fun setPlayerHasHand(player: Player, value: Boolean = true) {
        set(player.persistentDataContainer, value)
    }
}

object HandIDPDCHandler : PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "id")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun getHandUUID(entity: Entity): UUID? {
        val idString = get(entity.persistentDataContainer) ?: return null
        return UUID.fromString(idString)
    }

    fun setHandUUID(entity: Entity, uuid: UUID) {
        set(entity.persistentDataContainer, uuid.toString())
    }
}

object HandsPlayerUUIDPDCHandler : PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "player_uuid")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun getPlayerUUID(entity: Entity): UUID? {
        val idString = get(entity.persistentDataContainer) ?: return null
        return UUID.fromString(idString)
    }

    fun setPlayerUUID(entity: Entity, uuid: UUID) {
        set(entity.persistentDataContainer, uuid.toString())
    }
}

object PlayersHandUUIDPDCHandler : PersistentDataHandler<String> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "hand_uuid")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING

    fun getHandUUID(player: Player): UUID? {
        val idString = get(player.persistentDataContainer) ?: return null
        return UUID.fromString(idString)
    }

    fun setHandUUID(player: Player, uuid: UUID) {
        set(player.persistentDataContainer, uuid.toString())
    }

    fun removeHandUUID(player: Player) {
        remove(player.persistentDataContainer)
    }
}

object HandMoveTaskIdPDCHandler : PersistentDataHandler<Int> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "move_task_uuid")
    override val type: PersistentDataType<Int, Int> = PersistentDataType.INTEGER

    fun getTaskId(entity: Entity): Int {
        return get(entity.persistentDataContainer) as Int
    }

    fun setTaskId(entity: Entity, taskId: Int) {
        set(entity.persistentDataContainer, taskId)
    }
}

object HandHighTaskIdPDCHandler : PersistentDataHandler<Int> {
    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "high_task_uuid")
    override val type: PersistentDataType<Int, Int> = PersistentDataType.INTEGER

    fun getTaskId(entity: Entity): Int {
        return get(entity.persistentDataContainer) as Int
    }

    fun setTaskId(entity: Entity, taskId: Int) {
        set(entity.persistentDataContainer, taskId)
    }
}

