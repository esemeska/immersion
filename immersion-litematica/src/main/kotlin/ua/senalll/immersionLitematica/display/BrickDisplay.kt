package ua.senalll.immersionLitematica.display

import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.serialize.BrickDisplaySerializer
import ua.senalll.immersionLitematica.old.TBDScaleType
import ua.senalll.immersionLitematica.pdc.SchematicDataPDCHandler
import ua.senalll.immersionLitematica.pdc.SchematicUUIDPDCHandler
import java.util.UUID
import kotlin.system.exitProcess

class BrickDisplay (
    val location: Location,
    val blockData: BlockData,
    val structurePosition: Vector
){
    lateinit var entityUUID: UUID

    fun createBrick(schematicUUID: UUID): BlockDisplay {
        val blockDisplay = location.world.spawn(location.clone(), BlockDisplay::class.java){
            it.brightness = Display.Brightness(15, 15)
            it.isVisibleByDefault = true
            it.billboard = Display.Billboard.FIXED
            it.block = blockData
            it.isPersistent = true
            it.isInvulnerable = true
            it.transformation = Transformation(
                Vector3f(),
                AxisAngle4f(),
                Vector3f(0.98f),
                AxisAngle4f()
            )
        }

        entityUUID = blockDisplay.uniqueId
        SchematicUUIDPDCHandler.setSchematicUUID(blockDisplay, schematicUUID)
        SchematicDataPDCHandler.setSchematicData(blockDisplay, structurePosition)

        return blockDisplay
    }
}