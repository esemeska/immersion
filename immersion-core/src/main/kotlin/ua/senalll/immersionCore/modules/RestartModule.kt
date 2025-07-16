package ua.senalll.immersionCore.modules

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.restart.RestartHandler
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object RestartModule: Module {
    override val moduleName: String = "restart"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    override fun onLoad(plugin: JavaPlugin) {
    }

    override fun onEnable(plugin: JavaPlugin) {
        if (!(plugin as ImmersionCore).coreConfig.isRestartEnabled()) return
        RestartHandler.handleRestart(ImmersionCore.Companion.instance)
        logger.info { "Restart task started." }
    }

    override fun onActive(plugin: JavaPlugin) {
    }

    override fun onDisable(plugin: JavaPlugin) {
    }

    override fun onUnload(plugin: JavaPlugin) {
    }

    override fun onError(plugin: JavaPlugin) {
        RestartHandler.stop()
    }

    override fun onWarning(plugin: JavaPlugin) {
        RestartHandler.stop()
    }


}