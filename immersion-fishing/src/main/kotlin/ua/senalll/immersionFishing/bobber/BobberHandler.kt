package ua.senalll.immersionFishing.bobber

import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent
import java.util.UUID

object BobberHandler {
    private val activeBobbers = mutableMapOf<UUID, Bobber>()
    private val bobbersByHook = mutableMapOf<FishHook, Bobber>()

    fun createBobber(e: PlayerFishEvent): Bobber {
        val player = e.player
        val hook = e.hook

        val bobber = Bobber(player, hook)
        activeBobbers[bobber.uuid] = bobber
        bobbersByHook[hook] = bobber
        bobber.startBubbling()

        return bobber
    }


    fun removeBobberByHook(hook: FishHook) {
        val bobber = bobbersByHook[hook]
        if (bobber != null) {
            bobber.resetFishing()
            activeBobbers.remove(bobber.uuid)
            bobbersByHook.remove(hook)
        }
    }

    fun getBobberByHook(hook: FishHook): Bobber? {
        return bobbersByHook[hook]
    }

    fun handleRightClick(player: Player, hook: FishHook) {
        val bobber = getBobberByHook(hook)
        if (bobber != null && bobber.player == player) {
            if (bobber.canCatch) {
                bobber.handleCatch(player, hook)
            } else {
                bobber.resetFishing()
            }
        }
    }

    fun cleanupInvalidBobbers() {
        val iterator = bobbersByHook.iterator()
        while (iterator.hasNext()) {
            val entry = iterator.next()
            val hook = entry.key
            if (hook.isDead || !hook.isValid) {
                val bobber = entry.value
                bobber.resetFishing()
                activeBobbers.remove(bobber.uuid)
                iterator.remove()
            }
        }
    }
}