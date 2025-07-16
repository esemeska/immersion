package ua.senalll.immersionCore.chat.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import ua.senalll.immersionCore.chat.ChatUtils
import ua.senalll.immersionCore.config.yml.ChatConfig
import ua.senalll.immersionapi.component.plainText
import java.util.concurrent.ThreadLocalRandom

class RPCommand(private val chatConfig: ChatConfig) {

    fun coinCommand(commandName: String = "coin"): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .executes { ctx ->
                val sender = ctx.source.sender as Player
                val coinSettings = chatConfig.coinCmd()
                if(!(coinSettings["enabled"] as Boolean)) Command.SINGLE_SUCCESS

                val coinFace = (if (ThreadLocalRandom.current().nextInt(2) == 0)
                    coinSettings["first-face"] else coinSettings["second-face"]) as String
                val msg = MiniMessage.miniMessage().deserialize (
                    (coinSettings["format"] as String)
                    .replace("%sender%", sender.displayName().plainText())
                    .replace("%result%", coinFace)
                )
                ChatUtils.sendLocalMsg(sender, msg, coinSettings["radius"] as Int)
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    fun doCommand(commandName: String = "do"): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val doSettings = chatConfig.doCmd()
                    if(!(doSettings["enabled"] as Boolean)) Command.SINGLE_SUCCESS

                    val msgArgument = StringArgumentType.getString(ctx, "message")
                    val msg = MiniMessage.miniMessage().deserialize (
                        (doSettings["format"] as String)
                            .replace("%sender%", sender.displayName().plainText())
                            .replace("%result%", msgArgument)
                    )
                    ChatUtils.sendLocalMsg(sender, msg, doSettings["radius"] as Int)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    fun meCommand(commandName: String = "me"): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val meSettings = chatConfig.meCmd()
                    if(!(meSettings["enabled"] as Boolean)) Command.SINGLE_SUCCESS

                    val msgArgument = StringArgumentType.getString(ctx, "message")
                    val msg = MiniMessage.miniMessage().deserialize (
                        (meSettings["format"] as String)
                            .replace("%sender%", sender.displayName().plainText())
                            .replace("%result%", msgArgument)
                    )
                    ChatUtils.sendLocalMsg(sender, msg, meSettings["radius"] as Int)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    fun tryCommand(commandName: String = "try"): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val trySettings = chatConfig.tryCmd()
                    if(!(trySettings["enabled"] as Boolean)) Command.SINGLE_SUCCESS

                    val msgArgument = StringArgumentType.getString(ctx, "message")
                    val msg = MiniMessage.miniMessage().deserialize (
                        (trySettings["format"] as String)
                            .replace("%sender%", sender.displayName().plainText())
                            .replace("%action%", msgArgument)
                            .replace("%result%", (trySettings["results"] as List<String>).random())
                    )
                    ChatUtils.sendLocalMsg(sender, msg, trySettings["radius"] as Int)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    fun whispCommand(commandName: String = "whisp"): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .then(Commands.argument("message", StringArgumentType.greedyString())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val whispSettings = chatConfig.whispCmd()
                    if(!(whispSettings["enabled"] as Boolean)) Command.SINGLE_SUCCESS

                    val msgArgument = StringArgumentType.getString(ctx, "message")
                    val msg = MiniMessage.miniMessage().deserialize (
                        (whispSettings["format"] as String)
                            .replace("%sender%", sender.displayName().plainText())
                            .replace("%result%", msgArgument)
                    )
                    ChatUtils.sendLocalMsg(sender, msg, whispSettings["radius"] as Int)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }


}