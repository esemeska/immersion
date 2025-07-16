package ua.senalll.immersionCore.config.json

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ua.senalll.immersionapi.configs.JsonConfig

class ChatDataConfig(private val dataConfig: JsonConfig) {
    companion object {
        private const val HI_KEY = "hi"
        private const val IGNORE_KEY = "ignore_pm"
    }

    fun initializeStructure() {
        if (dataConfig.getAtPath(HI_KEY) == null) {
            dataConfig.setAtPath(JsonObject(), HI_KEY)
        }

        if (dataConfig.getAtPath(IGNORE_KEY) == null) {
            dataConfig.setAtPath(JsonObject(), IGNORE_KEY)
        }
    }

    // Add a player to the hi messages list
    fun addHiMessage(player: String, targetUuid: String) {
        addToPlayerList(player, targetUuid, HI_KEY)
    }

    // Remove a player from the hi messages list
    fun removeHiMessage(player: String, targetUuid: String) {
        removeFromPlayerList(player, targetUuid, HI_KEY)
    }

    // Check if a player is in the hi messages list
    fun hasHiMessage(player: String, targetUuid: String): Boolean {
        return isInPlayerList(player, targetUuid, HI_KEY)
    }

    // Get all players in the hi messages list for a player
    fun getHiMessages(player: String): List<String> {
        return getPlayerList(player, HI_KEY)
    }

    // Add a player to the ignore PM list
    fun addIgnorePM(player: String, targetUuid: String) {
        addToPlayerList(player, targetUuid, IGNORE_KEY)
    }

    // Remove a player from the ignore PM list
    fun removeIgnorePM(player: String, targetUuid: String) {
        removeFromPlayerList(player, targetUuid, IGNORE_KEY)
    }

    // Check if a player is in the ignore PM list
    fun isIgnoringPM(player: String, targetUuid: String): Boolean {
        return isInPlayerList(player, targetUuid, IGNORE_KEY)
    }

    // Get all players in the ignore PM list for a player
    fun getIgnoredPlayers(player: String): List<String> {
        return getPlayerList(player, IGNORE_KEY)
    }

    // Helper method to add a UUID to a player's list
    private fun addToPlayerList(player: String, targetUuid: String, key: String) {
        val root = dataConfig.getRoot()
        val section = if (root.has(key) && root.get(key).isJsonObject) {
            root.getAsJsonObject(key)
        } else {
            JsonObject()
        }

        val playerArray = if (section.has(player) && section.get(player).isJsonArray) {
            section.getAsJsonArray(player)
        } else {
            JsonArray()
        }

        // Check if UUID already exists in the array
        val exists = playerArray.asJsonArray.any { it.asString == targetUuid }
        if (!exists) {
            playerArray.add(targetUuid)
            section.add(player, playerArray)
            root.add(key, section)
            dataConfig.setRoot(root)
        }
    }

    // Helper method to remove a UUID from a player's list
    private fun removeFromPlayerList(player: String, targetUuid: String, key: String) {
        val root = dataConfig.getRoot()
        if (!root.has(key)) return

        val section = root.getAsJsonObject(key)
        if (!section.has(player)) return

        val playerArray = section.getAsJsonArray(player)
        val updatedArray = JsonArray()

        // Create new array without the target UUID
        playerArray.forEach {
            if (it.asString != targetUuid) {
                updatedArray.add(it)
            }
        }

        section.add(player, updatedArray)
        root.add(key, section)
        dataConfig.setRoot(root)
    }

    // Helper method to check if a UUID is in a player's list
    private fun isInPlayerList(player: String, targetUuid: String, key: String): Boolean {
        val list = getPlayerList(player, key)
        return list.contains(targetUuid)
    }

    // Helper method to get a player's list
    private fun getPlayerList(player: String, key: String): List<String> {
        val root = dataConfig.getRoot()
        if (!root.has(key)) return emptyList()

        val section = root.getAsJsonObject(key)
        if (!section.has(player)) return emptyList()

        val playerArray = section.getAsJsonArray(player)
        return playerArray.map { it.asString }
    }

    // Clear all data for a specific player
    fun clearPlayerData(player: String, hi: Boolean, ignore: Boolean) {
        val root = dataConfig.getRoot()
        if (hi){
            if (root.has(HI_KEY)) {
                val hiSection = root.getAsJsonObject(HI_KEY)
                hiSection.remove(player)
                root.add(HI_KEY, hiSection)
            }
        }
        if (ignore){
            if (root.has(IGNORE_KEY)) {
                val ignoreSection = root.getAsJsonObject(IGNORE_KEY)
                ignoreSection.remove(player)
                root.add(IGNORE_KEY, ignoreSection)
            }
        }
        dataConfig.setRoot(root)
    }
}