package ua.senalll.immersionDiscord.discord.listener

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Member
import discord4j.core.`object`.entity.channel.PrivateChannel
import kotlinx.coroutines.reactive.awaitFirstOrNull
import reactor.core.publisher.Mono
import ua.senalll.immersionDiscord.database.MemberTableHandler
import ua.senalll.immersionDiscord.discord.DiscordBot
import ua.senalll.immersionDiscord.discord.DiscordRole
import ua.senalll.immersionDiscord.link.CodeHandler
import ua.senalll.immersionapi.util.Utils
import kotlinx.coroutines.reactor.mono
import org.bukkit.Bukkit
import ua.senalll.immersionDiscord.plugin.FileManager
import ua.senalll.immersionDiscord.plugin.LitematicaHook
import ua.senalll.immersionLitematica.discord.RawLitematicaData
import ua.senalll.immersionLitematica.discord.RawLitematicaHandler

class DiscordLinkingListener(private val bot: DiscordBot) {
    private val logger = Utils.createLogger<DiscordLinkingListener>()

    fun registerEvents(){
        handleChatInputInteraction()
        handleMessageReceive()
    }

    fun handleChatInputInteraction() {
        bot.gatewayClient.on(ChatInputInteractionEvent::class.java)
            .flatMap { event ->

                val member = event.interaction.member.orElse(null)
                val code = event.options.firstOrNull { it.name == "код" }?.value?.get()?.asString()

                if (member == null) {
                    return@flatMap event.reply()
                        .withContent("❌ Ошибка: не удалось получить участника!")
                        .withEphemeral(true)
                        .then()
                }

                if (code == null) {
                    return@flatMap event.reply()
                        .withContent("❌ Код не указан!")
                        .withEphemeral(true)
                        .then()
                }

                mono {
                    MemberTableHandler.isMemberLinked(member.id.asString())
                }.flatMap { isLinked ->
                    val result = when {
                        isLinked -> DiscordLinkResult.LINKED
                        CodeHandler.checkCode(code) -> DiscordLinkResult.SUCCESS
                        else -> DiscordLinkResult.FAILED
                    }
                    handleDiscordLinkFromSlash(event, result, member, code)
                }
            }
            .subscribe()
    }


    fun handleDiscordLinkFromSlash(e: ChatInputInteractionEvent, linkResult: DiscordLinkResult, member: Member,  code: String): Mono<Void>{
        return when (linkResult) {
                DiscordLinkResult.SUCCESS -> {
                    val uuid = CodeHandler.getUUIDByCode(code)
                    if (uuid == null) {
                        return e.reply()
                            .withContent("Произошла ошибка: код не найден или устарел!")
                            .withEphemeral(true)
                    }

                    mono {
                        val p = Utils.getOfflinePlayer(uuid)
                        CodeHandler.deleteCode(uuid)
                        val id = member.id

                        MemberTableHandler.saveMemberData(id.asString(), p.uniqueId)

                        bot.changeMemberNickName(member, p.name.toString())
                        bot.changeMemberRole(member, DiscordRole.PLAYER)

                        e.reply()
                            .withContent("Поздравляю! Теперь ты подключен к аккаунту **${p.getName()}**")
                            .withEphemeral(true)
                            .awaitFirstOrNull()
                    }.then()
                }
                DiscordLinkResult.FAILED -> {
                    e.reply()
                        .withContent("Ты неправильно ввёл код!")
                        .withEphemeral(true)
                }
                DiscordLinkResult.LINKED -> {
                    e.reply()
                        .withContent("Твой аккаунт дискорд уже подключен к другому аккаунту!")
                        .withEphemeral(true)
                }
            }
    }

    fun handleMessageReceive() {
        bot.gatewayClient.on(MessageCreateEvent::class.java)
            .filterWhen { event ->
                val author = event.message.author
                
                if (!author.isPresent || author.get().isBot) {
                    return@filterWhen Mono.just(false)
                }

                event.message.channel.ofType(PrivateChannel::class.java).hasElement()
            }
            .flatMap { event ->
                val message = event.message

                val content = message.content.orEmpty().trim()
                if (!content.startsWith("!litematica")) {
                    return@flatMap Mono.empty<Void>()
                }

                val attachments = message.attachments.filter { it.filename.endsWith(".litematic") }
                if (attachments.isEmpty()) {
                    return@flatMap Mono.empty<Void>()
                }

                val file = attachments.first()

                val discordId = message.author.orElse(null)?.id ?: return@flatMap Mono.empty<Void>()
                mono {
                    val savedPath = FileManager.handleLitematicaFileAsync(file.url, file.filename)
                    val playerUUID = MemberTableHandler.getMemberUUID(discordId.asString())

                    LitematicaHook.replaceLitematicaFile(savedPath, file.filename, playerUUID)

                    savedPath
                }.flatMap { savedPath ->
                    message.channel.flatMap { ch ->
                        ch.createMessage("Файл `${file.filename}` успешно загружен на сервер!")
                    }
                }.onErrorResume { err ->
                    err.printStackTrace()
                    println("Ошибка при сохранении файла: ${err.message}")
                    message.channel.flatMap { ch ->
                        ch.createMessage("Произошла ошибка при загрузке файла: ${err.message}")
                    }
                }
            }
            .subscribe()
    }


}