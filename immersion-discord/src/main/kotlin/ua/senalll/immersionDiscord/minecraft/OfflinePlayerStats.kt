package ua.senalll.immersionDiscord.minecraft

import com.google.gson.JsonParser
import discord4j.core.spec.EmbedCreateSpec
import discord4j.rest.util.Color
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactor.mono
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.Statistic
import reactor.core.publisher.Mono
import ua.senalll.immersionDiscord.database.MemberTableHandler
import ua.senalll.immersionapi.util.Utils
import java.net.HttpURLConnection
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.logging.Level

object OfflinePlayerStats {
    private val logger = Utils.createLogger<OfflinePlayerStats>()

    suspend fun getPlayerStats(discordId: String): Mono<EmbedCreateSpec> {
        return mono(Dispatchers.IO) {
            try {
                val playerUUID = MemberTableHandler.getMemberUUID(discordId)
                val offlinePlayer: OfflinePlayer = Bukkit.getOfflinePlayer(playerUUID)
                val embedBuilder = EmbedCreateSpec.builder()
                    .title("Профиль ${offlinePlayer.name}")
                    .color(Color.of(240, 142, 198))

                if (offlinePlayer.hasPlayedBefore()) {
                    val deaths = offlinePlayer.getStatistic(Statistic.DEATHS)
                    embedBuilder.addField("Всего смертей", "```$deaths```", true)

                    val fishes = offlinePlayer.getStatistic(Statistic.FISH_CAUGHT)
                    embedBuilder.addField("Словлено рыбы", "```$fishes```", true)

                    val totalTime = offlinePlayer.getStatistic(Statistic.TOTAL_WORLD_TIME) / 20
                    embedBuilder.addField("Играет на сервере", "```${formatTotalTime(totalTime.toLong())}```", true)

                    val sdf = SimpleDateFormat("dd.MM.yyyy")
                    val lastLogin = offlinePlayer.lastLogin
                    embedBuilder.addField("Последний раз на сервере", "```${sdf.format(Date(lastLogin))}```", true)

                    val firstJoin = offlinePlayer.firstPlayed
                    embedBuilder.addField("Первый заход", "```${sdf.format(Date(firstJoin))}```", true)

                    embedBuilder.thumbnail(getCrafatarHeadUrlIfPremium(offlinePlayer.name.toString()))
                } else {
                    embedBuilder.description("Этот игрок попуск! Он ещё не разу не играл на нашем сервере(")
                    embedBuilder.thumbnail(getCrafatarHeadUrlIfPremium(offlinePlayer.name.toString()))
                }

                embedBuilder.build().also {
                    logger.log(Level.INFO, "Successfully created embed")
                }
            } catch (e: Exception) {
                logger.log(Level.SEVERE, "Error in getPlayerStats", e)
                EmbedCreateSpec.builder()
                    .title("Профиль не найден")
                    .description(
                        "Наша датабаза не хранит информацию об этом игроке. " +
                                "Скорее всего он ни разу не заходил на наш сервер (какой лох)."
                    )
                    .build()
            }
        }
    }


    private fun formatTotalTime(seconds: Long): String {
        return (seconds/ 3600).toString() + "ч"
    }

    private suspend fun getCrafatarHeadUrlIfPremium(playerName: String): String = withContext(Dispatchers.IO) {
        try {
            val client = HttpClient.newBuilder().build()
            val request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/$playerName"))
                .timeout(java.time.Duration.ofSeconds(3))
                .GET()
                .build()

            val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()
            if (response.statusCode() != 200) {
                return@withContext "https://mc-heads.net/avatar/2f878249a6e04526205db794c85e68c53c2e643ba19a194f985b84362767e8b7"
            }

            val json = JsonParser.parseString(response.body()).asJsonObject
            val rawUuid = json.get("id").asString

            val formattedUuid = rawUuid.replaceFirst(
                "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})".toRegex(),
                "$1-$2-$3-$4-$5"
            )

            "https://mc-heads.net/avatar/$formattedUuid/256/overlay.png"
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Failed to fetch Crafatar head for $playerName", e)
            "https://mc-heads.net/avatar/2f878249a6e04526205db794c85e68c53c2e643ba19a194f985b84362767e8b7"
        }
    }
}