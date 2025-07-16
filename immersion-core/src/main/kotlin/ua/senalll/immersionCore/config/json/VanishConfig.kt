package ua.senalll.immersionCore.config.json

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import org.bukkit.entity.Player
import java.util.UUID

class VanishConfig(private val config: DataConfig) {
    companion object {
        private const val KEY = "vanished_players"
    }

    fun initializeVanishedList() {
        if (config.getAtPath(KEY) == null) {
            val emptyArray = JsonArray()
            config.setAtPath(emptyArray, KEY)
        }
    }

    fun addVanishedPlayer(player: Player) {
        val vanishedList = getVanishedPlayersAsJsonArray()
        val playerUuid = player.uniqueId.toString()

        if (!isPlayerVanished(player)) {
            vanishedList.add(JsonPrimitive(playerUuid))
            config.setAtPath(vanishedList, KEY)
        }
    }

    fun removeVanishedPlayer(player: Player) {
        val vanishedList = getVanishedPlayersAsJsonArray()
        val playerUuid = player.uniqueId.toString()

        val newList = JsonArray()
        vanishedList.forEach { element ->
            if (element.asString != playerUuid) {
                newList.add(element)
            }
        }

        config.setAtPath(newList, KEY)
    }

    fun getVanishedPlayers(): List<UUID> {
        return getVanishedPlayersAsJsonArray().mapNotNull {
            try {
                UUID.fromString(it.asString)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun isPlayerVanished(player: Player): Boolean {
        return isPlayerVanished(player.uniqueId)
    }

    fun isPlayerVanished(uuid: UUID): Boolean {
        val playerUuid = uuid.toString()
        val vanishedList = getVanishedPlayersAsJsonArray()

        return vanishedList.any { it.asString == playerUuid }
    }

    private fun getVanishedPlayersAsJsonArray(): JsonArray {
        val vanishedElement = config.getAtPath(KEY)

        if (vanishedElement == null || !vanishedElement.isJsonArray) {
            val emptyArray = JsonArray()
            config.setAtPath(emptyArray, KEY)
            return emptyArray
        }

        return vanishedElement.asJsonArray
    }
}