package ua.senalll.immersionCore.nickname.profile

import org.bukkit.Bukkit
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import ua.senalll.immersionapi.component.darkGray

class ProfileInventoryHolder : InventoryHolder {

    private var inventory = Bukkit.createInventory(null, 9, "Профиль".darkGray())

    override fun getInventory(): Inventory {
        return inventory
    }
}