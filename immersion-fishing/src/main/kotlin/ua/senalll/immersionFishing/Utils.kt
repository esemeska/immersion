package ua.senalll.immersionFishing

import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.inventory.meta.ItemMeta

object Utils {

    fun hasCustomModelData(item: ItemStack, modelData: Int): Boolean {
        return item.hasItemMeta() && item.itemMeta.hasCustomModelData()
                && item.itemMeta.customModelData == modelData
    }

    fun transferLore(fromMeta: ItemMeta, toMeta: ItemMeta) {
        val lore = fromMeta.lore()
        toMeta.lore(lore ?: ArrayList())
    }

    fun transferDamage(fromMeta: ItemMeta, toMeta: ItemMeta) {
        if (fromMeta is Damageable && toMeta is Damageable) {
            toMeta.damage = fromMeta.damage
        }
    }

    fun transferEnchantments(fromMeta: ItemMeta, toMeta: ItemMeta) {
        fromMeta.enchants.forEach { (enchantment, level) ->
            toMeta.addEnchant(enchantment, level, true)
        }
    }
}