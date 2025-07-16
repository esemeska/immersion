package ua.senalll.immersionCore.vanish.packetListener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig

class PlayerInfoPacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.HIGH,
    PacketType.Play.Server.PLAYER_INFO
) {

    override fun onPacketSending(event: PacketEvent) {
        val receiver = event.player
        val vanishedPlayers = vanishConfig.getVanishedPlayers()

        val playerInfoDataList = event.packet.playerInfoDataLists.read(0)

        val filteredList = playerInfoDataList.filter { playerInfoData ->
            if (playerInfoData == null) return@filter false
            val playerInfoUUID = playerInfoData.profile.uuid

            if (receiver.uniqueId == playerInfoUUID) true
            else !vanishedPlayers.contains(playerInfoUUID)
        }

        event.packet.playerInfoDataLists.write(1, filteredList)
    }
}
