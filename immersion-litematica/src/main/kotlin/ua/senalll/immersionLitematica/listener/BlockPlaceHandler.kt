package ua.senalll.immersionLitematica.listener

import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.block.Block
import org.bukkit.entity.BlockDisplay
import org.bukkit.entity.Display
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.util.Transformation
import org.joml.AxisAngle4f
import org.joml.Vector3f
import ua.senalll.immersionLitematica.ImmersionLitematica
import java.util.HashMap

object BlockPlaceHandler : Listener {

    private val displayMap = HashMap<Block, BlockDisplay>()


    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val placedBlock = event.blockPlaced

        val location = placedBlock.location.clone()
        val nearbyEntities = location.world.getNearbyEntities(location, 0.5, 0.5, 0.5)

        println("onBlockPlace")
        for (entity in nearbyEntities) {
            println(nearbyEntities)
            if (entity is BlockDisplay) {
                println(entity)
                val displayBlock = entity.block
                val placedBlockData = placedBlock.blockData

                removeResultDisplay(placedBlock)
                println(displayBlock)
                println(displayBlock.material)
                println(placedBlockData)
                println(placedBlockData.material)
                if (displayBlock.material == placedBlockData.material) {
                    createResultDisplay(placedBlock, true)
                } else {
                    createResultDisplay(placedBlock, false)
                }

                break
            }
        }
    }

    private fun createResultDisplay(block: Block, isCorrect: Boolean) {
        val location = block.location.clone()
        val world = location.world

        val display = world.spawn(location, BlockDisplay::class.java)
        display.block = block.blockData

        val scale = 1.05f
        val transformation = Transformation(
            Vector3f(0f, 0f, 0f),
            AxisAngle4f(0f, 0f, 0f, 0f),
            Vector3f(scale, scale, scale),
            AxisAngle4f(0f, 0f, 0f, 0f)
        )
        display.transformation = transformation

        if (isCorrect) {
            display.glowColorOverride = Color.fromRGB(0, 255, 0).setAlpha(100)
        } else {
            display.glowColorOverride = Color.fromRGB(255, 0, 0).setAlpha(100)
        }

        display.brightness = Display.Brightness(10, 10)
        display.viewRange = 64.0f


        displayMap[block] = display

        Bukkit.getScheduler().runTaskLater(ImmersionLitematica.instance, Runnable {
            if (displayMap.containsKey(block)) {
                removeResultDisplay(block)
            }
        }, 100L)
    }

    private fun removeResultDisplay(block: Block) {
        val display = displayMap.remove(block)
        display?.remove()
    }

    fun cleanup() {
        for (display in displayMap.values) {
            display.remove()
        }
        displayMap.clear()
    }
}