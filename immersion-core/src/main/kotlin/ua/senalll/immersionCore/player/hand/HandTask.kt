package ua.senalll.immersionCore.player.hand

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.pdc.HandHighTaskIdPDCHandler
import ua.senalll.immersionCore.pdc.HandIDPDCHandler
import ua.senalll.immersionCore.pdc.HandMoveTaskIdPDCHandler
import ua.senalll.immersionCore.pdc.PlayerHasHandPDCHandler
import ua.senalll.immersionCore.player.hand.HandItemDisplay.handLoc
import java.util.UUID

object HandTask {
    const val HAND_LIFETIME_TICKS: Int = 20 * 6

    fun startHandMovementTask(itemDisplay: ItemDisplay, p: Player, targetLocation: Array<Location>): Int {
        return object : BukkitRunnable() {
            var tickCount = 0
            override fun run() {
                if (!itemDisplay.isValid || !p.isOnline) {
                    itemDisplay.remove()
                    this.cancel()
                    return
                }
                tickCount++
                if (tickCount % 5 == 0) {
                    targetLocation[0] = p.location.handLoc()
                }
                val current = itemDisplay.location
                val target = targetLocation[0]

                val lerpFactor = 0.2
                val x = current.x + (target.x - current.x) * lerpFactor
                val y = current.y + (target.y - current.y) * lerpFactor
                val z = current.z + (target.z - current.z) * lerpFactor

                val yaw = current.yaw + (target.yaw - current.yaw) * lerpFactor.toFloat()
                val pitch = current.pitch + (target.pitch - current.pitch) * lerpFactor.toFloat()

                val interpolated = Location(current.world, x, y, z, yaw, pitch)
                itemDisplay.teleport(interpolated)
            }
        }.runTaskTimer(ImmersionCore.instance, 0L, 1L).taskId
    }

    fun startHighFiveDetectionTask(itemDisplay: ItemDisplay, p: Player, handUUID: UUID): Int {
        return object : BukkitRunnable() {
            override fun run() {
                if (PlayerHasHandPDCHandler.playerHasHand(p)) {
                    val nearbyHands = HandItemDisplay.getNearbyHands(itemDisplay)
                    val nearbyHand = nearbyHands.stream()
                        .filter { h -> HandIDPDCHandler.getHandUUID(h) != handUUID }
                        .findFirst().orElse(null)
                    if (nearbyHand != null) {
                        highFive(itemDisplay, nearbyHand)
                        this.cancel()
                    }
                }
            }
        }.runTaskTimer(ImmersionCore.instance, 0, 3).taskId
    }

    fun scheduleHandDestruction(itemDisplay: ItemDisplay, p: Player) {
        object : BukkitRunnable() {
            override fun run() {
                if (itemDisplay.isValid && p.isOnline) {
                    destroyPlayerHand(p)
                }
            }
        }.runTaskLater(ImmersionCore.instance, HAND_LIFETIME_TICKS.toLong())
    }

    fun highFive(hand1: ItemDisplay, hand2: ItemDisplay) {
        val player1 = Hand.getPlayerFromHand(hand1)
        val player2 = Hand.getPlayerFromHand(hand2)

        if (player1 != null && player2 != null) {
            cancelHandTasks(hand1)
            cancelHandTasks(hand2)

            FiveAnimation.startHandImpactAnimation(hand1, hand2.location, player1)
            FiveAnimation.startHandImpactAnimation(hand2, hand1.location, player2)
        } else {
            println("One from players is null!")
        }
    }

    fun destroyPlayerHand(p: Player) {
        val hand = Hand.getHandFromPlayer(p)
        if (hand != null) {
            cancelHandTasks(hand)

            FiveAnimation.startHandFailAnimation(hand, p)
        }
    }

    fun cancelHandTasks(itemDisplay: ItemDisplay) {
        Bukkit.getScheduler().cancelTask(HandMoveTaskIdPDCHandler.getTaskId(itemDisplay))
        Bukkit.getScheduler().cancelTask(HandHighTaskIdPDCHandler.getTaskId(itemDisplay))
    }
}