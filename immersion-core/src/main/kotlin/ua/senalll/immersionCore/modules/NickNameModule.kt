package ua.senalll.immersionCore.modules

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.nickname.NickNameListener
import ua.senalll.immersionCore.nickname.NickNameScoreboard
import ua.senalll.immersionCore.nickname.RelationCommand
import ua.senalll.immersionCore.nickname.profile.ProfileGuiListener
import ua.senalll.immersionapi.module.Module
import ua.senalll.immersionapi.module.ModuleLifecycleStage
import ua.senalll.immersionapi.module.ModulePriority

object NickNameModule : Module {
    override val moduleName: String = "nickname"
    override val modulePriority: ModulePriority = ModulePriority.LAST
    override val isEnabledByDefault: Boolean = true
    override var lifecycleStage: ModuleLifecycleStage = ModuleLifecycleStage.LOAD

    override fun onLoad(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(NickNameListener(plugin as ImmersionCore), plugin)
        plugin.server.pluginManager.registerEvents(ProfileGuiListener((plugin as ImmersionCore).pdcRegistry.nicknamePDC()), plugin)

        plugin.lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { commands ->
            commands.registrar().register(
                RelationCommand.createCommand("relation", plugin.pdcRegistry.relationPDC())
            )
        }
    }

    override fun onEnable(plugin: JavaPlugin) {
        plugin.server.pluginManager.registerEvents(NickNameScoreboard(plugin), plugin)
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