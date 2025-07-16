package ua.senalll.immersionDiscord.config.yml

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.configs.YamlConfig

class CoreConfig (plugin: JavaPlugin): YamlConfig(plugin, "config.yml")  {
    val botConfig = BotConfig(this)

    fun ensureCoreDefaults() {
        botConfig.ensureBotDefaults()
    }

}