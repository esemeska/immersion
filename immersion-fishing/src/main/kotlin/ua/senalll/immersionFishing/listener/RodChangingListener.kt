package ua.senalll.immersionFishing.listener

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import ua.senalll.immersionFishing.item.rod.RodItem
import ua.senalll.immersionFishing.item.rod.RodUtils

object RodChangingListener: Listener {

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        val cursor = e.cursor
        val currentItem = e.currentItem
        if (e.isLeftClick || e.clickedInventory == null || currentItem == null || currentItem.type != Material.FISHING_ROD) return

        if (e.action == InventoryAction.SWAP_WITH_CURSOR && RodUtils.isBait(cursor)) {
            handleAddingBaitToRod(e)
        } else if (e.action == InventoryAction.PICKUP_HALF && RodUtils.isBaitedRod(currentItem)) {
           handleRemovingBaitFromRod(e)
        }
    }

    fun handleAddingBaitToRod(e: InventoryClickEvent){
        e.isCancelled = true
        val baitAmount = e.cursor.amount
        val notAppliedBaits = RodItem.processAddBaitToRod(e.currentItem!!, baitAmount)
        e.cursor.amount = notAppliedBaits
    }

    fun handleRemovingBaitFromRod(e: InventoryClickEvent){
        e.isCancelled = true
        val baitFromRodCount = RodItem.processRemoveBaitFromRod(e.currentItem!!)
        e.whoClicked.inventory.addItem(RodUtils.baitItemInCount(baitFromRodCount))
    }
}