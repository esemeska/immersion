package ua.senalll.immersionCore.vanish.packetListener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig

class GeneralEntityPacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.MONITOR,
    PacketType.Play.Server.SPAWN_ENTITY,
    PacketType.Play.Server.ENTITY_DESTROY,
    PacketType.Play.Server.ANIMATION,
    PacketType.Play.Server.BLOCK_BREAK_ANIMATION,
    PacketType.Play.Server.ENTITY_STATUS,
    PacketType.Play.Server.ENTITY_SOUND,
    PacketType.Play.Server.REL_ENTITY_MOVE,
    PacketType.Play.Server.REL_ENTITY_MOVE_LOOK,
    PacketType.Play.Server.ENTITY_LOOK,
    PacketType.Play.Server.ENTITY_HEAD_ROTATION,
    PacketType.Play.Server.ENTITY_METADATA,
    PacketType.Play.Server.ATTACH_ENTITY,
    PacketType.Play.Server.ENTITY_VELOCITY,
    PacketType.Play.Server.ENTITY_EQUIPMENT,
    PacketType.Play.Server.COLLECT,
    PacketType.Play.Server.ENTITY_TELEPORT,
    PacketType.Play.Server.ENTITY_EFFECT
)  {

    override fun onPacketSending(e: PacketEvent) {
        try {
            val receiver = e.player
            val packet = e.packet

            if (e.isReadOnly) e.isReadOnly = false

            when (packet.type) {
                PacketType.Play.Server.SPAWN_ENTITY -> {
                    val playerUUID = packet.uuiDs.read(0)
                    if (vanishConfig.isPlayerVanished(playerUUID)) {
                        e.isCancelled = true
                    }
                }

                PacketType.Play.Server.ENTITY_DESTROY -> {
                    if(packet.integerArrays != null && packet.integerArrays.size() > 0){
                        val entityIds = packet.integerArrays.read(0)
                        val entityIdList = mutableListOf<Int>()

                        for (entityId in entityIds) {
                            val entity = ProtocolLibrary.getProtocolManager().getEntityFromID(receiver.world, entityId)
                            if (entity !is Player || !vanishConfig.isPlayerVanished(entity.uniqueId)) {
                                entityIdList.add(entityId)
                            }
                        }

                        if (entityIdList.isNotEmpty()) {
                            packet.integerArrays.write(0, entityIdList.toIntArray())
                            return
                        }
                        e.isCancelled = true
                    }
                }

                else -> {
                    val entityId = if (packet.type == PacketType.Play.Server.COLLECT) {
                        packet.integers.read(1)
                    } else {
                        packet.integers.read(0)
                    }

                    val entity = ProtocolLibrary.getProtocolManager().getEntityFromID(receiver.world, entityId)
                    if (entity is Player && vanishConfig.isPlayerVanished(entity.uniqueId)) {
                        e.isCancelled = true
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}