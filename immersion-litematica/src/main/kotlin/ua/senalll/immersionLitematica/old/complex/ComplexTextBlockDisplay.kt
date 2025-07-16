package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Location
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import ua.senalll.immersionLitematica.old.TBDScaleType

class ComplexTextBlockDisplay (
    location: Location
): ComplexTextDisplay(location) {
    override val pixelData = PixelData(TBDScaleType.TEXTURE_PIXEL, 0.0625f)

    override fun create(){
        for (cubeFace in CubeFace.entries){
            createComplexFace(cubeFace)
        }
    }

    fun createComplexFace(cubeFace: CubeFace){
        for (x in 0 until 16) {
            for (y in 0 until 16) {
                createPixel(
                    Vector3f(-.473f, -.5f, .502f),
                    pixelData,
                    x,
                    y,
                    cubeFace
                )
            }
        }
    }

}