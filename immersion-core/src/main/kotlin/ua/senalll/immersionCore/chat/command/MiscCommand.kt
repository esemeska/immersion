package ua.senalll.immersionCore.chat.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.argument.ArgumentTypes
import net.kyori.adventure.text.format.TextColor
import org.bukkit.entity.Player
import ua.senalll.immersionCore.chat.ChatUtils
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.green
import ua.senalll.immersionapi.component.lightPurple
import ua.senalll.immersionapi.component.red
import ua.senalll.immersionapi.component.white
import kotlin.math.roundToInt

class MiscCommand {
    fun hatCommand(commandName: String = "hat"): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .executes { ctx ->
                val sender = ctx.source.sender as Player
                val inv = sender.inventory
                val held = inv.itemInMainHand
                val helm = inv.helmet

                inv.helmet = held
                inv.setItemInMainHand(helm)
                sender.updateInventory()
                Command.SINGLE_SUCCESS
            }
            .build()
    }

    fun coordinateCommand(commandName: String = "корды"): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("player", ArgumentTypes.player())
                .executes { ctx ->
                    val sender = ctx.source.sender as Player
                    val target = ChatUtils.getPlayerFromArgument(ctx)

                    val world = sender.world.name

                    val worldColor: TextColor = when (world) {
                        "world_nether" -> red
                        "world_the_end" -> lightPurple
                        else -> green
                    }

                    val msg = ("[${sender.location.x.roundToInt()}," +
                                " ${sender.location.y.roundToInt()}," +
                                " ${sender.location.z.roundToInt()}]")
                            .white().color(worldColor)

                    if (sender == target){
                        sender.sendMessage(msg)
                        Command.SINGLE_SUCCESS
                    }

                    target.sendMessage(
                        sender.displayName().color(white)
                        .append(" прислал вам свои координаты: ".gray())
                        .append(msg)
                    )

                    sender.sendMessage(
                        "* Вы прислали свои координаты ".gray()
                            .append(target.displayName().color(white))
                            .append(" *".gray())
                    )

                    Command.SINGLE_SUCCESS
                }
            )
            .build()
    }
}