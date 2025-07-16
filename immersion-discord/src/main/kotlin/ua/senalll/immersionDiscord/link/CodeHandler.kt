package ua.senalll.immersionDiscord.link

import ua.senalll.immersionapi.util.Utils
import java.util.UUID

object CodeHandler {
    private val logger = Utils.createLogger<CodeHandler>()
    private val redisRepository = RedisCodeRepository()

    fun saveCode(playerId: UUID, code: String) {
        redisRepository.saveCode(playerId, code)
    }

    fun getCode(playerId: UUID): String? {
        return redisRepository.getCode(playerId)
    }

    fun deleteCode(playerId: UUID) {
        redisRepository.deleteCode(playerId)
    }

    fun isPlayerHasCode(playerId: UUID): Boolean {
        return redisRepository.isPlayerHasCode(playerId)
    }

    fun checkCode(code: String): Boolean {
        return redisRepository.checkCode(code)
    }

    fun getUUIDByCode(code: String): UUID? {
        return redisRepository.getUUIDByCode(code)
    }
}