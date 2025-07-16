package ua.senalll.immersionLitematica.old.complex

import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import ua.senalll.immersionLitematica.old.TBDScaleType
import ua.senalll.immersionapi.component.white

abstract class ComplexTextDisplay(
    protected val location: Location,
) {
    abstract val pixelData: PixelData

    abstract fun create()

    private val defaulColor = Color.fromARGB(100, 255, 0, 0)

    class PixelData(
        val scaleType: TBDScaleType,
        val pixelOffset: Float
    )

    fun createPixel(baseVector: Vector3f, pixelData: PixelData, x: Int, y:Int, cubeFace: CubeFace) {
        val pixel: TextDisplay = location.world.spawn(location.clone().subtract(.5, .5, .5), TextDisplay::class.java) {
            it.text(" ".white())
            it.billboard = Display.Billboard.FIXED
            it.backgroundColor = defaulColor
            it.brightness = Display.Brightness(15, 15)
            it.isShadowed = false
            it.isSeeThrough = false

            it.setRotation(cubeFace.yaw, cubeFace.pitch)

            val newVector = baseVector.clone() as Vector3f
            val transformation = Transformation(
                newVector.sub(-pixelData.pixelOffset * x, -pixelData.pixelOffset * y, .0f),
                AxisAngle4f(0f, 0f, 0f, 1f),
                pixelData.scaleType.scale,
                AxisAngle4f(0f, 0f, 0f, 1f)
            )
            it.transformation = transformation
        }
    }

}


