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

class WorldParticlesPacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.HIGH,
    PacketType.Play.Server.WORLD_PARTICLES
) {

    override fun onPacketSending(event: PacketEvent) {
        if (event.packet.particles.read(0) == EnumWrappers.Particle.BLOCK_DUST) {
            val x = event.packet.float.read(0)
            val y = event.packet.float.read(1)
            val z = event.packet.float.read(2)

            val viewer = event.player

            for (uuid in vanishConfig.getVanishedPlayers()) {
                val vanishedPlayer = Bukkit.getPlayer(uuid) ?: continue

                if (viewer.world == vanishedPlayer.world
                    && vanishedPlayer.location.distanceSquared(
                        Location(vanishedPlayer.world, x.toDouble(), y.toDouble(), z.toDouble())
                    ) < 3.0
                ) {
                    event.isCancelled = true
                    break
                }
            }
        }
    }
}
