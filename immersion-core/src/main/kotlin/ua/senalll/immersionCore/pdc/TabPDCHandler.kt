package ua.senalll.immersionCore.pdc

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionapi.pdc.PersistentDataHandler

class TabPDCHandler : PersistentDataHandler<String> {

    override val key: NamespacedKey = NamespacedKey(ImmersionCore.instance, "tab_settings")
    override val type: PersistentDataType<String, String> = PersistentDataType.STRING
    private val gson = Gson()


    fun saveTabSettings(player: Player, settings: Map<String, Any>) {
        val json = mapToString(settings)
        set(player.persistentDataContainer, json)
    }

    fun getTabSettings(player: Player): Map<String, Any> {
        val json = get(player.persistentDataContainer)
        return json?.let { stringToMap(it) } ?: mapOf()
    }

    fun updateTabSetting(player: Player, key: String, value: Any) {
        val settings = getTabSettings(player).toMutableMap()
        settings[key] = value
        saveTabSettings(player, settings)
    }

    private fun mapToString(map: Map<String, Any>): String {
        return gson.toJson(map)
    }

    private fun stringToMap(json: String): Map<String, Any> {
        val type = object : TypeToken<Map<String, Any>>() {}.type
        return try {
            gson.fromJson(json, type)
        } catch (e: Exception) {
            mapOf()
        }
    }
}