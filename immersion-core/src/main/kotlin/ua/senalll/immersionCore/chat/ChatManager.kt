package ua.senalll.immersionCore.chat

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Sound
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import ua.senalll.immersionCore.chat.format.FormatManager
import ua.senalll.immersionCore.chat.format.FormattedMsg
import ua.senalll.immersionCore.chat.format.ProfileFormat
import ua.senalll.immersionCore.config.yml.ChatConfig
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.database.AsyncManager


object ChatManager : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onChat(e: AsyncChatEvent) {
        e.isCancelled = true
        //if (PlayerState.isChangingNickName(e.player)) return
        if (e.message().plainText().isEmpty()) return

        if (ChatUtils.hasGlobalPrefix(e.message())) {
            AsyncManager.runAsync {
                handleGlobalChat(e)
            }
        } else {
            AsyncManager.runAsync {
                handleLocalChat(e)
            }
        }
    }

    suspend fun handleGlobalChat(e: AsyncChatEvent) {
        val rawMessage = e.message().plainText().substring(1)
        val formattedMsg = FormatManager.format(rawMessage)

        val baseMsg = Component.text("[КР] ").color(NamedTextColor.LIGHT_PURPLE)
            .append(buildBaseMessageComponent(e.player))

        val finalMessage = buildFinalMessage(baseMsg, formattedMsg)

        sendMessageWithEffects(e.player.server, finalMessage, formattedMsg, e.player)
    }

    suspend fun handleLocalChat(e: AsyncChatEvent) {
        val formattedMsg = FormatManager.format(e.message().plainText())
        val baseMsgComponent = buildBaseMessageComponent(e.player)

        val finalMessage = buildFinalMessage(baseMsgComponent, formattedMsg)

        val recipients = ChatUtils.filterLocalRecipients(e.player)

        sendMessageWithEffects(recipients, finalMessage, formattedMsg, e.player)
    }

    private fun buildBaseMessageComponent(player: Player): Component {
        return Component.empty().append(ProfileFormat.playerProfile(player))
            .append(Component.text(": ").color(NamedTextColor.WHITE))
    }

    private fun buildFinalMessage(baseMsg: Component, formattedMsg: FormattedMsg): Component {
        return when {
            formattedMsg.isMention || formattedMsg.isLink -> baseMsg.append(formattedMsg.formattedMsg)
            else -> baseMsg.append(formattedMsg.cleanMsg)
        }
    }

    private fun sendMessageWithEffects(recipients: Audience, message: Component,
                                       formattedMsg: FormattedMsg, sender: Player) {
        recipients.sendMessage(message)

        if (formattedMsg.isMention) {
            formattedMsg.mentionedPlayers.forEach { player ->
                if (ChatUtils.isInLocal(player, sender)) {
                    player.playSound(player, Sound.BLOCK_LARGE_AMETHYST_BUD_PLACE, 3.0f, 0.533f)
                }
            }
        }
    }



}