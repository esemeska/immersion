package ua.senalll.immersionCore

import org.bukkit.GameMode
import org.bukkit.event.player.PlayerMoveEvent
import kotlin.math.abs

object CoreUtils {

    fun byteToBool(value: Any?): Boolean {
        return value.toString() == "1"
    }

    fun isPlayerMoving(e: PlayerMoveEvent): Boolean {
        val player = e.player
        if (player.isDead || player.gameMode == GameMode.CREATIVE) return false

        val from = e.from
        val to = e.to

        val deltaPosition = from.distance(to)
        val deltaAngle = abs(from.yaw - to.yaw) + abs(from.pitch - to.pitch)

        return deltaAngle < 10f && deltaPosition > 0.0625 // 1/16 block movement threshold
    }
}