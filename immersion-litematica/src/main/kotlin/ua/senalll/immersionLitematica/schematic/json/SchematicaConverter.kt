package ua.senalll.immersionLitematica.schematic.json

import com.fasterxml.jackson.databind.ObjectMapper
import net.querz.nbt.io.NBTUtil
import net.querz.nbt.io.NamedTag
import net.querz.nbt.tag.CompoundTag
import java.io.File
import java.io.FileWriter

class SchematicaConverter {

    fun convertToJson(fileName: String, outputFilePath: String) {
        try {
            val inputFilePath = fileName

            val nbtData = readNbtFileWithQuerz(inputFilePath)

            val jsonData = JsonCompoundTag.compoundTagToJsonString(nbtData)

            FileWriter(outputFilePath).use { writer ->
                writer.write(jsonData)
            }

            println("Successfully converted $fileName to $outputFilePath")
        } catch (e: Exception) {
            println("Error converting file: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun readNbtFileWithQuerz(filePath: String): CompoundTag {
        val file = File(filePath)

        val namedTag: NamedTag = NBTUtil.read(file)

        return namedTag.tag as CompoundTag
    }

    fun convertToJsonString(fileName: String): String {
        val inputFilePath = fileName
        val nbtData = readNbtFileWithQuerz(inputFilePath)
        return JsonCompoundTag.compoundTagToJsonString(nbtData)
    }

    fun convertToFormattedJson(fileName: String, outputFilePath: String): String {
        try {
            val inputFilePath = fileName
            val nbtData = readNbtFileWithQuerz(inputFilePath)

            val mapper = ObjectMapper()
            val jsonNode = mapper.readTree(JsonCompoundTag.compoundTagToJsonString(nbtData))
            val prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode)

            FileWriter(outputFilePath).use { writer ->
                writer.write(prettyJson)
            }

            println("Successfully converted $fileName to formatted JSON at $outputFilePath")

            return prettyJson
        } catch (e: Exception) {
            println("Error converting file: ${e.message}")
            e.printStackTrace()
            return ""
        }
    }
}