package ua.senalll.immersionapi.module

import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Logger

interface Module {
    val moduleName: String
    val modulePriority: ModulePriority
    val isEnabledByDefault: Boolean
    var lifecycleStage: ModuleLifecycleStage

    fun onLoad(plugin: JavaPlugin)
    fun onEnable(plugin: JavaPlugin)
    fun onActive(plugin: JavaPlugin)
    fun onDisable(plugin: JavaPlugin)
    fun onUnload(plugin: JavaPlugin)
    fun onError(plugin: JavaPlugin)
    fun onWarning(plugin: JavaPlugin)

    val logger: Logger get() = Logger.getLogger(this::class.java.name)
}