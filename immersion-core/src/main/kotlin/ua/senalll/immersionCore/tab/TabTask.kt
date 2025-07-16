package ua.senalll.immersionCore.tab

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Server
import org.bukkit.plugin.Plugin

class TabTask(
    private val plugin: Plugin,
    private val tab: Tab,
    private val server: Server = plugin.server
) {
    private lateinit var scheduledTask: ScheduledTask

    fun startTabUpdateTask() {
        if(::scheduledTask.isInitialized) {
            stop()
        }

        scheduledTask = server.globalRegionScheduler.runAtFixedRate(plugin, {task: ScheduledTask ->
            val onlinePlayers = server.onlinePlayers

            for (player in onlinePlayers){
                tab.updatePlayerTab(player)
            }
        }, 1L, 20L)
    }

    fun stop(){
        scheduledTask.cancel()
    }
}