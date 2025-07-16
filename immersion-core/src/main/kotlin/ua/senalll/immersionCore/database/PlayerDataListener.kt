package ua.senalll.immersionCore.database

import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.jetbrains.annotations.Async
import ua.senalll.immersionCore.CoreUtils
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.nickname.profile.ProfileGuiListener
import ua.senalll.immersionCore.pdc.TabPDCHandler
import ua.senalll.immersionapi.database.table.AsyncTableHandler
import ua.senalll.immersionapi.util.Utils
import java.util.logging.Level
import java.util.logging.Logger

class PlayerDataListener(
    private val playerTabTableHandler: AsyncTableHandler,
    private val tabPDCHandler: TabPDCHandler
): Listener {
    private val logger: Logger = Utils.createLogger<PlayerDataListener>()


    @EventHandler
    fun onAsyncPlayerLogin(e: AsyncPlayerPreLoginEvent){
        ProfileGuiListener.onAsyncPreLogin(e)
    }

    @EventHandler
    fun onPlayerJoin(e: AsyncPlayerPreLoginEvent){
        val profile = e.playerProfile
        profile.id?.let { uuid ->
            TabTableHandler.isPlayerExist(uuid).thenAccept { result ->
                if (result) return@thenAccept
                val values = mapOf(
                    TableField.Companion.UUID to profile.id.toString()
                )

                playerTabTableHandler.save(values).exceptionally { ex ->
                    logger.log(Level.SEVERE, "${ex.message}")
                    null
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player = e.player
        val uuid = player.uniqueId

        TabTableHandler.getPlayerFields(uuid).thenAccept { map ->
            if (map == null) return@thenAccept
            val tabSettings = mutableMapOf<String, Any>()

            for (field in map.keys) {
                if (field == TableField.Companion.UUID.name) continue

                map[field]?.let { value ->
                    when (field) {
                        TabTableHandler.SHOW_TPS_FIELD.name -> tabSettings["show_tps"] = CoreUtils.byteToBool(value)
                        TabTableHandler.TPS_HEADER_FIELD.name -> tabSettings["tps_header"] = CoreUtils.byteToBool(value)
                        TabTableHandler.SHOW_ONLINE_FIELD.name -> tabSettings["show_online"] = CoreUtils.byteToBool(value)
                        TabTableHandler.ONLINE_HEADER_FIELD.name -> tabSettings["online_header"] = CoreUtils.byteToBool(value)
                        TabTableHandler.SHOW_MSPT_FIELD.name -> tabSettings["show_mspt"] = CoreUtils.byteToBool(value)
                        TabTableHandler.MSPT_HEADER_FIELD.name -> tabSettings["mspt_header"] = CoreUtils.byteToBool(value)
                        TabTableHandler.SHOW_RESTART_FIELD.name -> tabSettings["show_restart"] = CoreUtils.byteToBool(value)
                        TabTableHandler.RESTART_HEADER_FIELD.name -> tabSettings["restart_header"] = CoreUtils.byteToBool(value)
                    }
                }
            }

            Bukkit.getScheduler().runTask(ImmersionCore.instance, Runnable {
                val player = Bukkit.getPlayer(uuid)
                if (player != null && player.isOnline) {
                    tabPDCHandler.saveTabSettings(player, tabSettings)
                    logger.info("Tab settings loaded from database to PDC for player ${player.name}")
                }
            })
        }
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        val player = e.player
        val uuid = player.uniqueId

        val tabSettings = tabPDCHandler.getTabSettings(player)

        if (tabSettings.isEmpty()) return

        val dbValues = mutableMapOf<TableField, Any>(
            TableField.Companion.UUID to uuid.toString()
        )

        tabSettings.forEach { (key, value) ->
            when (key) {
                "show_tps" -> dbValues[TabTableHandler.SHOW_TPS_FIELD] = value
                "tps_header" -> dbValues[TabTableHandler.TPS_HEADER_FIELD] = value
                "show_online" -> dbValues[TabTableHandler.SHOW_ONLINE_FIELD] = value
                "online_header" -> dbValues[TabTableHandler.ONLINE_HEADER_FIELD] = value
                "show_mspt" -> dbValues[TabTableHandler.SHOW_MSPT_FIELD] = value
                "mspt_header" -> dbValues[TabTableHandler.MSPT_HEADER_FIELD] = value
                "show_restart" -> dbValues[TabTableHandler.SHOW_RESTART_FIELD] = value
                "restart_header" -> dbValues[TabTableHandler.RESTART_HEADER_FIELD] = value
            }
        }

        TabTableHandler.playerTabTableHandler.save(dbValues).exceptionally { ex ->
            ImmersionCore.instance.logger.log(Level.SEVERE, "Failed to save tab settings for player ${player.name}: ${ex.message}")
            null
        }.thenAccept { _ ->
            ImmersionCore.instance.logger.info("Tab settings saved from PDC to database for player ${player.name}")
        }
    }

}