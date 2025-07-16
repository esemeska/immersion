package ua.senalll.immersionLitematica.display.serialize

import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector
import ua.senalll.immersionLitematica.display.BrickDisplay
import java.util.*

object BrickDisplaySerializer {

    fun serializeBrickDisplay(brickDisplay: BrickDisplay, schematicUUID: UUID): String {
        val dto = BrickDisplayDTO(
            location = brickDisplay.location,
            blockData = brickDisplay.blockData,
            structurePosition = brickDisplay.structurePosition,
            schematicUUID = schematicUUID,
            entityUUID = brickDisplay.entityUUID
        )
        return Jackson.objectMapper.writeValueAsString(dto)
    }

    fun deserializeBrickDisplay(json: String): BrickDisplay {
        val dto = Jackson.objectMapper.readValue(json, BrickDisplayDTO::class.java)
        val brickDisplay = BrickDisplay(
            location = dto.location,
            blockData = dto.blockData,
            structurePosition = dto.structurePosition
        )

        return brickDisplay
    }

    fun serializeBrickDisplays(brickDisplays: List<Pair<BrickDisplay, UUID>>): String {
        val dtoList = brickDisplays.map { (brickDisplay, schematicUUID) ->
            BrickDisplayDTO(
                location = brickDisplay.location,
                blockData = brickDisplay.blockData,
                structurePosition = brickDisplay.structurePosition,
                schematicUUID = schematicUUID,
                entityUUID = brickDisplay.entityUUID
            )
        }
        return Jackson.objectMapper.writeValueAsString(dtoList)
    }

    fun deserializeBrickDisplays(json: String): List<BrickDisplay> {
        val dtoList: List<BrickDisplayDTO> =
            Jackson.objectMapper.readValue(
                json,
                Jackson.objectMapper.typeFactory.constructCollectionType(
                    List::class.java,
                    BrickDisplaySerializer.BrickDisplayDTO::class.java
                )
            )

        return dtoList.map { dto ->
            val brickDisplay = BrickDisplay(
                location = dto.location,
                blockData = dto.blockData,
                structurePosition = dto.structurePosition
            )
            brickDisplay
        }
    }

    data class BrickDisplayDTO(
        val location: Location,
        val blockData: BlockData,
        val structurePosition: Vector,
        val schematicUUID: UUID,
        val entityUUID: UUID? = null
    )
}