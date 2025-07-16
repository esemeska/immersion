package ua.senalll.immersionDiscord.discord

import kotlinx.coroutines.reactive.awaitSingle
import net.kyori.adventure.text.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import reactor.core.publisher.Mono
import ua.senalll.immersionDiscord.database.MemberTableHandler
import ua.senalll.immersionDiscord.link.CodeHandler
import ua.senalll.immersionapi.component.green
import ua.senalll.immersionapi.component.lightPurple
import ua.senalll.immersionapi.component.white
import ua.senalll.immersionapi.component.yellow
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class MinecraftLinkListener(private val bot: DiscordBot) : Listener {

    @EventHandler
    suspend fun onAsyncPreLogin(e: AsyncPlayerPreLoginEvent){
        val uuid = e.uniqueId
        val playerName = e.name
        val isExist = MemberTableHandler.isMemberExist(uuid)

        if(isExist){
            e.allow()
            val discordId = MemberTableHandler.getMemberDiscordID(uuid)
            bot.changeMemberNickName(discordId, playerName)
            if (CodeHandler.isPlayerHasCode(uuid)) {
                CodeHandler.deleteCode(uuid)
            }
        }else{
            val code =
                if (CodeHandler.isPlayerHasCode(uuid)){
                    CodeHandler.getCode(uuid) as String
                }else{
                    hashUUIDToCode(uuid).also { CodeHandler.saveCode(uuid, it) }
                }
            val message = disallowMsg(code).awaitSingle()
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, message)
        }
    }

    private fun disallowMsg(code: String): Mono<Component> {
        return bot.inviteLink().map { link ->
             "Приветствуем на сервере!".lightPurple()
                .appendNewline()
                .appendNewline()
                .append("Чтобы играть на сервере, нужно привязать дискорд аккаунт.".white())
                .appendNewline()
                .append(" Чтобы это сделать, отправь ".white())
                .append(code.green())
                .append(" нашему боту".white())
                .append((" (" + bot.botName + ")").lightPurple())
                .append(" в личные сообщения.".white())
                .appendNewline()
                .appendNewline()
                .append("Ссылку на дискорд сервер можно найти на нашем сайте: ".white())
                .append("22kuromi.notion.site".yellow())
        }
    }

    private fun hashUUIDToCode(playerUUID: UUID): String {
        val uuid = playerUUID.toString()
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(uuid.toByteArray(StandardCharsets.UTF_8))

            buildString {
                for (i in 0 until 5) {
                    val value = hashBytes[i].toInt() and 0xFF
                    val hexString = Integer.toHexString(value).let {
                        if (it.length < 2) "0$it" else it
                    }

                    val n = ThreadLocalRandom.current().nextInt(0, 3)
                    if (n < 1) {
                        append(hexString)
                    } else {
                        val randomChar = ('A' + ThreadLocalRandom.current().nextInt(26))
                        append(randomChar)
                    }
                }
            }
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            null
        } ?: ""
    }
}