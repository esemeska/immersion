package ua.senalll.immersionCore.vanish.packetListener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig

class ServerInfoPacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.HIGH,
    PacketType.Status.Server.SERVER_INFO
) {

    override fun onPacketSending(event: PacketEvent) {
        val serverPing = event.packet.serverPings.read(0)
        val vanishedPlayers = vanishConfig.getVanishedPlayers()

        if (vanishedPlayers.isEmpty()) {
            serverPing.playersOnline = Bukkit.getOnlinePlayers().size
            return
        }

        val vanishedPlayerCount = vanishedPlayers.size
        serverPing.playersOnline = Bukkit.getOnlinePlayers().size - vanishedPlayerCount

        val wrappedGameProfiles = ArrayList(serverPing.players)
        wrappedGameProfiles.removeIf { profile ->
            vanishedPlayers.contains(profile.uuid)
        }
        serverPing.setPlayers(wrappedGameProfiles)
    }
}
