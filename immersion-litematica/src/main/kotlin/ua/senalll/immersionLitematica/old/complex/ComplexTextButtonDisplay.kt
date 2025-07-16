package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Location
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import ua.senalll.immersionLitematica.old.TBDScaleType

class ComplexTextButtonDisplay(
    location: Location,
): ComplexTextDisplay(location) {
    override val pixelData = PixelData(TBDScaleType.TEXTURE_PIXEL, 0.0625f)

    fun createCustom(topVector: Vector3f, frontVector: Vector3f, sideVector: Vector3f) {
        for (cubeFace in listOf(CubeFace.TOP)) {
            for (x in 0 until 6) {
                for (y in 0 until 4) {
                    createPixel(
                        topVector,
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }
        for (cubeFace in listOf(CubeFace.FRONT, CubeFace.BACK)) {
            for (x in 0 until 6) {
                for (y in 0 until 2) {
                    createPixel(
                        frontVector,
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }

        for (cubeFace in listOf(CubeFace.LEFT, CubeFace.RIGHT)) {
            for (x in 0 until 4) {
                for (y in 0 until 2) {
                    createPixel(
                        sideVector,
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }
    }


    override fun create() {
        for (cubeFace in listOf(CubeFace.TOP)) {
            for (x in 0 until 6) {
                for (y in 0 until 4) {
                    createPixel(
                        Vector3f(-pixelData.pixelOffset*3f+.025f, -pixelData.pixelOffset*2f, -.5f+(pixelData.pixelOffset*2)+.05f),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }
        for (cubeFace in listOf(CubeFace.FRONT, CubeFace.BACK)) {
            for (x in 0 until 6) {
                for (y in 0 until 2) {
                    createPixel(
                        Vector3f(-pixelData.pixelOffset*3f+.025f, -.5f, pixelData.pixelOffset*2f),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }

        for (cubeFace in listOf(CubeFace.LEFT, CubeFace.RIGHT)) {
            for (x in 0 until 4) {
                for (y in 0 until 2) {
                    createPixel(
                        Vector3f(-pixelData.pixelOffset*2f+.025f, -.5f, pixelData.pixelOffset*3f),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }
    }
}