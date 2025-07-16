package ua.senalll.immersionDiscord

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import ua.senalll.immersionDiscord.config.yml.CoreConfig
import ua.senalll.immersionDiscord.database.MemberTableHandler
import ua.senalll.immersionDiscord.discord.DiscordBot
import ua.senalll.immersionDiscord.discord.MinecraftLinkListener
import ua.senalll.immersionDiscord.link.RedisConnectionManager
import ua.senalll.immersionapi.database.DatabaseConnectionManager
import ua.senalll.immersionapi.database.DatabaseManager
import java.io.File

class ImmersionDiscord : SuspendingJavaPlugin() {
    companion object {
        lateinit var instance: ImmersionDiscord
        var connectionManager: DatabaseConnectionManager = DatabaseConnectionManager()
    }

    lateinit var coreConfig: CoreConfig
    lateinit var redisConnectionManager: RedisConnectionManager

    lateinit var bot: DiscordBot

    override suspend fun onEnableAsync() {
        instance = this
        createConfig()

        initializeDatabase()
        DatabaseManager.init(this)

        MemberTableHandler.init()

        bot = DiscordBot(coreConfig.botConfig)
        bot.connect()

        handleRedis()

        this.server.pluginManager.registerEvents(MinecraftLinkListener(bot), this)
    }

    fun initializeDatabase() {
        val dbFile = File(dataFolder, "db.sqlite")

        dbFile.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()
                println("Создана директория для базы данных: ${parent.absolutePath}")
            }
        }

        println("Инициализация SQLite базы: ${dbFile.absolutePath}")

        connectionManager.setup("jdbc:sqlite:${dbFile.absolutePath}")
    }

    override suspend fun onDisableAsync(){
        DatabaseManager.shutdown()
        bot.disconnect()
        redisConnectionManager.shutdown()
    }

    fun handleRedis() {
        redisConnectionManager = RedisConnectionManager.getInstance()
        try {
            redisConnectionManager.connect()
        } catch (e: Exception) {
            logger.warning("Could not connect to Redis. Using fallback storage method.")
        }
    }

    fun createConfig(){
        coreConfig = CoreConfig(this)
        coreConfig.ensureCoreDefaults()
    }
}
