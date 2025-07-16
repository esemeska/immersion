package ua.senalll.immersionDiscord.link

import io.lettuce.core.RedisClient
import io.lettuce.core.RedisConnectionException
import io.lettuce.core.RedisURI
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import ua.senalll.immersionapi.util.Utils
import java.time.Duration

class RedisConnectionManager {
    private val logger = Utils.createLogger<RedisConnectionManager>()
    private lateinit var client: RedisClient
    private lateinit var connection: StatefulRedisConnection<String, String>
    private lateinit var commands: RedisCommands<String, String>
    private var isConnected = false

    companion object {
        private const val DEFAULT_HOST = "redis-18651.crce175.eu-north-1-1.ec2.redns.redis-cloud.com"
        private const val DEFAULT_PORT = 18651
        private const val DEFAULT_TIMEOUT_SECONDS = 10L

        private var instance: RedisConnectionManager? = null

        fun getInstance(): RedisConnectionManager {
            if (instance == null) {
                instance = RedisConnectionManager()
            }
            return instance!!
        }
    }

    fun connect(
        host: String = DEFAULT_HOST,
        port: Int = DEFAULT_PORT,
        password: String? = null,
        database: Int = 0,
        timeoutSeconds: Long = DEFAULT_TIMEOUT_SECONDS
    ): Boolean {
        try {
            val redisURI = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withTimeout(Duration.ofSeconds(timeoutSeconds))
                .withDatabase(database)
                .withAuthentication("default", "f4ynU7nIvzdtSwferlnNI1x4QR55dhAB")

            if (!password.isNullOrEmpty()) {
                redisURI.withPassword(password.toCharArray())
            }

            client = RedisClient.create(redisURI.build())
            connection = client.connect()
            commands = connection.sync()
            isConnected = true

            logger.info("Successfully connected to Redis at $host:$port")
            return true
        } catch (e: RedisConnectionException) {
            logger.warning("Failed to connect to Redis at $host:$port: ${e.message}. Plugin will use alternative storage.")
            isConnected = false
        } catch (e: Exception) {
            logger.severe("Unexpected error connecting to Redis at $host:$port: ${e.message}")
            isConnected = false
        }
        return false
    }

    fun isConnected(): Boolean {
        return isConnected
    }

    fun getCommands(): RedisCommands<String, String> {
        if (!::commands.isInitialized) {
            throw IllegalStateException("Redis connection not initialized. Call connect() first.")
        }
        return commands
    }

    fun shutdown() {
        if (::connection.isInitialized) {
            connection.close()
        }
        if (::client.isInitialized) {
            client.shutdown()
        }
        logger.info("Redis connection closed")
    }
}