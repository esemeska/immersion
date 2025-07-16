package ua.senalll.immersionCore.chat.format

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

data class FormattedMsg(
    val cleanMsg: Component,
    val formattedMsg: Component,
    val isMention: Boolean,
    val mentionedPlayers: List<Player>,
    val isLink: Boolean
)