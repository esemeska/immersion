package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.scoreboard.Team
import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionapi.component.plainText

object Test : Listener  {
    lateinit var team: Team
    lateinit var sbManager: ScoreboardManager

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent){
        addPlayerToTeam(e.player)
    }

    fun test(){
        initTeam()
        val world = Bukkit.getWorlds().first()
        val location = Location(world, .0, 150.0, 0.0)
        val blockDisplay: BlockDisplay = location.world.spawn(location.clone().subtract(.5, .5, .5), BlockDisplay::class.java) {
            it.billboard = Display.Billboard.FIXED
            it.brightness = Display.Brightness(1, 1)
            it.isVisibleByDefault = true
            it.block = Bukkit.createBlockData(Material.BAMBOO_BLOCK)
            it.isInvisible = true
        }
        val invisibility = PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 1, false, false, false)
        //(blockDisplay as LivingEntity).addPotionEffect(invisibility)
        team.addEntities(blockDisplay)
    }

    fun addPlayerToTeam(player: Player){
        val temp = sbManager.mainScoreboard.getTeam("display_test")
        temp?.let { team = it } ?: initTeam()
        team.addEntry(player.displayName().plainText())
    }

    fun initTeam() {
        sbManager = ImmersionLitematica.instance.server.scoreboardManager
        val scoreboard = sbManager.mainScoreboard

        team = scoreboard.getTeam("display_test")
            ?: scoreboard.registerNewTeam("display_test").apply {
                setCanSeeFriendlyInvisibles(true)
            }
    }
}