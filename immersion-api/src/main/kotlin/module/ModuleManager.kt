package ua.senalll.immersionapi.module

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.util.Utils
import java.util.logging.Logger


class ModuleManager {

    private val modules = mutableListOf<Module>()
    private val logger: Logger = Utils.createLogger<ModuleManager>()

    fun register(module: Module) {
        modules += module
    }

    fun loadEnabledModules(plugin: JavaPlugin) {
        modules
            .sortedBy { it.modulePriority }
            .forEach { module ->
                try {
                    module.onLoad(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.LOAD
                    logger.info("Module ${module.moduleName} loaded (priority: ${module.modulePriority})")
                } catch (e: ModuleLoadException) {
                    module.lifecycleStage = ModuleLifecycleStage.ERROR
                    logger.severe("Error in onLoad for module ${module.moduleName}: ${e.message}")
                    module.onError(plugin)
                    module.onUnload(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.UNLOAD
                }
            }

        plugin.server.scheduler.runTask(plugin, Runnable {
            enableLoadedModules(plugin)
        })
    }

    private fun enableLoadedModules(plugin: JavaPlugin) {
        modules
            .filter { it.lifecycleStage == ModuleLifecycleStage.LOAD }
            .sortedBy { it.modulePriority }
            .forEach { module ->
                try {
                    module.onEnable(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.ENABLE
                    logger.info("Module ${module.moduleName} enabled")
                } catch (e: ModuleEnableException) {
                    module.lifecycleStage = ModuleLifecycleStage.ERROR
                    logger.severe("Error in onEnable for module ${module.moduleName}: ${e.message}")
                    module.onError(plugin)
                    module.onUnload(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.UNLOAD
                }
            }
    }

    fun disableAll(plugin: JavaPlugin) {
        modules
            .sortedByDescending { it.modulePriority }
            .forEach { module ->
                try {
                    module.onDisable(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.DISABLE
                    logger.info("Module ${module.moduleName} disabled (priority: ${module.modulePriority})")
                } catch (e: ModuleDisableException) {
                    module.lifecycleStage = ModuleLifecycleStage.ERROR
                    logger.severe("Module disable error: ${e.message}")
                    module.onError(plugin)
                    module.onUnload(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.UNLOAD
                } catch (e: Exception) {
                    module.lifecycleStage = ModuleLifecycleStage.ERROR
                    logger.severe("Unexpected error when disabling module ${module.moduleName}: ${e.message}")
                    module.onError(plugin)
                    module.onUnload(plugin)
                    module.lifecycleStage = ModuleLifecycleStage.UNLOAD
                }
            }
    }

    fun getModules(): List<Module> = modules.toList()

}