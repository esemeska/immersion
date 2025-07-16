package ua.senalll.immersionapi.configs

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import org.bukkit.plugin.java.JavaPlugin
import ua.senalll.immersionapi.util.Utils
import java.io.File
import java.io.IOException
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.logging.Logger
import kotlin.concurrent.read
import kotlin.concurrent.write

open class JsonConfig (
    plugin: JavaPlugin,
    private val fileName: String = "data.json"
) {
    companion object {
        val GSON: Gson = GsonBuilder().setPrettyPrinting().create()
    }

    private val logger: Logger = Utils.createLogger<JsonConfig>()
    private val file: File = File(plugin.dataFolder, fileName)
    private val lock = ReentrantReadWriteLock()

    init {
        if (!file.exists()) {
            try {
                file.createNewFile()
                logger.info("Created file $fileName")
                saveJson(JsonObject())
            } catch (e: IOException) {
                logger.severe("Error when creating $fileName: ${e.message}")
            }
        }
    }

    fun reload(): JsonElement {
        return try {
            val content = file.readText()
            if (content.isBlank() || content == "null") JsonObject()
            else JsonParser.parseString(content)
        } catch (e: IOException) {
            logger.severe("Error reloading $fileName: ${e.message}")
            JsonObject()
        }
    }

    fun saveJson(json: JsonElement) {
        try {
            file.writeText(GSON.toJson(json))
        } catch (e: IOException) {
            logger.severe("Error saving $fileName: ${e.message}")
        }
    }

    fun clear() = saveJson(JsonObject())

    fun setProperty(player: String, property: String, value: JsonElement?) = lock.write {
        val root = reload().asJsonObject
        val propertyObj = if (root.has(property) && root.get(property).isJsonObject)
            root.getAsJsonObject(property)
        else JsonObject()

        if (value != null) propertyObj.add(player, value)
        else propertyObj.remove(player)

        root.add(property, propertyObj)
        saveJson(root)
    }

    fun removeProperty(player: String, property: String) = lock.write {
        val root = reload().asJsonObject
        if (root.has(property)) {
            val propertyObj = root.getAsJsonObject(property)
            propertyObj.remove(player)
            root.add(property, propertyObj)
            saveJson(root)
        }
    }

    fun getProperty(player: String, property: String): JsonElement? = lock.read {
        val root = reload().asJsonObject
        if (!root.has(property)) return null
        val propertyObj = root.getAsJsonObject(property)
        return propertyObj.get(player)
    }

    fun getRoot(): JsonObject = lock.read { reload().asJsonObject }

    fun setRoot(json: JsonObject) = saveJson(json)

    fun getAtPath(vararg path: String): JsonElement? = lock.read {
        var current: JsonElement = reload()
        for (key in path) {
            if (current is JsonObject && current.has(key)) {
                current = current.get(key)
            } else {
                return null
            }
        }
        return current
    }

    fun setAtPath(value: JsonElement, vararg path: String) = lock.write {
        val root = reload().asJsonObject
        var current: JsonObject = root
        for (i in 0 until path.size - 1) {
            val key = path[i]
            if (!current.has(key) || !current.get(key).isJsonObject) {
                current.add(key, JsonObject())
            }
            current = current.getAsJsonObject(key)
        }
        current.add(path.last(), value)
        saveJson(root)
    }

    fun getList(path: String): List<JsonElement>? = getAtPath(*path.split('.').toTypedArray())?.asJsonArray?.toList()

    fun getMap(path: String): Map<String, JsonElement>? =
        getAtPath(*path.split('.').toTypedArray())?.asJsonObject?.entrySet()?.associate { it.key to it.value }
}