package ua.senalll.immersionDiscord.discord.command

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.command.ApplicationCommandOption
import discord4j.core.spec.EmbedCreateSpec
import discord4j.discordjson.json.ApplicationCommandOptionData
import discord4j.discordjson.json.ApplicationCommandRequest
import discord4j.rest.util.Color
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.mono
import reactor.core.publisher.Mono
import ua.senalll.immersionDiscord.discord.DiscordBot
import ua.senalll.immersionDiscord.minecraft.OfflinePlayerStats
import ua.senalll.immersionapi.util.Utils
import java.util.logging.Level

class SlashCommandHandler(private val bot: DiscordBot) {
    private val logger = Utils.createLogger<SlashCommandHandler>()

    fun init(){
        val commands = listOf(
            linkCommandRequest(),
            statsCommandRequest(),
        )

        bot.gatewayClient.restClient.applicationService
            .bulkOverwriteGuildApplicationCommand(bot.gatewayClient.selfId.asLong(),
                bot.guild.id.asLong(),
                commands)
            .subscribe()
        bot.gatewayClient.on(ChatInputInteractionEvent::class.java)
            .flatMap(handleCommands())
            .subscribe()
    }

    fun handleCommands(): (ChatInputInteractionEvent) -> Mono<Void> {
        return { event ->
            when (event.commandName) {
                "stats" -> {
                    event.deferReply().withEphemeral(true)
                        .then(processStatsCommand(event))
                        .doOnError { error ->
                            logger.log(Level.SEVERE, "Error processing stats command", error)
                        }
                }
                else -> Mono.empty()
            }
        }
    }

    private fun processStatsCommand(event: ChatInputInteractionEvent): Mono<Void> {
        val userIdMono = event.getOption("никнейм")
            .map { option ->
                when (option.type) {
                    ApplicationCommandOption.Type.USER -> {
                        option.value.get()
                            .asUser()
                            .map {
                                logger.log(Level.INFO, it.id.toString())
                                it.id.asString() }
                            .switchIfEmpty(Mono.just(event.interaction.user.id.asString()))
                    }
                    ApplicationCommandOption.Type.STRING -> {
                        extractUserIdFromInput(option.value.get().asString())
                    }
                    else -> {
                        Mono.just(event.interaction.user.id.asString())
                    }
                }
            }
            .orElse(Mono.just(event.interaction.user.id.asString()))
        val botId = bot.gatewayClient.selfId.asString()

        return userIdMono.flatMap { id ->
            mono {
                if (id == botId) {
                    EmbedCreateSpec.builder()
                        .title("Чо?")
                        .description(
                            "Ты меня не можешь проверить, так как на твой экран не " +
                                    "поместиться количество словленных мною рыб."
                        )
                        .build()
                }else {
                        OfflinePlayerStats.getPlayerStats(id).awaitSingle()
                }
            }.flatMap { embed ->
                event.editReply().withEmbeds(embed).then()
            }.onErrorResume { error ->
                logger.log(Level.SEVERE, "Error getting player stats", error)
                val errorEmbed = EmbedCreateSpec.builder()
                    .title("Ошибка")
                    .description("Произошла ошибка при получении статистики: ${error.message}")
                    .color(Color.RED)
                    .build()

                event.editReply().withEmbeds(errorEmbed).then()
            }
        }
    }

    fun statsCommandRequest() = ApplicationCommandRequest.builder()
        .name("stats")
        .description("Просмотреть некоторую статистику игрока")
        .addOption(
            ApplicationCommandOptionData.builder()
                .name("никнейм")
                .description("Никнейм игрока")
                .type(3) // STRING тип
                .required(false)
                .build()
        )
        .build()

    fun linkCommandRequest() = ApplicationCommandRequest.builder()
        .name("verify")
        .description("Привязать майнкрафт аккаунт")
        .addOption(
            ApplicationCommandOptionData.builder()
                .name("код")
                .description("Одноразовый код из майнкрафт")
                .type(3)
                .required(true)
                .build()
        )
        .build()

    private fun extractUserIdFromInput(input: String): Mono<String> {
        val mentionPattern = "<@(\\d+)>".toRegex()
        val matchResult = mentionPattern.find(input)

        return if (matchResult != null) {
            Mono.just(matchResult.groupValues[1])
        } else {
            Mono.just(input)
        }
    }
}