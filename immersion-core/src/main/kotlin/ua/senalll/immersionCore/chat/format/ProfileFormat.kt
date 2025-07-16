package ua.senalll.immersionCore.chat.format

import net.kyori.adventure.text.Component
import org.bukkit.Statistic
import org.bukkit.entity.Player
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.white

object ProfileFormat {

    fun playerProfile(player: Player): Component{
        return player.displayName().color(white).hoverEvent(
            " Профиль игрока ".white()
                .appendNewline()
                .append(" На сервере: ".white().append(((player.getStatistic(Statistic.TOTAL_WORLD_TIME) / 20 / 3600).toString() + "ч").gray()))
                .append(" ".white())
                .appendNewline()
                .append(" Смертей: ".white().append(player.getStatistic(Statistic.DEATHS).toString().gray()))
                .append(" ".white())
        )
    }
}