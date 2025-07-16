package ua.senalll.immersionLitematica.schematic.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.data.BlockData
import org.bukkit.block.data.Directional
import java.io.File
import java.util.UUID
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.max

class JsonSchematic(
    val name: String?,
    val sizeX: Int,
    val sizeY: Int,
    val sizeZ: Int
) {
    private var author: UUID? = null
    private var timeCreated: Long = 0
    private var timeModified: Long = 0
    private var totalBlocks = 0
    private var totalVolume = 0

    private val palette: MutableMap<Int?, BlockData?> = HashMap()
    private val blocks: Array<Array<IntArray?>?> = Array(sizeX) {
        Array(sizeY) {
            IntArray(
                sizeZ
            )
        }
    }

    companion object {
        fun fromJson(jsonString: String): JsonSchematic? {
            try {
                val mapper = ObjectMapper()
                val rootNode = mapper.readTree(jsonString)

                val metadataNode = rootNode.get("Metadata")
                val name = metadataNode.get("Name").asText().replace("\"", "")
                val sizeNode = metadataNode.get("EnclosingSize")
                val sizeX = sizeNode.get("x").asInt()
                val sizeY = sizeNode.get("y").asInt()
                val sizeZ = sizeNode.get("z").asInt()

                val schematic = JsonSchematic(name, sizeX, sizeY, sizeZ)

                //schematic.setAuthor(metadataNode.get("Author").asText().replace("\"", ""))
                schematic.setTimeCreated(metadataNode.get("TimeCreated").asLong())
                schematic.setTimeModified(metadataNode.get("TimeModified").asLong())
                schematic.setTotalBlocks(metadataNode.get("TotalBlocks").asInt())
                schematic.setTotalVolume(metadataNode.get("TotalVolume").asInt())

                val regionsNode = rootNode.get("Regions")
                if (regionsNode != null && regionsNode.size() > 0) {
                    val regionName = regionsNode.fieldNames().next()
                    val region = regionsNode.get(regionName)

                    val paletteNode = region.get("BlockStatePalette")
                    for (i in 0 until paletteNode.size()) {
                        val blockStateNode = paletteNode.get(i)
                        val blockName = blockStateNode.get("Name").asText().replace("\"", "")

                        var blockData: BlockData? = null
                        try {
                            if (blockName == "minecraft:water") {
                                if (blockStateNode.has("Properties") &&
                                    blockStateNode.get("Properties").has("level")) {
                                    val level = blockStateNode.get("Properties").get("level").asText().replace("\"", "")
                                    if (level == "0") {
                                        val material = Material.valueOf("WATER")
                                        blockData = Bukkit.createBlockData(material)
                                    } else {
                                        blockData = Bukkit.createBlockData(Material.AIR)
                                    }
                                } else {
                                    val material = Material.valueOf("WATER")
                                    blockData = Bukkit.createBlockData(material)
                                }
                            } else {
                                val material = Material.valueOf(blockName.replace("minecraft:", "").uppercase())
                                blockData = Bukkit.createBlockData(material)

                                if (blockStateNode.has("Properties")) {
                                    val propsNode = blockStateNode.get("Properties")
                                    val propsIterator = propsNode.fields()
                                    while (propsIterator.hasNext()) {
                                        val prop = propsIterator.next()
                                        val propName = prop.key
                                        val propValue = prop.value.asText().replace("\"", "")

                                        if (blockData is Directional && propName == "facing") {
                                            blockData.facing = BlockFace.valueOf(propValue.uppercase())
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            blockData = Bukkit.createBlockData(Material.AIR)
                        }

                        schematic.addToPalette(i, blockData)
                    }

                    val blockStates = region.get("BlockStates").map { it.asLong() }.toLongArray()
                    val size = region.get("Size")
                    val regionSizeX = size.get("x").asInt()
                    val regionSizeY = size.get("y").asInt()
                    val regionSizeZ = size.get("z").asInt()

                    val paletteSize = paletteNode.size()
                    val bitsPerBlock = max(2, ceil(log2(paletteSize.toDouble())).toInt())

                    decodeBlockStates(schematic, blockStates, bitsPerBlock, regionSizeX, regionSizeY, regionSizeZ)
                }

                return schematic
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

        private fun decodeBlockStates(
            schematic: JsonSchematic,
            blockStates: LongArray,
            bitsPerBlock: Int,
            sizeX: Int,
            sizeY: Int,
            sizeZ: Int
        ) {
            val totalBlocks = sizeX * sizeY * sizeZ
            val totalBits = totalBlocks * bitsPerBlock
            val requiredLongs = (totalBits + 63) / 64

            if (blockStates.size < requiredLongs) {
                throw IllegalArgumentException(
                    "Not enough data in blockStates array. Needed $requiredLongs longs, but got ${blockStates.size}"
                )
            }

            val mask = (1L shl bitsPerBlock) - 1
            var bitPosition = 0
            var currentIndex = 0

            for (y in 0 until sizeY) {
                for (z in 0 until sizeZ) {
                    for (x in 0 until sizeX) {
                        val longIndex = bitPosition / 64
                        val startBit = bitPosition % 64

                        var value: Long

                        if (startBit + bitsPerBlock <= 64) {
                            value = (blockStates[longIndex] shr startBit) and mask
                        } else {
                            val bitsFromFirst = 64 - startBit
                            val bitsFromSecond = bitsPerBlock - bitsFromFirst

                            value = (blockStates[longIndex] ushr startBit)

                            if (longIndex + 1 < blockStates.size) {
                                value = (blockStates[longIndex] ushr startBit) or
                                        ((blockStates[longIndex + 1] and ((1L shl bitsFromSecond) - 1)) shl bitsFromFirst)
                            }

                            value = value and mask
                        }

                        schematic.setBlockAt(x, y, z, value.toInt())
                        bitPosition += bitsPerBlock
                        currentIndex++
                    }
                }
            }
        }

        fun fromJsonFile(filePath: String): JsonSchematic? {
            return try {
                val content = File(filePath).readText()
                fromJson(content)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        fun fromLitematic(): JsonSchematic? {
            val converter = SchematicaConverter()
            val jsonString = converter.convertToFormattedJson(litematicFileName, litematicFilePath)
            return fromJson(jsonString)
        }
    }

    fun getAuthor(): UUID? = author

    fun getTimeCreated(): Long = timeCreated

    fun getTimeModified(): Long = timeModified

    fun getTotalBlocks(): Int = totalBlocks

    fun getTotalVolume(): Int = totalVolume

    fun getPalette(): Map<Int?, BlockData?> = palette

    fun getBlocks3D(): Array<Array<IntArray?>?> = blocks


    fun setBlockAt(x: Int, y: Int, z: Int, paletteIndex: Int) {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY && z >= 0 && z < sizeZ) {
            blocks[x]!![y]!![z] = paletteIndex
        }
    }

    fun getBlockAt(x: Int, y: Int, z: Int): Int {
        if (x >= 0 && x < sizeX && y >= 0 && y < sizeY && z >= 0 && z < sizeZ) {
            return blocks[x]!![y]!![z]
        }
        return -1
    }

    fun getBlockDataAt(x: Int, y: Int, z: Int): BlockData? {
        val index = getBlockAt(x, y, z)
        if (index >= 0 && palette.containsKey(index)) {
            return palette[index]
        }
        return Bukkit.createBlockData(Material.AIR)
    }

    fun addToPalette(index: Int, blockData: BlockData?) {
        palette.put(index, blockData)
    }

    fun setAuthor(author: UUID?) {
        this.author = author
    }

    fun setTimeCreated(timeCreated: Long) {
        this.timeCreated = timeCreated
    }

    fun setTimeModified(timeModified: Long) {
        this.timeModified = timeModified
    }

    fun setTotalBlocks(totalBlocks: Int) {
        this.totalBlocks = totalBlocks
    }

    fun setTotalVolume(totalVolume: Int) {
        this.totalVolume = totalVolume
    }
}