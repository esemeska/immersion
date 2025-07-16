package ua.senalll.immersionDiscord.plugin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.bukkit.Bukkit
import ua.senalll.immersionapi.util.Utils
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.logging.Level

object FileManager {
    private val logger = Utils.createLogger<FileManager>()

    suspend fun handleLitematicaFileAsync(fileUrl: String, filename: String): Path = withContext(Dispatchers.IO) {
        try {
            val pluginLitematica = Bukkit.getPluginManager().getPlugin("immersion-litematica")
                ?: throw IllegalStateException("Плагин 'immersion-litematica' не найден.")
            if (!pluginLitematica.isEnabled) {
                throw IllegalStateException("Плагин 'immersion-litematica' отключён.")
            }

            val baseDir = pluginLitematica.dataFolder.toPath().toAbsolutePath()
                ?: throw IllegalStateException("Не удалось определить директорию плагина.")

            val saveDir = baseDir.resolve("from_discord").toAbsolutePath()

            if (!Files.exists(saveDir)) {
                Files.createDirectories(saveDir)
            }

            val savePath = saveDir.resolve(filename).toAbsolutePath()

            URI(fileUrl).toURL().openStream().use { inputStream ->
                Files.copy(inputStream, savePath, StandardCopyOption.REPLACE_EXISTING)
            }

            logger.log(Level.INFO, "Successfully saved Litematica file: $savePath")
            savePath
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Failed to handle Litematica file: $filename from $fileUrl", e)
            throw e
        }
    }
}