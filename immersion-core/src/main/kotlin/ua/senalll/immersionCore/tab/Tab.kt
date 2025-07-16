package ua.senalll.immersionCore.tab

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.config.yml.TabConfig
import ua.senalll.immersionCore.pdc.TabPDCHandler
import ua.senalll.immersionCore.restart.RestartHandler
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class Tab (
    private val tabConfig: TabConfig,
    private val tabPDCHandler: TabPDCHandler,
    private val plugin: JavaPlugin
) {

    fun updatePlayerTab(player: Player) {
        val settings = tabPDCHandler.getTabSettings(player)

        if (settings.isEmpty()) return

        val tab: Pair<Component, Component> = createTabFromSettings(settings)

        Bukkit.getScheduler().runTask(plugin, Runnable {
            player.sendPlayerListHeaderAndFooter(tab.first, tab.second)
        })
    }

    private fun createTabFromSettings(settings: Map<String, Any>): Pair<Component, Component> {
        val rawHeader = tabConfig.getHeader()
        val rawFooter = tabConfig.getFooter()

        val headerFields = mutableListOf<String>()
        val footerFields = mutableListOf<String>()

        if (settings["show_tps"] as? Boolean == true) {
            val tps = String.format("%.2f", tps())
            if (settings["tps_header"] as? Boolean == true) {
                headerFields.add(tps)
            } else {
                footerFields.add(tps)
            }
        }

        if (settings["show_online"] as? Boolean == true) {
            val online = online()
            if (settings["online_header"] as? Boolean == true) {
                headerFields.add(online)
            } else {
                footerFields.add(online)
            }
        }

        if (settings["show_mspt"] as? Boolean == true) {
            val mspt = "${String.format("%.2f", mspt())}ms"
            if (settings["mspt_header"] as? Boolean == true) {
                headerFields.add(mspt)
            } else {
                footerFields.add(mspt)
            }
        }

        if (settings["show_restart"] as? Boolean == true) {
            val restart = timeToRestart()
            if (settings["restart_header"] as? Boolean == true) {
                headerFields.add(restart)
            } else {
                footerFields.add(restart)
            }
        }

        val headerContent = if (headerFields.isNotEmpty()) " " + headerFields.joinToString(" | ") else ""
        val footerContent = if (footerFields.isNotEmpty()) " " + footerFields.joinToString(" | ") else ""

        return Pair(
            MiniMessage.miniMessage().deserialize(rawHeader + headerContent),
            MiniMessage.miniMessage().deserialize(rawFooter + footerContent)
        )
    }

    fun tps(): Double {
        val tps = plugin.server.tps[0] + 2
        return tps.coerceAtMost(22.0)
    }

    fun mspt(): Double {
        return plugin.server.averageTickTime
    }

    fun online(): String {
        val onlinePlayers = Bukkit.getOnlinePlayers()
        if (onlinePlayers.isEmpty()) return "🥲"
        var vanishedPlayers = 0
        for (player in onlinePlayers){
            if ((plugin as ImmersionCore).pdcRegistry.vanishPDC().isVanished(player)){
                vanishedPlayers++
            }
        }
        return "${onlinePlayers.size - vanishedPlayers}/${plugin.server.maxPlayers}"
    }

    fun timeToRestart(): String {
        val restartTime = RestartHandler.restartTime
        val now = LocalDateTime.now()
        if (now.isAfter(restartTime)) {
            return "0ч 0м"
        }

        val hours = now.until(restartTime, ChronoUnit.HOURS)
        val minutes = now.plusHours(hours).until(restartTime, ChronoUnit.MINUTES)

        return "${hours}ч ${minutes}м"
    }
}