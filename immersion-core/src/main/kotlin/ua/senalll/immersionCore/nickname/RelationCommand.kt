package ua.senalll.immersionCore.nickname

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ua.senalll.immersionCore.database.RelationTableHandler
import ua.senalll.immersionCore.pdc.RelationPDCHandler
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.red
import ua.senalll.immersionapi.component.white
import java.util.UUID

object RelationCommand {

    fun createCommand(commandName: String, relationPDCHandler: RelationPDCHandler): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.literal("accept")
                .then(Commands.argument("uuid", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        val uuidStr = StringArgumentType.getString(ctx, "uuid")

                        val initiatorUUID = try {
                            UUID.fromString(uuidStr)
                        } catch (ex: IllegalArgumentException) {
                            sender.sendMessage("Некорректный UUID!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val initiator = Bukkit.getPlayer(initiatorUUID)
                        if (initiator == null) {
                            sender.sendMessage("Игрок не в сети!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val pending = relationPDCHandler.getMakingFriendList(initiator)
                        if (!pending.contains(sender.uniqueId)) {
                            sender.sendMessage("Нет активного приглашения от этого игрока!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        RelationTableHandler.asyncAddRelation(initiatorUUID, sender.uniqueId)
                            .thenRun {
                                initiator.sendMessage("* Вы подружились с ".white().append(sender.displayName()))
                                sender.sendMessage("* Вы подружились с ".white().append(initiator.displayName()))
                            }

                        relationPDCHandler.removeMakingFriend(sender, initiator)
                        relationPDCHandler.removeMakingFriend(initiator, sender)

                        NameUpdater.updateNameForBoth(sender, initiator)

                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .then(Commands.literal("deny")
                .then(Commands.argument("uuid", StringArgumentType.string())
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        val uuidStr = StringArgumentType.getString(ctx, "uuid")

                        val initiatorUUID = try {
                            UUID.fromString(uuidStr)
                        } catch (ex: IllegalArgumentException) {
                            sender.sendMessage("Некорректный UUID!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val initiator = Bukkit.getPlayer(initiatorUUID)
                        if (initiator == null) {
                            sender.sendMessage("Игрок не в сети!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        val pending = relationPDCHandler.getMakingFriendList(initiator)
                        if (!pending.contains(sender.uniqueId)) {
                            sender.sendMessage("Нет активного приглашения от этого игрока!".red())
                            return@executes Command.SINGLE_SUCCESS
                        }

                        relationPDCHandler.removeMakingFriend(sender, initiator)
                        relationPDCHandler.removeMakingFriend(initiator, sender)
                        sender.sendMessage("Вы отклонили приглашение от ".gray().append(initiator.displayName()))
                        initiator.sendMessage("Ваше приглашение было отклонено игроком ".gray().append(sender.displayName()))

                        Command.SINGLE_SUCCESS
                    }
                )
            )
            .build()
    }

}