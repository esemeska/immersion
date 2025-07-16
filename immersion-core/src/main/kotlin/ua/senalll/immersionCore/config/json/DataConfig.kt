package ua.senalll.immersionCore.config.json


import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.configs.JsonConfig

class DataConfig(val plugin: JavaPlugin): JsonConfig(plugin, "data.json") {
    val vanishConfig = VanishConfig(this)
    val chatDataConfig = ChatDataConfig(this)


}