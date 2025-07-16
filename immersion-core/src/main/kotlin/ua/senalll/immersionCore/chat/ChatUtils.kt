package ua.senalll.immersionCore.chat

import com.mojang.brigadier.context.CommandContext
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.Style
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.component.darkGray
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.white
import ua.senalll.immersionapi.component.yellow

object ChatUtils {
    private val chatConfig = ImmersionCore.instance.coreConfig.chatConfig

    fun getMsg(args: Array<String>): String {
        val theFinalString = StringBuilder()
        val ag = args.size
        val a = ag - 1

        for (i in 1 until args.size - 1) {
            theFinalString.append(args[i]).append(' ')
        }

        theFinalString.append(args[a])
        return theFinalString.toString().trim()
    }

    fun senderSaidHi(sender: Player, target: Player){
        sender.sendMessage("*".yellow()
            .append("Ты передал привет ".gray())
            .append(sender.displayName().color(gray))
            .append("*".yellow()).italic()
        )
    }

    fun hiToTarget(sender: Player, target: Player){
        target.sendMessage("*".yellow()
            .append("Тебе передал привет ".gray())
            .append(sender.displayName().color(gray))
            .append("*".yellow()).italic()
        )
    }

    fun getPlayerFromArgument(ctx: CommandContext<CommandSourceStack>): Player{
        return ctx.getArgument("player", PlayerSelectorArgumentResolver::class.java)
            .resolve(ctx.source).first()
    }

    fun pmToSender(sender: Player, target: Player, text: String): Component {
        return "[ЛС]".darkGray()
            .append(
                (" Ты написал ${target.name().plainText()} : $text").gray()
            )
    }

    fun pmToTarget(sender: Player, target: Player, text: String): Component {
        return "[ЛС] ".darkGray()
            .append(("${sender.name().plainText()} пишет тебе: $text").gray())
            .appendNewline()
            .append(extensionMsg(sender))
    }

    fun extensionMsg(sender: Player): Component {
        val nick = sender.name().plainText()
        return Component.text("     ").append(
            "[Игнорировать] ".darkGray()
                .style(Style.style(ClickEvent.runCommand("/ignore $nick")))
                .hoverEvent("ЛКМ, чтобы игнорировать сообщения от $nick".white())
        )
            .append(
                "[Ответить]".darkGray()
                    .style(Style.style(ClickEvent.suggestCommand("/m $nick ")))
                    .hoverEvent("ЛКМ, чтобы ответить $nick".white())
            )
    }

    fun toAllPlayers(msg: Component) {
        if (Bukkit.getOnlinePlayers().isNotEmpty()) {
            Bukkit.getOnlinePlayers().forEach { player -> player.sendMessage(msg) }
        }
    }

    fun filterLocalRecipients(sender: Player): Audience {
        return sender.server.filterAudience { audience ->
            (audience is Player && isInLocal(audience, sender)) || audience is ConsoleCommandSender
        }
    }

    fun filterLocalRecipients(sender: Player, radius: Int): Audience {
        return sender.server.filterAudience { audience ->
            (audience is Player && isInLocal(audience, sender, radius)) || audience is ConsoleCommandSender
        }
    }

    fun hasGlobalPrefix(msg: Component): Boolean{
        return msg.plainText().startsWith(chatConfig.globalChatSymbol())
    }

    fun isInLocal(target: Player, sender: Player, radius: Int = chatConfig.localChatRadius()): Boolean {
        return sender.world == target.world && sender.location.distance(target.location) <=  radius
    }

    fun sendLocalMsg(player: Player, msg: Component, radius: Int){
        val recipients = filterLocalRecipients(player, radius)
        recipients.sendMessage(msg)
    }
}