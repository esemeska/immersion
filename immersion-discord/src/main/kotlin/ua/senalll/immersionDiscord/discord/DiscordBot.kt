package ua.senalll.immersionDiscord.discord

import discord4j.common.util.Snowflake
import discord4j.core.DiscordClientBuilder
import discord4j.core.GatewayDiscordClient
import discord4j.core.event.domain.lifecycle.ReadyEvent
import discord4j.core.`object`.entity.Guild
import discord4j.core.`object`.entity.User
import discord4j.core.`object`.entity.channel.TextChannel
import discord4j.core.`object`.presence.ClientActivity
import discord4j.core.`object`.presence.ClientPresence
import discord4j.discordjson.possible.Possible
import discord4j.gateway.intent.Intent
import discord4j.gateway.intent.IntentSet
import reactor.core.publisher.Mono
import ua.senalll.immersionDiscord.config.yml.BotConfig
import ua.senalll.immersionDiscord.discord.command.SlashCommandHandler
import ua.senalll.immersionDiscord.discord.listener.DiscordLinkingListener
import ua.senalll.immersionapi.util.Utils
import java.util.Optional
import java.util.logging.Level

class DiscordBot(val botConfig: BotConfig) {
    private val logger = Utils.createLogger<DiscordBot>()
    private val BOT_TOKEN = botConfig.token()

    lateinit var gatewayClient: GatewayDiscordClient
    lateinit var guild: Guild
    lateinit var botName: String

    fun connect(){
        DiscordClientBuilder.create(BOT_TOKEN)
            .build()
            .gateway()
            .setEnabledIntents(IntentSet.of(
                Intent.GUILD_MESSAGES, Intent.MESSAGE_CONTENT, Intent.GUILD_MEMBERS,
                Intent.DIRECT_MESSAGES
            ))
            .login()
            .flatMap { gateway ->
                gatewayClient = gateway
                gatewayClient.updatePresence(ClientPresence.online(ClientActivity.playing("Kuromi")))

                gatewayClient.self.map { user -> botName = user.username }
            }
            .doOnSuccess {
                initGuild()
            }
            .block()
    }

    fun initGuild(){
        gatewayClient.on(ReadyEvent::class.java)
            .doOnNext { logger.log(Level.INFO, "ReadyEvent") }
            .flatMap {
                gatewayClient.getGuildById(Snowflake.of(botConfig.guildId()))
            }
            .switchIfEmpty(Mono.error(RuntimeException("Could not find guild with ID: ${botConfig.guildId()}")))
            .flatMap<Any> { mainGuild ->
                this.guild = mainGuild

                val slashCommandHandler = SlashCommandHandler(this)
                slashCommandHandler.init()

                val listener = DiscordLinkingListener(this)
                listener.registerEvents()

                Mono.empty()
            }
            .doOnError { error ->
                logger.log(Level.SEVERE, "Failed to initialize guild: ${error.message}", error)
            }
            .subscribe()
    }

    fun disconnect() {
        if (::gatewayClient.isInitialized) {
            gatewayClient.logout()
                .doOnSuccess {
                    println("Бот успешно отключён")
                }
                .doOnError { error ->
                    println("Ошибка при отключении бота: ${error.message}")
                }
                .subscribe()
        } else {
            println("Клиент ещё не инициализирован, отключать нечего")
        }
    }

    fun changeMemberNickName(user: User, nickname: String){
        guild.getMemberById(Snowflake.of(user.id.asString()))
            .switchIfEmpty(
                Mono.fromRunnable<Void> {
                    logger.log(Level.INFO, "Не может изменить никнейм, из-за того что member == null")
                }.then(Mono.empty())
            )
            .flatMap { member ->
                member.edit()
                    .withNickname(Possible.of(Optional.ofNullable(nickname)))
            }.doOnSuccess {
                logger.log(Level.INFO, "Никнейм успешно изменен на: $nickname")
            }
            .doOnError { error ->
                logger.log(Level.SEVERE, "Не удалось изменить никнейм: ${error.message}")
            }
            .subscribe()
    }

    fun changeMemberNickName(id: String, nickname: String){
        guild.getMemberById(Snowflake.of(id))
            .flatMap { member ->
                if(member == null) {
                    logger.log(Level.INFO, "Не может изменить никнейм, из-за того что member == null")
                }
                member.edit()
                    .withNickname(Possible.of(Optional.ofNullable(nickname)))
            }.doOnSuccess {
                logger.log(Level.INFO, "Никнейм успешно изменен на: $nickname")
            }
            .doOnError { error ->
                logger.log(Level.SEVERE, "Не удалось изменить никнейм: ${error.message}")
            }
            .subscribe()
    }

    fun changeMemberRole(user: User, role: DiscordRole) {
        guild.getMemberById(Snowflake.of(user.id.asString()))
            .switchIfEmpty(
                Mono.fromRunnable<Void> {
                    logger.log(Level.INFO, "Не может установить роль: участник не найден")
                }.then(Mono.empty())
            )
            .flatMap { member ->
                member.edit()
                    .withRoles(Snowflake.of(botConfig.roleId()))
            }
            .subscribe(
                { logger.log(Level.INFO, "Роль успешно изменена на: ${role.roleName}") },
                { error -> logger.log(Level.SEVERE, "Не удалось установить роль: ${error.message}") }
            )
    }

    fun inviteLink(): Mono<String> {
        return gatewayClient.getGuildById(guild.id)
            .flatMap { guild ->
                guild.channels
                    .ofType(TextChannel::class.java)
                    .next()
                    .flatMap { channel ->
                        channel.createInvite { spec ->
                            spec.setMaxAge(3600)
                            spec.setMaxUses(1)
                            spec.setTemporary(false)
                        }
                    }
            }
            .map { invite ->
                "https://discord.gg/${invite.code}"
            }
    }
}