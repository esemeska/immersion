package ua.senalll.immersionCore.modules

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.hook.SkinsRestorerHook
import ua.senalll.immersionCore.player.hand.HandListener
import ua.senalll.immersionCore.player.heads.PlayerHeadOnDeath
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object PlayerModule : Module {

    override val moduleName: String = "player"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    override fun onLoad(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(HandListener, plugin)
        SkinsRestorerHook.initHandler()
        plugin.server.pluginManager.registerEvents(PlayerHeadOnDeath, plugin)
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