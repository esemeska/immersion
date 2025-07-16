package ua.senalll.immersionCore.player.heads

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import java.net.MalformedURLException
import java.net.URISyntaxException
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

object PlayerHeadOnDeath : Listener {

    private val random: Random = Random()
    private const val DROP_CHANCE_PERCENT: Int = 30

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.player

        val value = random.nextInt(100)
        if (value >= DROP_CHANCE_PERCENT) return

        try {
            val causeEvent = player.lastDamageCause
            val head = PlayerHeads.getHead(player, causeEvent)
            player.world.dropItem(player.location, head)
        } catch (e: MalformedURLException) {
            Logger.getLogger(this.javaClass.getName())
                .log(Level.SEVERE, "Ошибка при создании головы игрока: " + player.name, e)
        } catch (e: URISyntaxException) {
            Logger.getLogger(this.javaClass.getName())
                .log(Level.SEVERE, "Ошибка при создании головы игрока: " + player.name, e)
        }
    }
}