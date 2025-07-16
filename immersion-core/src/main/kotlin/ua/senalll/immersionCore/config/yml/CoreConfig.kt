package ua.senalll.immersionCore.config.yml

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.configs.YamlConfig

class CoreConfig (plugin: JavaPlugin): YamlConfig(plugin, "config.yml") {

    val tabConfig = TabConfig(this)
    val chatConfig = ChatConfig(this)

    fun ensureCoreDefaults() {
        if (!contains("restart-interval-minutes")) {
            set("restart-interval-hours", 6)
        }
        if (!contains("restart-enabled")) {
            set("restart-enabled", true)
        }

        tabConfig.ensureTabDefaults()
        chatConfig.ensureChatDefaults()
    }

    fun isRestartEnabled(): Boolean {
        reload()
        return getBoolean("restart-enabled")
    }

    fun getRestartIntervalHours(): Int {
        reload()
        return getInt("restart-interval-hours").takeIf { it > 0 } ?: 6
    }

}