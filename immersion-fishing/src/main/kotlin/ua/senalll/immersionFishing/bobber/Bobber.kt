package ua.senalll.immersionFishing.bobber

import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import org.bukkit.util.Vector
import ua.senalll.immersionFishing.ImmersionFishing
import ua.senalll.immersionFishing.listener.PlayerFishListener
import ua.senalll.immersionFishing.pdc.FishHookPDCHandler
import java.util.UUID
import kotlin.random.Random

class Bobber(val player: Player, val hook: FishHook) {
    val uuid = UUID.randomUUID()
    val timeToCatchOpportunity = Random.nextInt(3, 11)
    val timeToCatch = Random.nextInt(15,20)
    var isCaught = false
    var canCatch = false
    var allTasks = mutableListOf<BukkitTask>()

    private val plugin = ImmersionFishing.instance

    init {
        FishHookPDCHandler.setUUIDToHook(hook, player.uniqueId)
    }

    fun startBubbling() {
        val bubbleTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val location = hook.location
            location.world.spawnParticle(Particle.FALLING_WATER, location, 5, 0.1, 0.1, 0.1, 0.0)

            val vector = Vector(0.0, Random.nextDouble(-0.08, 0.06), 0.0)
            hook.velocity = vector
        }, 0L, 5L)
        allTasks.add(bubbleTask)

        val bubbleTaskCancel = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            bubbleTask.cancel()
            startCatchOpportunity()
        }, timeToCatchOpportunity.toLong() * 20L)
        allTasks.add(bubbleTaskCancel)
    }

    fun startCatchOpportunity() {
        canCatch = true

        val catchTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val location = hook.location
            location.world.spawnParticle(Particle.BUBBLE_POP, location, 5, 0.3, -.05, 0.3, 0.0)
            location.world.spawnParticle(Particle.BUBBLE_COLUMN_UP, location, 5, 0.0, -1.0, 0.0, 0.0)
        }, 0L, 5L)
        allTasks.add(catchTask)

        val catchCancelTask = Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            if (!isCaught) {
                canCatch = false
                catchTask.cancel()
                resetFishing()
            }
        }, timeToCatch.toLong())
        allTasks.add(catchCancelTask)
    }

    fun resetFishing() {
        canCatch = false

        for (task in allTasks){
            task.cancel()
        }
        allTasks.clear()
    }

    fun handleCatch(player: Player, hook: FishHook) {
        isCaught = true
        resetFishing()
        PlayerFishListener.handleCatch(player, hook)
    }
}