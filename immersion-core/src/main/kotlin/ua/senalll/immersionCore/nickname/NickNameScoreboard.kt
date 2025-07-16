package ua.senalll.immersionCore.nickname

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.ScoreboardManager
import org.bukkit.scoreboard.Team
import ua.senalll.immersionapi.component.plainText

class NickNameScoreboard(
    private val plugin: JavaPlugin,
    private val sbManager: ScoreboardManager = plugin.server.scoreboardManager
) : Listener {

    private val TEAM_NAME = "NoNickName"
    private lateinit var team: Team

    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent){
        addPlayerToTeam(e.player)
    }

    fun addPlayerToTeam(player: Player){
        val temp = sbManager.mainScoreboard.getTeam(TEAM_NAME)
        temp?.let { team = it } ?: initTeam()
        team.addEntry(player.displayName().plainText())
    }

    fun initTeam(){
        sbManager.mainScoreboard.registerNewTeam(TEAM_NAME)
        team = sbManager.mainScoreboard.getTeam(TEAM_NAME)!!
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER)
        team.setCanSeeFriendlyInvisibles(false)
    }


}