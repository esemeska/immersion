package ua.senalll.immersionCore.vanish.packetListener

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.mojang.brigadier.suggestion.Suggestions
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig
import java.util.UUID

class TabCompletePacketListener(plugin: JavaPlugin, private val vanishConfig: VanishConfig) : PacketAdapter(
    plugin,
    ListenerPriority.HIGH,
    PacketType.Play.Server.TAB_COMPLETE
) {

    override fun onPacketSending(event: PacketEvent) {
        val suggestions = event.packet.getSpecificModifier(Suggestions::class.java).read(0)
        var writeChanges = false

        val iterator = suggestions.list.iterator()
        while (iterator.hasNext()) {
            val suggestion = iterator.next()
            val suggestionString = suggestion.text

            if (!suggestionString.contains("/") &&
                try {
                    vanishConfig.isPlayerVanished(UUID.fromString(suggestionString))
                } catch (e: IllegalArgumentException) {
                    false
                }) {
                writeChanges = true
                iterator.remove()
            }
        }

        if (writeChanges) {
            event.packet.getSpecificModifier(Suggestions::class.java).write(0, suggestions)
        }
    }
}
