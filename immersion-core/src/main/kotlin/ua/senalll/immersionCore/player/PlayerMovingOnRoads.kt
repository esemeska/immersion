package ua.senalll.immersionCore.player

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import ua.senalll.immersionCore.CoreUtils

class PlayerMovingOnRoads {
    private val roadMaterials = setOf(
        // Mud brick types
        Material.MUD_BRICKS, Material.MUD_BRICK_SLAB, Material.MUD_BRICK_STAIRS,
        // Andesite types
        Material.POLISHED_ANDESITE, Material.POLISHED_ANDESITE_SLAB, Material.POLISHED_ANDESITE_STAIRS,
        Material.ANDESITE, Material.ANDESITE_SLAB, Material.ANDESITE_STAIRS,
        // Cobblestone types
        Material.COBBLESTONE, Material.COBBLESTONE_SLAB, Material.COBBLESTONE_STAIRS,
        Material.MOSSY_COBBLESTONE, Material.MOSSY_COBBLESTONE_SLAB, Material.MOSSY_COBBLESTONE_STAIRS,
        // Stone types
        Material.STONE_BRICKS, Material.STONE_BRICK_SLAB, Material.STONE_BRICK_STAIRS,
        Material.STONE, Material.STONE_SLAB, Material.STONE_STAIRS,
        // Deepslate types
        Material.POLISHED_DEEPSLATE, Material.POLISHED_DEEPSLATE_SLAB, Material.POLISHED_DEEPSLATE_STAIRS,
        Material.DEEPSLATE_BRICKS, Material.DEEPSLATE_BRICK_SLAB, Material.DEEPSLATE_BRICK_STAIRS,
        Material.COBBLED_DEEPSLATE, Material.COBBLED_DEEPSLATE_SLAB, Material.COBBLED_DEEPSLATE_STAIRS,
        Material.DEEPSLATE_TILES, Material.DEEPSLATE_TILE_SLAB, Material.DEEPSLATE_TILE_STAIRS,
        // Brick types
        Material.BRICKS, Material.BRICK_SLAB, Material.BRICK_STAIRS
    )

    companion object {
        const val DEFAULT_SPEED = 0.2f
        const val PATH_SPEED = 0.24f
        const val ROAD_SPEED = 0.27f
    }

    @EventHandler
    fun onMove(e: PlayerMoveEvent) {
        if (!CoreUtils.isPlayerMoving(e)) return
        updatePlayerSpeed(e)
    }


    private fun updatePlayerSpeed(e: PlayerMoveEvent) {
        val player = e.player
        val standingBlock = e.to.block

        when (standingBlock.type) {
            Material.DIRT_PATH -> {
                player.walkSpeed = PATH_SPEED
                return
            }
            Material.AIR -> {
                if (isOnRoad(e.to)) {
                    player.walkSpeed = ROAD_SPEED
                    return
                }
            }
            else -> {
                player.walkSpeed = DEFAULT_SPEED
                return
            }
        }
    }

    private fun isOnRoad(location: Location): Boolean {
        val blockBelow = location.clone().subtract(0.0, 1.0, 0.0).block

        if (!roadMaterials.contains(blockBelow.type)) return false

        val adjacentOffsets = arrayOf(
            Vector(1, 0, 0),
            Vector(-1, 0, 0),
            Vector(0, 0, 1),
            Vector(0, 0, -1)
        )

        val adjacentTypes = adjacentOffsets.map { offset ->
            blockBelow.getRelative(offset.blockX, offset.blockY, offset.blockZ).type
        }

        if (!adjacentTypes.all { roadMaterials.contains(it) }) return false

        return adjacentTypes.any { it != blockBelow.type }
    }
}