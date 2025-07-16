package ua.senalll.immersionCore.restart

import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import ua.senalll.immersionCore.ImmersionCore
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.function.Consumer

object RestartHandler {
    private lateinit var task: ScheduledTask
    lateinit var restartTime: LocalDateTime

    fun handleRestart(instance: ImmersionCore){
        val now: LocalDateTime = LocalDateTime.now()
        restartTime = now.plusHours(instance.coreConfig.getRestartIntervalHours().toLong())
        val delaySeconds = now.until(restartTime, ChronoUnit.SECONDS)
        val delayTicks = delaySeconds * 20L

        val restartTask = RestartTask(instance)
        task = instance.server.globalRegionScheduler.runDelayed(instance, Consumer { _ ->
            restartTask.start()
        }, delayTicks)
    }

    fun stop(){
        task.cancel()
    }
}