package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Location
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import ua.senalll.immersionLitematica.old.TBDScaleType
import kotlin.collections.iterator

class ComplexTest(
    location: Location,
): ComplexTextDisplay(location) {
    override val pixelData = PixelData(TBDScaleType.TEXTURE_PIXEL, 0.0625f)

    fun createCustom(faceMap: Map<Set<CubeFace>, Pair<Int, Int>>, topVector: Vector3f, frontVector: Vector3f, sideVector: Vector3f) {
        for ((faces, size) in faceMap) {
            val (width, height) = size

            for (cubeFace in faces) {
                for (x in 0 until width) {
                    for (y in 0 until height) {
                        var baseVector: Vector3f
                        if (mutableListOf(CubeFace.LEFT, CubeFace.RIGHT).contains(cubeFace)){
                            baseVector = sideVector
                        }else if (mutableListOf(CubeFace.TOP, CubeFace.BOTTOM).contains(cubeFace)){
                            baseVector = topVector
                        }else {
                            baseVector = frontVector
                        }
                        createPixel(baseVector, pixelData, x, y, cubeFace)
                    }
                }
            }
        }
    }

    override fun create() {
        //
    }
}