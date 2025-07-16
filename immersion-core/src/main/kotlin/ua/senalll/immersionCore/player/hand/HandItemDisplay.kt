package ua.senalll.immersionCore.player.hand

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ua.senalll.immersionCore.pdc.IsHandPDCHandler
import java.util.stream.Collectors

object HandItemDisplay {
    val DEFAULT_HAND_MATERIAL = Material.RABBIT_HIDE
    const val DEFAULT_HAND_Y: Int = -3

    fun spawnHand(player: Player): HandBuilder {
        return HandBuilder(player)
    }

    fun setupHandDisplay(itemDisplay: ItemDisplay, scale: Double = 1.2) {
        val transformation = itemDisplay.transformation
        transformation.scale.set(scale)
        itemDisplay.transformation = transformation
        itemDisplay.setItemStack(getDefaultHandItemStack())
        itemDisplay.isPersistent = true
        itemDisplay.isInvulnerable = true
        itemDisplay.billboard = Display.Billboard.FIXED
    }

    fun getDefaultHandItemStack(): ItemStack {
        val itemStack = ItemStack(DEFAULT_HAND_MATERIAL, 1)
        val itemMeta = itemStack.itemMeta
        if (itemMeta != null) {
            itemMeta.setCustomModelData(555)
            itemStack.itemMeta = itemMeta
        }
        return itemStack
    }

    fun getNearbyHands(f: Entity): List<ItemDisplay> {
        return f.getNearbyEntities(10.0, 10.0, 10.0).parallelStream()
            .filter { IsHandPDCHandler.isHand(it) }
            .filter { entity -> entity is ItemDisplay }
            .map { entity -> entity as ItemDisplay }
            .collect(Collectors.toList())
    }

    fun Location.handLoc(): Location {
        return this.subtract(0.0, DEFAULT_HAND_Y.toDouble(), 0.0)
    }

    class HandBuilder(private val player: Player) {
        private var scale: Double = 1.2
        private var yOffset: Double = DEFAULT_HAND_Y.toDouble()

        fun withScale(scale: Double): HandBuilder {
            this.scale = scale
            return this
        }

        fun execute(callback: (ItemDisplay) -> Unit) {
            val loc = player.location.clone()
            val world = loc.world ?: return

            world.spawn(loc.clone().subtract(0.0, yOffset, 0.0), ItemDisplay::class.java) { itemDisplay ->
                setupHandDisplay(itemDisplay, scale)
                callback(itemDisplay)
            }
        }
    }
}

