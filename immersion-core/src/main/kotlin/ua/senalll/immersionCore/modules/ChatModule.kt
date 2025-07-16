package ua.senalll.immersionCore.modules

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.chat.ChatManager
import ua.senalll.immersionCore.chat.VanillaChat
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object ChatModule : Module {
    override val moduleName: String = "chat"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD


    override fun onLoad(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(ChatManager, plugin)
        plugin.server.pluginManager.registerEvents(VanillaChat, plugin)
    }

    override fun onEnable(plugin: JavaPlugin) {
    }

    override fun onActive(plugin: JavaPlugin) {
    }

    override fun onDisable(plugin: JavaPlugin) {
    }

    override fun onUnload(plugin: JavaPlugin) {
    }

    override fun onError(plugin: JavaPlugin) {
    }

    override fun onWarning(plugin: JavaPlugin) {
    }
}