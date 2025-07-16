package ua.senalll.immersionCore.vanish

import com.comphenix.protocol.ProtocolLibrary
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.config.json.DataConfig

object VanishManager {
    fun initPacketListeners(plugin: JavaPlugin, dataConfig: DataConfig = (plugin as ImmersionCore).dataConfig) {
        try {
            if (!plugin.server.pluginManager.isPluginEnabled("ProtocolLib")) {
                plugin.logger.severe("ProtocolLib is required but not enabled. Vanish functionality will not work.")
                return
            }

            val protocolManager = ProtocolLibrary.getProtocolManager()
            //protocolManager.addPacketListener(ServerInfoPacketListener(plugin, dataJsonConfig))
            //protocolManager.addPacketListener(PlayerInfoPacketListener(plugin, dataJsonConfig))
            //protocolManager.addPacketListener(TabCompletePacketListener(plugin, dataJsonConfig))
            //protocolManager.addPacketListener(GeneralEntityPacketListener(plugin, dataJsonConfig))
            //protocolManager.addPacketListener(NamedSoundEffectPacketListener(plugin, dataJsonConfig))
            //protocolManager.addPacketListener(WorldParticlesPacketListener(plugin, dataJsonConfig))

            plugin.logger.info("Successfully initialized vanish packet listeners")
        } catch (e: Exception) {
            plugin.logger.severe("Failed to initialize vanish packet listeners: ${e.message}")
            e.printStackTrace()
        }
    }
}