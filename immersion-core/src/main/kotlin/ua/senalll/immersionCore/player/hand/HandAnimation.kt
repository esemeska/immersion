package ua.senalll.immersionCore.player.hand

import org.bukkit.Location
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.joml.Quaternionf
import ua.senalll.immersionCore.ImmersionCore

object FiveAnimation {
    const val SCALE_START = 0.8f
    const val SCALE_END = 1.5f
    const val DURATION_MS = 700L

    fun startHandImpactAnimation(itemDisplay: ItemDisplay, loc: Location, p: Player) {
        val itemLoc = itemDisplay.location
        val lastLocation = getMidpoint(itemLoc, loc)

        object : BukkitRunnable() {
            private val startTime = System.currentTimeMillis()

            override fun run() {
                if (!itemDisplay.isValid) {
                    this.cancel()
                    return
                }
                val elapsedTime = System.currentTimeMillis() - startTime
                val progress = minOf(elapsedTime / DURATION_MS.toFloat(), 1.0f)

                itemDisplay.teleport(getPercentageFromStart(itemDisplay.location, lastLocation, progress.toDouble()))

                if (progress >= 0.7f) {
                    setScale(itemDisplay, getScale(progress))
                }

                if (progress >= 1.0f) {
                    Hand.finalizeHandImpact(itemDisplay, p)
                    this.cancel()
                }
            }
        }.runTaskTimer(ImmersionCore.instance, 0L, 1L)
    }

    fun startHandFailAnimation(itemDisplay: ItemDisplay, p: Player) {
        object : BukkitRunnable() {
            private val startTime = System.currentTimeMillis()

            override fun run() {
                if (!itemDisplay.isValid) {
                    this.cancel()
                    return
                }
                val elapsedTime = System.currentTimeMillis() - startTime
                val progress = minOf(elapsedTime / DURATION_MS.toFloat(), 1.0f)

                val rotationAngle = progress * 360.0f
                val rotation = Quaternionf().rotateY(Math.toRadians(rotationAngle.toDouble()).toFloat())

                val transform = itemDisplay.transformation
                transform.rightRotation.set(rotation)
                transform.scale.set(getScale(progress))
                itemDisplay.transformation = transform

                if (progress >= 1.0f) {
                    Hand.finalizeHandFail(itemDisplay, p)
                    this.cancel()
                }
            }
        }.runTaskTimer(ImmersionCore.instance, 0L, 1L)
    }

    fun getMidpoint(loc1: Location, loc2: Location): Location {
        val world = loc1.world
        val x = (loc1.x + loc2.x) / 2
        val y = (loc1.y + loc2.y) / 2
        val z = (loc1.z + loc2.z) / 2
        return Location(world, x, y, z)
    }

    fun getPercentageFromStart(start: Location, end: Location, percentage: Double): Location {
        val world = start.world
        val x = start.x + percentage * (end.x - start.x)
        val y = start.y + percentage * (end.y - start.y)
        val z = start.z + percentage * (end.z - start.z)
        return Location(world, x, y, z)
    }

    private fun getScale(progress: Float): Float {
        return if (progress < 0.5f)
            SCALE_START + (SCALE_END - SCALE_START) * (progress * 2)
        else
            SCALE_END - (SCALE_END - SCALE_START) * ((progress - 0.5f) * 2)
    }

    private fun setScale(itemDisplay: ItemDisplay, scale: Float) {
        val transform = itemDisplay.transformation
        transform.rightRotation.set(Quaternionf())
        transform.scale.set(scale)
        itemDisplay.transformation = transform
    }
}