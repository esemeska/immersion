package ua.senalll.immersionFishing

import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionFishing.listener.PlayerFishListener
import ua.senalll.immersionFishing.listener.RodChangingListener

class ImmersionFishing : JavaPlugin() {
    companion object {
        lateinit var instance: ImmersionFishing
    }

    override fun onEnable() {
        instance = this
        this.server.pluginManager.registerEvents(PlayerFishListener, this)
        this.server.pluginManager.registerEvents(RodChangingListener, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}
