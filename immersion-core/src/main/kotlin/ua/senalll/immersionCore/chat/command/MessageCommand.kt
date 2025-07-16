package ua.senalll.immersionCore.chat.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import ua.senalll.immersionCore.chat.ChatUtils
import ua.senalll.immersionCore.config.json.ChatDataConfig
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.red

class MessageCommand(private val chatDataConfig: ChatDataConfig) : Listener {
    fun pmCommand(commandName: String = "msg"): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("player", ArgumentTypes.player())
                .then(Commands.argument("message", StringArgumentType.greedyString())
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        val target = ChatUtils.getPlayerFromArgument(ctx)

                        val msgToTarget = StringArgumentType.getString(ctx, "message")

                        if (target == sender){
                            sender.sendMessage("* Ты не можешь отправить сообщение самому себе *".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        if(chatDataConfig.isIgnoringPM(target.uniqueId.toString(), sender.uniqueId.toString())){
                            sender.sendMessage(ChatUtils.pmToSender(sender, target, msgToTarget))
                            Command.SINGLE_SUCCESS
                        }

                        chatDataConfig.addHiMessage(sender.uniqueId.toString(), target.uniqueId.toString())
                        sender.sendMessage(ChatUtils.pmToSender(sender, target, msgToTarget))
                        target.sendMessage(ChatUtils.pmToTarget(sender, target, msgToTarget))

                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .build()
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent){
        chatDataConfig.clearPlayerData(e.player.uniqueId.toString(), hi = true, ignore = false)
    }

    fun hiCommand(commandName: String = "hi"): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val target = ChatUtils.getPlayerFromArgument(ctx)

                    if (target == sender){
                        sender.sendMessage("* Ты не можешь передать привет самому себе *".red())
                        return@executes Command.SINGLE_SUCCESS
                    }

                    if(chatDataConfig.hasHiMessage(sender.uniqueId.toString(), target.uniqueId.toString())){
                        sender.sendMessage("* Ты уже передал ему привет *".gray())
                        Command.SINGLE_SUCCESS
                    }

                    ChatUtils.hiToTarget(sender, target)
                    ChatUtils.senderSaidHi(sender, target)
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }


    fun ignoreCommand(commandName: String = "ignore", chatDataConfig: ChatDataConfig): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val target = ChatUtils.getPlayerFromArgument(ctx)

                    if(chatDataConfig.isIgnoringPM(sender.uniqueId.toString(), target.uniqueId.toString())){
                        sender.sendMessage("* Ты уже игнорируешь игрока ${target.displayName().plainText()} *".gray())
                        return@executes Command.SINGLE_SUCCESS
                    }

                    chatDataConfig.addIgnorePM(sender.uniqueId.toString(), target.uniqueId.toString())
                    sender.sendMessage("* Теперь ты игнорируешь игрока ${target.displayName().plainText()} *".gray())
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

    fun unignoreCommand(commandName: String = "unignore", chatDataConfig: ChatDataConfig): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val target = ChatUtils.getPlayerFromArgument(ctx)

                    if(!chatDataConfig.isIgnoringPM(sender.uniqueId.toString(), target.uniqueId.toString())){
                        sender.sendMessage("* Ты и так принимаешь сообщения от игрока ${target.displayName().plainText()} *".gray())
                        return@executes Command.SINGLE_SUCCESS
                    }

                    chatDataConfig.removeIgnorePM(sender.uniqueId.toString(), target.uniqueId.toString())
                    sender.sendMessage("* Теперь ты принимаешь сообщения от игрока ${target.displayName().plainText()} *".gray())
                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }

}