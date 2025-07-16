package ua.senalll.immersionLitematica.old.resourcePack

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ua.senalll.immersionapi.util.Utils
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileReader
import java.io.InputStreamReader
import java.util.zip.ZipFile
import javax.imageio.ImageIO
import java.awt.Color
import java.util.logging.Level

object RpAnalyzer {
    private val TEXTURE_SIZE = 16

    private val blockColors = mutableMapOf<String, Array<IntArray>>()

    private val logger = Utils.createLogger<RpAnalyzer>()

    fun printBlockColorsMatrix(blockstateId: String) {
        val colors = blockColors[blockstateId]
        if (colors == null) {
            logger.warning("Нет цветов для blockstate: $blockstateId")
            return
        }

        logger.info("Цветовая матрица для $blockstateId:")
        for (y in 0 until TEXTURE_SIZE) {
            val row = StringBuilder()
            for (x in 0 until TEXTURE_SIZE) {
                val rgb = colors[y][x]
                val r = (rgb shr 16) and 0xFF
                val g = (rgb shr 8) and 0xFF
                val b = rgb and 0xFF

                val hex = String.format("#%02X%02X%02X", r, g, b)
                row.append("$hex ")
            }
            logger.info(row.toString())
        }
    }

    fun analyzeResourcePack(resourcePackDir: File) {
        if (resourcePackDir.isDirectory) {
            analyzeResourcePackDirectory(resourcePackDir)
        } else {
            logger.severe("Не удалось определить формат ресурспака: ${resourcePackDir.absolutePath}")
        }
    }

    private fun analyzeResourcePackDirectory(resourcePackDir: File) {
        val blockstatesDir = File(resourcePackDir, "assets/minecraft/blockstates")

        if (!blockstatesDir.exists() || !blockstatesDir.isDirectory) {
            logger.severe("Директория blockstates не найдена в ресурспаке")
            return
        }

        blockstatesDir.listFiles()?.filter { it.extension == "json" }?.forEach { blockstateFile ->
            try {
                val blockId = blockstateFile.nameWithoutExtension
                analyzeBlockState(blockId, FileReader(blockstateFile), resourcePackDir)
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Ошибка при обработке ${blockstateFile.name}", e)
            }
        }
    }


    private fun analyzeBlockState(blockId: String, reader: InputStreamReader, resourcePack: File) {
        try {
            val jsonElement = JsonParser.parseReader(reader)
            val jsonObject = jsonElement.asJsonObject

            if (jsonObject.has("variants")) {
                val variants = jsonObject.getAsJsonObject("variants")

                for ((variantName, variantValue) in variants.entrySet()) {
                    val variant = if (variantValue.isJsonArray) {
                        variantValue.asJsonArray.get(0).asJsonObject
                    } else {
                        variantValue.asJsonObject
                    }

                    if (variant.has("model")) {
                        val modelName = variant.get("model").asString
                        processModel(blockId, variantName, modelName, resourcePack)
                    }
                }
            } else {
                logger.info("Blockstate $blockId не содержит вариантов или имеет сложную структуру")
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Ошибка при анализе blockstate $blockId", e)
        }
    }

    private fun processModel(blockId: String, variantName: String, modelName: String, resourcePack: File) {
        val modelPath = "assets/minecraft/models/${modelName.replace("minecraft:", "")}.json"

        try {
            val modelJson = loadJsonFromResourcePack(resourcePack, modelPath)

            if (modelJson != null && modelJson.has("textures")) {
                val textures = modelJson.getAsJsonObject("textures")

                if (textures.has("all")) {
                    val textureId = textures.get("all").asString
                    val texturePath = "assets/minecraft/textures/${textureId.replace("minecraft:", "")}.png"

                    val textureColors = loadAndAnalyzeTexture(resourcePack, texturePath)

                    if (textureColors != null) {
                        val fullBlockstateId = if (variantName == "") blockId else "$blockId[$variantName]"
                        blockColors[fullBlockstateId] = textureColors
                        logger.info("Проанализирован блок $fullBlockstateId с текстурой $textureId")

                    }
                } else {
                    logger.fine("Модель $modelName не имеет текстуры 'all', пропускаем")
                }
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Ошибка при обработке модели $modelName", e)
        }
    }

    private fun loadJsonFromResourcePack(resourcePack: File, relativePath: String): JsonObject? {
        try {
            if (resourcePack.isDirectory) {
                val file = File(resourcePack, relativePath)
                if (file.exists()) {
                    FileReader(file).use { reader ->
                        return JsonParser.parseReader(reader).asJsonObject
                    }
                }
            } else if (resourcePack.extension.equals("zip", ignoreCase = true)) {
                ZipFile(resourcePack).use { zipFile ->
                    val entry = zipFile.getEntry(relativePath)
                    if (entry != null) {
                        zipFile.getInputStream(entry).use { inputStream ->
                            InputStreamReader(inputStream).use { reader ->
                                return JsonParser.parseReader(reader).asJsonObject
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Ошибка при загрузке JSON: $relativePath", e)
        }
        return null
    }


    private fun loadAndAnalyzeTexture(resourcePack: File, texturePath: String): Array<IntArray>? {
        try {
            val textureImage: BufferedImage

            if (resourcePack.isDirectory) {
                val file = File(resourcePack, texturePath)
                if (!file.exists()) {
                    logger.warning("Текстура не найдена: $texturePath")
                    return null
                }
                textureImage = ImageIO.read(file)
            } else {
                ZipFile(resourcePack).use { zipFile ->
                    val entry = zipFile.getEntry(texturePath)
                    if (entry == null) {
                        logger.warning("Текстура не найдена в ZIP: $texturePath")
                        return null
                    }
                    textureImage = ImageIO.read(zipFile.getInputStream(entry))
                }
            }

            if (textureImage.width != TEXTURE_SIZE || textureImage.height != TEXTURE_SIZE) {
                logger.warning("Текстура $texturePath имеет нестандартный размер: ${textureImage.width}x${textureImage.height}")
            }

            val pixelColors = Array(TEXTURE_SIZE) { IntArray(TEXTURE_SIZE) }

            for (y in 0 until TEXTURE_SIZE) {
                for (x in 0 until TEXTURE_SIZE) {
                    val rgb = if (x < textureImage.width && y < textureImage.height) {
                        textureImage.getRGB(x, y)
                    } else {
                        Color.MAGENTA.rgb
                    }
                    pixelColors[y][x] = rgb
                }
            }

            return pixelColors
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Ошибка при загрузке текстуры: $texturePath", e)
            return null
        }
    }

    fun getBlockColors(blockstateId: String): Array<IntArray>? {
        return blockColors[blockstateId]
    }

    fun getPixelColor(blockstateId: String, x: Int, y: Int): Int? {
        if (x < 0 || x >= TEXTURE_SIZE || y < 0 || y >= TEXTURE_SIZE) {
            return null
        }

        val colors = blockColors[blockstateId] ?: return null
        return colors[y][x]
    }
}