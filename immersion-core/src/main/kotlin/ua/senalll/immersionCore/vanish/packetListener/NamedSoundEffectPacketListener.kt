package ua.senalll.immersionCore.vanish.packetListener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig

class NamedSoundEffectPacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.HIGH,
    PacketType.Play.Server.NAMED_SOUND_EFFECT
) {

    override fun onPacketSending(event: PacketEvent) {
        if (event.packet.soundCategories.read(0) == EnumWrappers.SoundCategory.PLAYERS) {
            val x = event.packet.integers.read(0) / 8
            val y = event.packet.integers.read(1) / 8
            val z = event.packet.integers.read(2) / 8

            val viewer = event.player

            for (uuid in vanishConfig.getVanishedPlayers()) {
                val vanishedPlayer = Bukkit.getPlayer(uuid)
                if (vanishedPlayer != null &&
                    viewer.world == vanishedPlayer.world &&
                    vanishedPlayer.location.distanceSquared(
                        Location(
                            vanishedPlayer.world,
                            x.toDouble(),
                            y.toDouble(),
                            z.toDouble()
                        )
                    ) < 2.0
                ) {
                    event.isCancelled = true
                    break
                }
            }
        }
    }
}
