package ua.senalll.immersionCore.tab

import com.mojang.brigadier.Command
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.entity.Player
import ua.senalll.immersionCore.database.TabTableHandler
import ua.senalll.immersionCore.pdc.TabPDCHandler

object TabCommand{
    fun createCommand(commandName: String, tabPDCHandler: TabPDCHandler): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.literal("tps")
                .then(Commands.literal("enable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_tps", true)
                        sender.sendMessage("TPS display enabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("disable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_tps", false)
                        sender.sendMessage("TPS display disabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("header")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "tps_header", true)
                        sender.sendMessage("TPS will be shown in header")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("footer")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "tps_header", false)
                        sender.sendMessage("TPS will be shown in footer")
                        Command.SINGLE_SUCCESS
                    })
            )
            .then(Commands.literal("online")
                .then(Commands.literal("enable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_online", true)
                        sender.sendMessage("Online players display enabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("disable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_online", false)
                        sender.sendMessage("Online players display disabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("header")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "online_header", true)
                        sender.sendMessage("Online players count will be shown in header")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("footer")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "online_header", false)
                        sender.sendMessage("Online players count will be shown in footer")
                        Command.SINGLE_SUCCESS
                    })
            )
            .then(Commands.literal("mspt")
                .then(Commands.literal("enable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_mspt", true)
                        sender.sendMessage("MSPT display enabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("disable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_mspt", false)
                        sender.sendMessage("MSPT display disabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("header")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "mspt_header", true)
                        sender.sendMessage("MSPT will be shown in header")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("footer")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "mspt_header", false)
                        sender.sendMessage("MSPT will be shown in footer")
                        Command.SINGLE_SUCCESS
                    })
            )
            .then(Commands.literal("restart")
                .then(Commands.literal("enable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_restart", true)
                        sender.sendMessage("Restart timer display enabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("disable")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "show_restart", false)
                        sender.sendMessage("Restart timer display disabled")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("header")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "restart_header", true)
                        sender.sendMessage("Restart timer will be shown in header")
                        Command.SINGLE_SUCCESS
                    })
                .then(Commands.literal("footer")
                    .executes { ctx ->
                        val sender = ctx.source.sender as Player
                        tabPDCHandler.updateTabSetting(sender, "restart_header", false)
                        sender.sendMessage("Restart timer will be shown in footer")
                        Command.SINGLE_SUCCESS
                    })
            )
            .build()
    }

}