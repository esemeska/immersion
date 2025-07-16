package ua.senalll.immersionCore.chat.format

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.modrinth
import ua.senalll.immersionapi.component.twitch
import ua.senalll.immersionapi.component.white
import ua.senalll.immersionapi.component.youtube
import ua.senalll.immersionapi.util.Utils
import java.util.regex.Pattern

object FormatManager {
    private val domains = mutableMapOf(
        "youtube.com" to youtube,
        "twitch.tv" to twitch,
        "modrinth.com" to modrinth
    )

    suspend fun format(msg: String): FormattedMsg {
        val pattern = Pattern.compile("(\\s+|\\S+)")
        val matcher = pattern.matcher(msg)

        var cleanWords = Component.empty()
        var formattedWords = Component.empty()

        val mentionedPlayers = ArrayList<Player>()
        val mentionedDiscordPlayers = ArrayList<OfflinePlayer>()

        var isLink = false

        while (matcher.find()) {
            val token = matcher.group()

            val isSpace = token.trim().isEmpty()
            cleanWords = cleanWords.append(token.white())

            if (isSpace) {
                formattedWords = formattedWords.append(token.white())
            } else {
                if (!isLink && Utils.isURL(token)) {
                    isLink = true
                }
                formattedWords = formattedWords.append(formatWord(token, mentionedPlayers, mentionedDiscordPlayers))
            }
        }

        val isMention = mentionedPlayers.isNotEmpty() || mentionedDiscordPlayers.isNotEmpty()
        return FormattedMsg(cleanWords, formattedWords, isMention, mentionedPlayers, isLink)
    }

    private suspend fun formatWord(word: String, mentionedPlayers: MutableList<Player>, mentionedDiscordPlayers: MutableList<OfflinePlayer>): Component {
        return when {
            isMention(word) -> formatMention(word, mentionedPlayers, mentionedDiscordPlayers)
            Utils.isURL(word) -> formatLink(word)
            else -> formatColor(word)
        }
    }

    private fun isMention(word: String): Boolean {
        return word.startsWith("@") && word.length >= 4
    }

    private suspend fun formatMention(
        word: String,
        mentionedPlayers: MutableList<Player>,
        mentionedDiscordPlayers: MutableList<OfflinePlayer>
    ): Component {
        val nickname = word.substring(1)

        val offlinePlayer = Utils.getOfflinePlayer(nickname)
        when {
            offlinePlayer.isOnline -> mentionedPlayers.add(offlinePlayer as Player)
            offlinePlayer.hasPlayedBefore() -> mentionedDiscordPlayers.add(offlinePlayer)
        }

        return word.white()
    }

    private fun formatLink(word: String): Component {
        try {
            for ((key, value) in domains) {
                if (word.contains(key)) {
                    return ("₪ $key").white().style(Style.style(ClickEvent.openUrl(word)))
                        .color(value)
                        .hoverEvent("Нажми, чтобы перейти по ссылке".white())
                }
            }

            val formattedFirstUrlPart = word.replaceFirst("https?://(www\\.)?".toRegex(), "")
            val slashIndex = formattedFirstUrlPart.indexOf("/")

            return if (slashIndex >= 0) {
                ("₪" + formattedFirstUrlPart.substring(slashIndex) + " ").white()
                    .style(Style.style(ClickEvent.openUrl(word))).color(gray)
            } else {
                ("₪ $formattedFirstUrlPart ").white()
                    .style(Style.style(ClickEvent.openUrl(word))).color(gray)
            }
        } catch (e: Exception) {
            return word.white()
        }
    }

    private fun formatColor(word: String): Component {
        val serializer = MiniMessage.builder()
            .tags(
                TagResolver.builder()
                .resolver(StandardTags.gradient())
                .build()
            )
            .build()
        return serializer.deserialize(word)
    }
}