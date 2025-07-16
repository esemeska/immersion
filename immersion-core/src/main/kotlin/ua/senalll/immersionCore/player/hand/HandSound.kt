package ua.senalll.immersionCore.player.hand

import org.bukkit.entity.Player

class HandSound {
    enum class Sound(val soundName: String) {
        HAND_CAST("hand.hi_five_cast"),
        HAND_FAIL("hand.hi_five_fail"),
        HAND_IMPACT("hand.hi_five_impact")
    }

    companion object {
        fun playHandSound(player: Player, sound: Sound) {
            val loc = player.location
            for (ent in loc.world.getNearbyEntities(loc, 20.0, 20.0, 20.0)) {
                if (ent is Player) {
                    val nearbyPlayer = ent
                    nearbyPlayer.playSound(player.location, sound.soundName, 0.8F, 1.0F)
                }
            }
        }
    }
}