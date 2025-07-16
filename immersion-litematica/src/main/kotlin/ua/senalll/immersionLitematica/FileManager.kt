package ua.senalll.immersionLitematica

import java.io.File
import java.io.FileWriter
import java.util.UUID

class FileManager {

    private val plugin = ImmersionLitematica.instance

    fun getPlayerDir(uuid: UUID): File {
        val dir = File(plugin.dataFolder, uuid.toString())
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getRawLitematicaDir(uuid: UUID): File {
        val dir = File(getPlayerDir(uuid), "litematica_raw_files")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun getSchematicDir(uuid: UUID): File {
        val dir = File(getPlayerDir(uuid), "schematic")
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun createSchematicJsonFile(uuid: UUID, fileName: String, content: String): File {
        val schematicDir = getSchematicDir(uuid)
        val file = File(schematicDir, "$fileName.json")

        if (!file.exists()) {
            file.createNewFile()
        }

        FileWriter(file).use { it.write(content) }

        return file
    }

    fun getLitematicaFile(uuid: UUID, fileName: String): File {
        return File(getRawLitematicaDir(uuid), fileName)
    }

    fun listSchematicFiles(uuid: UUID): List<File> {
        val dir = getSchematicDir(uuid)
        return dir.listFiles { f -> f.extension == "json" }?.toList() ?: emptyList()
    }
}