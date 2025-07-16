package ua.senalll.immersionLitematica.schematic

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.entity.BlockDisplay
import org.bukkit.util.Transformation
import org.bukkit.util.Vector
import org.joml.AxisAngle4f
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.BrickDisplay
import ua.senalll.immersionLitematica.schematic.json.JsonSchematic
import ua.senalll.immersionLitematica.display.outline.OutlineBlockDisplay
import java.util.UUID

class SchematicVisualizer(
    private val world: World,
    private val schematic: JsonSchematic,
    private val schematicUUID: UUID = UUID.randomUUID()
) {

    private val blockDisplays = mutableListOf<BlockDisplay>()

    fun visualize(originX: Int = 10, originY: Int = 150, originZ: Int = 0) {
        clearOldVisualizations()
        val sizeX = schematic.sizeX
        val sizeY = schematic.sizeY
        val sizeZ = schematic.sizeZ

        for (x in 0 until sizeX) {
            for (y in 0 until sizeY) {
                for (z in 0 until sizeZ) {
                    if (x == 0 && y == 0 && z == 0){
                        val blockData: BlockData? = schematic.getBlockDataAt(x, y, z)
                        if (blockData != null && blockData.material != Material.AIR) {
                            val location = Location(
                                world,
                                (originX + x).toDouble(),
                                (originY + y).toDouble(),
                                (originZ + z).toDouble()
                            )

                            val brick = BrickDisplay(location, blockData, Vector(x,y,z))
                            val blockDisplay = brick.createBrick(schematicUUID)
                            blockDisplays.add(blockDisplay)

                            val outline = OutlineBlockDisplay(location)
                            outline.createTBD()
                        }
                    }
                }
            }
        }

        //scaleVisualization(1f)

    }

    fun clearOldVisualizations() {
        blockDisplays.forEach { it.remove() }
        blockDisplays.clear()

        world.entities
            .filterIsInstance<BlockDisplay>()
            .forEach { it.remove() }
    }

    fun scaleVisualization(scale: Float) {
        val scaleTransform = Transformation(
            Vector3f(0.25f, 0.25f, 0.25f),
            AxisAngle4f(0f, 0f, 0f, 0f),
            Vector3f(scale, scale, scale),
            AxisAngle4f(0f, 0f, 0f, 0f)
        )

        blockDisplays.forEach { it.transformation = scaleTransform }
    }
}