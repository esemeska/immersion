package ua.senalll.immersionFishing.item.rod

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import ua.senalll.immersionFishing.Utils

object RodUtils {
    val BAIT_MATERIAL = Material.ROTTEN_FLESH
    const val BAITED_ROD_MODEL_DATA = 2

    fun getPlayerRod(player: Player): ItemStack {
        return player.inventory.getItem(getPlayerRodSlot(player))!!
    }

    fun getPlayerRodSlot(player: Player): Int{
        val main = player.inventory.itemInMainHand
        if (main.type != Material.AIR) return player.inventory.heldItemSlot
        return 40
    }

    fun makeRodBaited(itemStack: ItemStack){
        val itemMeta = itemStack.itemMeta
        itemMeta.setCustomModelData(BAITED_ROD_MODEL_DATA)
        itemStack.itemMeta = itemMeta
    }

    fun makeRodDefault(itemStack: ItemStack){
        val itemMeta = itemStack.itemMeta
        itemMeta.setCustomModelData(null)
        itemStack.itemMeta = itemMeta
    }

    fun updateFishingRodDurability(rod: ItemStack) {
        val meta = rod.itemMeta
        if (meta !is Damageable) return
        meta.damage = meta.damage + 1
        rod.itemMeta = meta
    }

    fun baitItemInCount(count: Int): ItemStack{
        return ItemStack(BAIT_MATERIAL, count)
    }

    fun isBait(item: ItemStack?): Boolean {
        return !(item == null || item.type != BAIT_MATERIAL)
    }

    fun isBaitedRod(itemStack: ItemStack): Boolean{
        if (itemStack.type != Material.FISHING_ROD) return false
        return Utils.hasCustomModelData(itemStack, BAITED_ROD_MODEL_DATA)
    }
}