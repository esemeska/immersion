package ua.senalll.immersionCore

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionCore.config.yml.CoreConfig
import ua.senalll.immersionCore.config.json.DataConfig
import ua.senalll.immersionCore.modules.ChatModule
import ua.senalll.immersionCore.modules.DatabaseModule
import ua.senalll.immersionCore.modules.NickNameModule
import ua.senalll.immersionCore.modules.PlayerModule
import ua.senalll.immersionCore.modules.RestartModule
import ua.senalll.immersionCore.modules.TabModule
import ua.senalll.immersionCore.modules.VanishModule
import ua.senalll.immersionCore.pdc.PDCRegistry
import ua.senalll.immersionapi.module.ModuleManager

class ImmersionCore : JavaPlugin() {
    companion object {
        lateinit var instance: ImmersionCore
            private set

    }

    lateinit var coreConfig: CoreConfig
        private set

    lateinit var dataConfig: DataConfig
        private set

    lateinit var pdcRegistry: PDCRegistry
        private set

    private lateinit var moduleManager: ModuleManager

    override fun onEnable() {
        instance = this
        createConfig()
        pdcRegistry = PDCRegistry()

        handleModules()
    }

    override fun onDisable() {
        moduleManager.disableAll(this)
    }

    fun handleModules(){
        moduleManager = ModuleManager()
        moduleManager.register(ChatModule)
        moduleManager.register(DatabaseModule)
        moduleManager.register(NickNameModule)
        moduleManager.register(PlayerModule)
        moduleManager.register(RestartModule)
        moduleManager.register(TabModule)
        moduleManager.register(VanishModule)
        moduleManager.loadEnabledModules(this)
    }

    fun createConfig(){
        coreConfig = CoreConfig(this)
        coreConfig.ensureCoreDefaults()

        dataConfig = DataConfig(this)
    }
}
