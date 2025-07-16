package ua.senalll.immersionDiscord.link

import ua.senalll.immersionapi.util.Utils
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class RedisCodeRepository {
    private val logger = Utils.createLogger<RedisCodeRepository>()
    private val redisConnection = RedisConnectionManager.getInstance()

    private val codeStorage = ConcurrentHashMap<String, String>()
    private val reverseCodeStorage = ConcurrentHashMap<String, String>()

    companion object {
        private const val CODE_PREFIX = "player_code:"
        private const val CODE_REVERSE_PREFIX = "code_player:"
    }

    fun saveCode(playerId: UUID, code: String) {
        val playerIdString = playerId.toString()

        if (redisConnection.isConnected()) {
            val commands = redisConnection.getCommands()
            commands.set("$CODE_PREFIX$playerIdString", code)
            commands.set("$CODE_REVERSE_PREFIX$code", playerIdString)
        } else {
            codeStorage["$CODE_PREFIX$playerIdString"] = code
            reverseCodeStorage["$CODE_REVERSE_PREFIX$code"] = playerIdString
        }

        logger.info("Saved code for player $playerIdString")
    }

    fun getCode(playerId: UUID): String? {
        val playerIdString = playerId.toString()

        if (redisConnection.isConnected()) {
            return redisConnection.getCommands().get("$CODE_PREFIX$playerIdString")
        } else {
            return codeStorage["$CODE_PREFIX$playerIdString"]
        }
    }

    fun deleteCode(playerId: UUID) {
        val playerIdString = playerId.toString()

        // First get the code to delete the reverse mapping
        val code = getCode(playerId)

        if (redisConnection.isConnected()) {
            val commands = redisConnection.getCommands()
            if (code != null) {
                commands.del("$CODE_REVERSE_PREFIX$code")
            }
            commands.del("$CODE_PREFIX$playerIdString")
        } else {
            if (code != null) {
                reverseCodeStorage.remove("$CODE_REVERSE_PREFIX$code")
            }
            codeStorage.remove("$CODE_PREFIX$playerIdString")
        }

        logger.info("Deleted code for player $playerIdString")
    }

    fun isPlayerHasCode(playerId: UUID): Boolean {
        return getCode(playerId) != null
    }

    fun checkCode(code: String): Boolean {
        if (redisConnection.isConnected()) {
            return redisConnection.getCommands()?.get("$CODE_REVERSE_PREFIX$code") != null
        } else {
            return reverseCodeStorage["$CODE_REVERSE_PREFIX$code"] != null
        }
    }

    fun getUUIDByCode(code: String): UUID? {
        val playerIdString = if (redisConnection.isConnected()) {
            redisConnection.getCommands()?.get("$CODE_REVERSE_PREFIX$code")
        } else {
            reverseCodeStorage["$CODE_REVERSE_PREFIX$code"]
        } ?: return null

        return try {
            UUID.fromString(playerIdString)
        } catch (e: IllegalArgumentException) {
            logger.warning("Invalid UUID format found in codes: $playerIdString")
            null
        }
    }
}