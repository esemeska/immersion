package ua.senalll.immersionapi.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.net.URI
import java.util.UUID
import java.util.logging.Logger

object Utils {

    inline fun <reified T> createLogger(): Logger {
        return Logger.getLogger(T::class.java.name)
    }

    fun isURL(input: String): Boolean {
        try {
            if (!input.startsWith("http://") && !input.startsWith("https://")) {
                return false
            }

            val uri = URI(input)
            if (!uri.isAbsolute) {
                return false
            }

            uri.toURL()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getOfflinePlayer(uuid: UUID): OfflinePlayer = Bukkit.getOfflinePlayer(uuid)


}