package ua.senalll.immersionCore.modules

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.config.json.VanishConfig
import ua.senalll.immersionCore.vanish.VanishCommand
import ua.senalll.immersionCore.vanish.VanishListener
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object VanishModule: Module {
    override val moduleName: String = "vanish"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    override fun onLoad(plugin: JavaPlugin) {
        val plugin = plugin as ImmersionCore
        val vanishPDC = plugin.pdcRegistry.vanishPDC()
        val vanishConfig = plugin.dataConfig.vanishConfig
        vanishConfig.initializeVanishedList()

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(VanishCommand.createCommand("v", vanishConfig))
        }
        plugin.server.pluginManager.registerEvents(VanishListener(vanishConfig), plugin)

    }

    override fun onEnable(plugin: JavaPlugin) {
        //VanishManager.initPacketListeners(plugin)
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