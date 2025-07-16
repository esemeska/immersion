package ua.senalll.immersionLitematica.schematic

import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionLitematica.discord.RawLitematicaData
import ua.senalll.immersionLitematica.schematic.json.JsonSchematic
import java.nio.file.Files
import java.nio.file.StandardCopyOption

object SchematicManager {
    private val rootDir = ImmersionLitematica.instance.dataFolder.toPath()

    fun initializeJsonSchematic(rawLitematicaData: RawLitematicaData){
        initializePlayerDir(rawLitematicaData)

    }


    fun initializePlayerDir(rawLitematicaData: RawLitematicaData){
        val playerUUID = rawLitematicaData.playerUUID
        val playerDir = rootDir.resolve(playerUUID.toString())

        val litematicDir = playerDir.resolve("litematic")
        val schematicDir = playerDir.resolve("schematic")

        Files.createDirectories(litematicDir)
        Files.createDirectories(schematicDir)

        val sourceFile = rawLitematicaData.filePath
        val targetFile = litematicDir.resolve(sourceFile.fileName)


        Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING)

        val jsonSchematic = JsonSchematic.fromLitematic()
        jsonSchematic?.setAuthor(playerUUID)
    }
}