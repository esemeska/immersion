package ua.senalll.immersionCore.modules

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.database.NickNameTableHandler
import ua.senalll.immersionCore.database.PlayerDataListener
import ua.senalll.immersionCore.database.RelationTableHandler
import ua.senalll.immersionCore.database.TabTableHandler
import ua.senalll.immersionapi.database.DatabaseManager
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object DatabaseModule: Module {
    override val moduleName: String = "database"
    override val modulePriority: ModulePriority = ModulePriority.CORE
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    override fun onLoad(plugin: JavaPlugin) {
        DatabaseManager.loadDatabase(plugin)

        NickNameTableHandler.init()
        RelationTableHandler.init()
        TabTableHandler.init()

        plugin.server.pluginManager.registerEvents(PlayerDataListener(
            TabTableHandler.playerTabTableHandler,
            (plugin as ImmersionCore).pdcRegistry.tabPDC()),
            plugin
        )
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