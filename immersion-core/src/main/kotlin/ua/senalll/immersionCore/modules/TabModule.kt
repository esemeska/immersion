package ua.senalll.immersionCore.modules

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.tab.Tab
import ua.senalll.immersionCore.tab.TabCommand
import ua.senalll.immersionCore.tab.TabTask
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object TabModule: Module {
    override val moduleName: String = "tab"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    var tab: Tab? = null


    override fun onLoad(plugin: JavaPlugin) {
        val plugin = plugin as ImmersionCore
        val tabPDC = plugin.pdcRegistry.tabPDC()
        tab = Tab(plugin.coreConfig.tabConfig, tabPDC, plugin)

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(TabCommand.createCommand("tab", tabPDC))
        }
    }

    override fun onEnable(plugin: JavaPlugin) {
        if (tab === null) return
        TabTask(ImmersionCore.instance, (tab as Tab)).startTabUpdateTask()
    }

    override fun onActive(plugin: JavaPlugin) {
    }

    override fun onDisable(plugin: JavaPlugin) {
    }

    override fun onUnload(plugin: JavaPlugin) {
    }

    override fun onError(plugin: JavaPlugin) {
        if (tab === null) return
        TabTask(ImmersionCore.instance, (tab as Tab)).stop()
    }

    override fun onWarning(plugin: JavaPlugin) {
    }
}