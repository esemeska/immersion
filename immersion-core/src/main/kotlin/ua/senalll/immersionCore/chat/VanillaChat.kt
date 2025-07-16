package ua.senalll.immersionCore.chat

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.Style
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.chat.format.ProfileFormat
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.white

object VanillaChat : Listener {

    private const val JOIN = "join"
    private const val QUIT = "quit"
    private val config = ImmersionCore.instance.coreConfig

    fun createJoinMsg(joinPlayer: Player): Component {
        return formatName(joinPlayer, JOIN)
            .style(Style.style(ClickEvent.runCommand("/hi " + joinPlayer.displayName())))
    }

    fun createQuitMsg(quitPlayer: Player): Component {
        return formatName(quitPlayer, QUIT)
    }

    fun joinRandomMsg(): String {
        val messages = config.getStringList("chat.join.types")
        return messages.random()
    }

    fun quitRandomMsg(): String {
        val messages = config.getStringList("chat.quit.types")
        return messages.random()
    }

    fun formatName(player: Player, type: String): Component {
        val playerName = player.displayName().plainText()
        val template = if (type.equals(JOIN, ignoreCase = true)) joinRandomMsg() else quitRandomMsg()
        val replaced = template.replace("%player%", playerName)

        val words = replaced.split("\\s+".toRegex())
        var component = Component.empty()

        for (word in words) {
            component = if (word == playerName) {
                component.append(ProfileFormat.playerProfile(player)).append(" ".white())
            } else {
                component.append("$word ".gray())
            }
        }
        return component
    }


    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (ImmersionCore.instance.dataConfig.vanishConfig.isPlayerVanished(e.player)) return
        e.joinMessage(createJoinMsg(e.player))
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        e.quitMessage(createQuitMsg(e.player))
    }
}