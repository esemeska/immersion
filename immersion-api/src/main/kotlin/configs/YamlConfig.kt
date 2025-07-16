package ua.senalll.immersionapi.configs

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.util.Utils
import java.io.File
import java.io.IOException
import java.util.logging.Logger

open class YamlConfig(
    private val plugin: JavaPlugin,
    private val filename: String = "config.yml"
) {
    private val logger: Logger = Utils.createLogger<YamlConfig>()
    private val file: File = File(plugin.dataFolder, filename)
    protected var config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

    init {
        if (!file.exists()) {
            plugin.saveResource(filename, false)
            logger.info { "Created new $filename for plugin ${plugin.name}" }
        }
        reload()
    }

    open fun ensureDefaults(defaults: Map<String, Any?>) {
        var changed = false
        for ((key, value) in defaults) {
            if (!contains(key)) {
                set(key, value)
                changed = true
            }
        }
        if (changed) save()
    }

    open fun migrateConfig(migrations: List<Pair<String, String>>) {
        var changed = false
        for ((oldKey, newKey) in migrations) {
            if (contains(oldKey) && !contains(newKey)) {
                set(newKey, config.get(oldKey))
                config.set(oldKey, null)
                changed = true
            }
        }
        if (changed) save()
    }

    open fun reload() {
        config = YamlConfiguration.loadConfiguration(file)
    }

    open fun save() {
        try {
            config.save(file)
        } catch (e: IOException) {
            logger.severe { "Error when saving $filename: ${e.message}" }
        }
    }

    fun getString(path: String): String? = config.getString(path)
    fun getInt(path: String): Int = config.getInt(path)
    fun getBoolean(path: String): Boolean = config.getBoolean(path)
    fun getStringList(path: String): List<String> = config.getStringList(path)
    fun getSection(path: String) = config.getConfigurationSection(path)
    fun contains(path: String): Boolean = config.contains(path)

    fun set(path: String, value: Any?) {
        config.set(path, value)
        save()
    }

    fun getMap(path: String): Map<String, Any>? =
        config.getConfigurationSection(path)?.getValues(false)

    fun getList(path: String): List<Any>? =
        config.getList(path)?.filterIsInstance<Any>()

    fun getDeepSection(path: String): Map<String, Any?>? {
        val section = config.getConfigurationSection(path) ?: return null
        return section.getKeys(false).associateWith { key ->
            val subPath = if (path.isEmpty()) key else "$path.$key"
            when (val value = config.get(subPath)) {
                is org.bukkit.configuration.ConfigurationSection -> getDeepSection(subPath)
                else -> value
            }
        }
    }

    inline fun <reified T> getAsObject(path: String, crossinline mapper: (Map<String, Any?>) -> T): T? {
        val map = getDeepSection(path) ?: return null
        return mapper(map)
    }
}