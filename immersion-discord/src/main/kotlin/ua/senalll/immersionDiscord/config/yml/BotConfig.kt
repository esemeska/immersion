package ua.senalll.immersionDiscord.config.yml

class BotConfig(private val config: CoreConfig) {
    fun ensureBotDefaults(){
        val tabDefaults = mapOf<String, Any?>(
            "discord.token" to "",
            "discord.role_id" to "",
            "discord.guild_id" to "",
            "discord.bot_id" to ""
        )
        config.ensureDefaults(tabDefaults)
    }

    fun token(): String{
        return config.getString("discord.token") ?: ""
    }

    fun roleId(): String{
        return config.getString("discord.role_id") ?: ""
    }

    fun guildId(): String{
        return config.getString("discord.guild_id") ?: ""
    }

    fun botId(): String{
        return config.getString("discord.bot_id") ?: ""
    }
}