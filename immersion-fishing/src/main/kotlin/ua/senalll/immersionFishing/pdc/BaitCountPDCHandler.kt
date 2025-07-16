package ua.senalll.immersionFishing.pdc

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import ua.senalll.immersionFishing.ImmersionFishing

object BaitCountPDCHandler {
    val key: NamespacedKey = NamespacedKey(ImmersionFishing.instance, "bait_count")
    val type: PersistentDataType<Int, Int> = PersistentDataType.INTEGER

    fun getBaitCount(itemStack: ItemStack): Int {
        val meta = itemStack.itemMeta ?: return 0
        val container = meta.persistentDataContainer
        return container.get(key, type) ?: 0
    }

    fun setBaitCount(itemStack: ItemStack, count: Int) {
        val meta = itemStack.itemMeta ?: return
        val container = meta.persistentDataContainer
        container.set(key, type, count)
        itemStack.itemMeta = meta
    }

    fun hasBait(itemStack: ItemStack): Boolean {
        val meta = itemStack.itemMeta ?: return false
        return meta.persistentDataContainer.has(key, type)
    }

    fun removeBaitData(itemStack: ItemStack) {
        val meta = itemStack.itemMeta ?: return
        val container = meta.persistentDataContainer
        container.remove(key)
        itemStack.itemMeta = meta
    }
}