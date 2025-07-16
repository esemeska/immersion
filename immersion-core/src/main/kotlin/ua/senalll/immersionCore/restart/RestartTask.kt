package ua.senalll.immersionCore.restart

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Server
import org.bukkit.plugin.Plugin

class RestartTask(
    private val plugin: Plugin,
    private val server: Server = plugin.server
) {
    private var timer: Int = 5

    fun start() {
        sendMsg("say ⌛ Рестартаю сервер через минуту!")

        server.globalRegionScheduler.runDelayed(plugin,{ _: ScheduledTask ->
            timer = 5
            server.globalRegionScheduler.runAtFixedRate(plugin, { task: ScheduledTask ->
                sendMsg("say Рестарт через $timer")
                timer--
                if (timer <= 0) {
                    try {
                        restartServer()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        sendMsg("say Блин, у меня чё-то не получилось, ну ладно, играйте дальше.")
                    } finally {
                        task.cancel()
                    }
                }
            }, 0L, 20L)
        }, 60L * 20)
    }

    fun restartServer() {
        sendMsg("save-all")
        sendMsg("say Я рестартаю!")
        server.shutdown()
    }

    fun sendMsg(msg: String) {
        server.globalRegionScheduler.execute(plugin) {
            Bukkit.dispatchCommand(server.consoleSender, msg)
        }
    }
}