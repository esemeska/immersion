package ua.senalll.immersionCore.vanish

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig

object VanishCommand  {
    fun createCommand(commandName: String, vanishConfig: VanishConfig): LiteralCommandNode<CommandSourceStack>{
        return Commands.literal(commandName)
            .executes { ctx ->
                val sender = ctx.source.sender as Player
                if(vanishConfig.isPlayerVanished(sender)) {
                    VanishPlayerChange.deVanishPlayer(sender)
                }else{
                    VanishPlayerChange.vanishPlayer(sender)
                }
                Command.SINGLE_SUCCESS
            }
            .build()
    }
}