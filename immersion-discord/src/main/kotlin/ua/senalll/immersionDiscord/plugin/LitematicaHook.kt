package ua.senalll.immersionDiscord.plugin

import org.bukkit.Bukkit
import ua.senalll.immersionLitematica.discord.RawLitematicaData
import ua.senalll.immersionLitematica.discord.RawLitematicaHandler
import java.nio.file.Path
import java.util.UUID

object LitematicaHook {

    fun replaceLitematicaFile(litematicaSavedPath: Path, fileName: String, playerUUID: UUID){
        Bukkit.getPluginManager().getPlugin("immersion-litematica")
            ?: throw IllegalStateException("Плагин immersion-litematica не найден")

        val instance = RawLitematicaHandler.getInstance()

        instance.convertLitematicFile(
            RawLitematicaData(litematicaSavedPath, fileName, playerUUID)
        )
    }
}