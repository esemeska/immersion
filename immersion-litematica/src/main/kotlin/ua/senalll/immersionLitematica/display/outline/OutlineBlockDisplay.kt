package ua.senalll.immersionLitematica.display.outline

import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import ua.senalll.immersionLitematica.ImmersionLitematica
import ua.senalll.immersionLitematica.old.TBDScaleType
import ua.senalll.immersionLitematica.pdc.OutlinePDCHandler
import ua.senalll.immersionapi.component.white
import java.util.UUID

class OutlineBlockDisplay(
    private val location: Location,
    private var outlineType: OutlineType = OutlineType.DEFAULT,
    private val uuid: UUID = UUID.randomUUID()
) {
    private val displays = mutableListOf<TextDisplay>()

    val key = NamespacedKey(ImmersionLitematica.Companion.instance, "outline_face")

    fun createTBD(){
        for (cubeFace in CubeFace.entries){
            createFace(cubeFace)
        }
    }

    fun createFace(cubeFace: CubeFace){
        val face: TextDisplay = location.world.spawn(location.clone().subtract(.5, .5, .5), TextDisplay::class.java)
        setOutlineSettings(face, cubeFace)

        OutlinePDCHandler.setOutlineUUID(face, uuid)

        face.teleport(face.location.subtract(-1.0,-1.0,-1.0))
        displays.add(face)
    }

    fun setOutlineSettings(textDisplay: TextDisplay, cubeFace: CubeFace){
        textDisplay.text(" ".white())
        textDisplay.billboard = Display.Billboard.FIXED
        textDisplay.backgroundColor = outlineType.color
        textDisplay.brightness = outlineType.brightness
        textDisplay.isShadowed = false
        textDisplay.isSeeThrough = false
        textDisplay.setRotation(cubeFace.yaw, cubeFace.pitch)

        val transformation = Transformation(
            Vector3f(-.1f, -.5f, .502f),
            AxisAngle4f(0f, 0f, 0f, 1f),
            TBDScaleType.BLOCK.scale,
            AxisAngle4f(0f, 0f, 0f, 1f)
        )
        textDisplay.transformation = transformation;
        textDisplay.persistentDataContainer.set(key, PersistentDataType.STRING, uuid.toString())
    }

    fun setOutlineType(newOutlineType: OutlineType){
        this.outlineType = newOutlineType
    }

}