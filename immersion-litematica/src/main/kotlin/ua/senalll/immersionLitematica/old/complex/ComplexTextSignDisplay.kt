package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Location
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import ua.senalll.immersionLitematica.old.TBDScaleType

class ComplexTextSignDisplay(
    location: Location,
): ComplexTextDisplay(location) {
    override val pixelData = PixelData(TBDScaleType.SIGN, 0.042f)

    override fun create() {
        for (cubeFace in CubeFace.getSide()){
            for (x in 0 until 2) {
                for (y in 0 until 14) {
                    createPixel(
                        Vector3f(-.025f, -.5f, pixelData.pixelOffset+.001f),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }

        for (cubeFace in listOf(CubeFace.FRONT, CubeFace.BACK)){
            for (x in 0 until 24) {
                for (y in 0 until 12) {
                    createPixel(
                        Vector3f(-.477f, -.5f+14*pixelData.pixelOffset-.01f, pixelData.pixelOffset+.001f),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }

        for (cubeFace in listOf(CubeFace.LEFT, CubeFace.RIGHT)){
            for (x in 0 until 2) {
                for (y in 0 until 12) {
                    createPixel(
                        Vector3f(-.025f, -.5f+14*pixelData.pixelOffset-.01f, pixelData.pixelOffset*12),
                        pixelData,
                        x,
                        y,
                        cubeFace
                    )
                }
            }
        }

        for (cubeFace in listOf(CubeFace.TOP, CubeFace.BOTTOM)){
            for (x in 0 until 24) {
                for (y in 0 until 2) {
                    var baseVector = Vector3f(-.5f, -pixelData.pixelOffset, .58f)
                    if (cubeFace == CubeFace.BOTTOM){
                        baseVector = Vector3f(-.5f, -pixelData.pixelOffset, -.08f)
                    }
                    createPixel(
                        baseVector,
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
