package ua.senalll.immersionCore.config.yml

import kotlin.collections.iterator

class TabConfig(private val config: CoreConfig) {

    fun ensureTabDefaults(){
        val tabDefaults = mapOf<String, Any?>(
            "tab.header" to "VANILLA SERVER\nTHIS iS HEADER",
            "tab.footer" to "THIS iS FOOTER",
            "tab.mspt" to "MSPT: %mspt%",
            "tab.tps" to "TPS: %tps%",
            "tab.online" to "ONLINE: %online%",
            "tab.restart-count" to "До рестарта: %time-to-restart%"
        )
        config.ensureDefaults(tabDefaults)
    }

    fun getHeader(): String {
        return config.getString("tab.header") ?: "VANILLA SERVER\nTHIS iS HEADER"
    }

    fun getFooter(): String {
        return config.getString("tab.footer") ?: "THIS iS FOOTER"
    }

    fun getMsptFormat(): String {
        return config.getString("tab.mspt") ?: "MSPT: %mspt%"
    }

    fun getTpsFormat(): String {
        return config.getString("tab.tps") ?: "TPS: %tps%"
    }

    fun getOnlineFormat(): String {
        return config.getString("tab.online") ?: "ONLINE: %online%"
    }

    fun getRestartCountFormat(): String {
        return config.getString("tab.restart-count") ?: "До рестарта: %time-to-restart%"
    }

    fun formatHeader(replacements: Map<String, String>): String {
        return replaceVariables(getHeader(), replacements)
    }

    fun formatFooter(replacements: Map<String, String>): String {
        return replaceVariables(getFooter(), replacements)
    }

    private fun replaceVariables(text: String, replacements: Map<String, String>): String {
        var result = text
        for ((key, value) in replacements) {
            result = result.replace("%$key%", value)
        }
        return result
    }
}