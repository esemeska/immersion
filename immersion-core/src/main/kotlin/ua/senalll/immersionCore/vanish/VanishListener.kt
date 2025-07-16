package ua.senalll.immersionCore.vanish

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.config.json.VanishConfig
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import ua.senalll.immersionapi.component.red

class VanishListener(private val vanishConfig: VanishConfig) : Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(e: PlayerJoinEvent){
        val player = e.player
        if(!vanishConfig.isPlayerVanished(player)) {
            VanishPlayerChange.updateVanishForPlayers()
        }else {
            e.joinMessage(Component.empty())
            player.sendMessage("* ты в ванише *".gray().italic())

            object: BukkitRunnable(){
                override fun run() {
                    VanishPlayerChange.vanishPlayer(player)
                }
            }.runTask(ImmersionCore.instance)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onPlayerQuit(e: PlayerQuitEvent){
        val player = e.player
        if(!vanishConfig.isPlayerVanished(player)) return
        e.quitMessage(Component.empty())
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onChat(e: AsyncChatEvent){
        val player = e.player
        if (!vanishConfig.isPlayerVanished(player)) return
        e.isCancelled = true
        player.sendMessage("* чат в ванише не работает! *".red())
    }
}