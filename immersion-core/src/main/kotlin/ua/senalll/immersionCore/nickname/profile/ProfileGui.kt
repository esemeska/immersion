package ua.senalll.immersionCore.nickname.profile

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.nickname.NickName
import ua.senalll.immersionCore.nickname.NickNameType
import ua.senalll.immersionapi.component.darkGray
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import ua.senalll.immersionapi.component.resetComponent
import ua.senalll.immersionapi.component.white
import java.util.concurrent.CompletableFuture

object ProfileGui {
    val HEAD_SLOT: Int = 2
    val NAME_SLOT: Int = 6
    val keyNameStatus = NamespacedKey(ImmersionCore.instance, "name-status")

    fun isHeadSlot(slot: Int): Boolean{
        return slot == HEAD_SLOT
    }

    fun isNameSlot(slot: Int): Boolean{
        return slot == NAME_SLOT
    }

    fun openSettingsGui(p: Player) {
        val head = getPlayerHead(p)
        val nameFuture = getFullNameItem(p)
        nameFuture.thenApply{ nameItem ->
            Bukkit.getScheduler().runTask(ImmersionCore.instance, Runnable {
                val inventory = Bukkit.createInventory(ProfileInventoryHolder(), 9, "Профиль".darkGray())
                inventory.setItem(HEAD_SLOT, head)
                inventory.setItem(NAME_SLOT, nameItem)
                p.openInventory(inventory)
            })
        }
    }

    fun changeFullNameItem(p: Player, inventory: Inventory) {
        getFullNameItem(p).thenAccept { itemStack -> inventory.setItem(NAME_SLOT, itemStack) }
    }

    fun getFullNameItem(player: Player): CompletableFuture<ItemStack> {
        return NickName.getNickNameFromPlayer(player).thenCompose { nickName ->
            val lore = ArrayList<Component>()
            val name = nickName.name

            when (nickName.nickNameType) {
                NickNameType.FULLNAME -> {
                    val names = name.split(" ")
                    lore.add("Имя: ".darkGray())
                    lore.add("   ${names[0]}".gray())
                    lore.add("Фамилия: ".darkGray())
                    lore.add("   ${names[1]}".gray())
                    lore.add(Component.space())
                }
                NickNameType.NICKNAME -> {
                    lore.add("Имя: ".darkGray())
                    lore.add("   $name".gray())
                }
                NickNameType.NO_NICK -> {
                    lore.add("* нету имени *".darkGray().italic())
                }
            }

            val hasNoName = NickName.hasNoName(player).get()
            if (hasNoName) {
                lore.add(
                    "Нажми ".darkGray()
                    .append("ЛКМ,".gray())
                    .append(" чтобы обозначить своё полное имя.".darkGray())
                )
            }
            val settingItem = ItemStack(Material.WRITABLE_BOOK, 1)
            val itemMeta = settingItem.itemMeta
            itemMeta.displayName("Полное имя".white().resetComponent())
            itemMeta.lore(lore)
            settingItem.setItemMeta(itemMeta)
            CompletableFuture.completedFuture(settingItem)
        }
    }

    fun getPlayerHead(p: Player): ItemStack {
        val hasNoName = NickName.hasNoName(p).get()
        val lore = ArrayList<Component>()
        val playerHead = ItemStack(Material.PLAYER_HEAD, 1)
        val skullMeta = playerHead.itemMeta as SkullMeta
        skullMeta.owningPlayer = p
        playerHead.itemMeta = skullMeta

        val itemMeta = playerHead.itemMeta
        itemMeta.displayName("Профиль ${p.name}".white().resetComponent())

        if (!hasNoName) {
            NickName.getNameFromPlayer(p).thenApply { name ->
                lore.add("Полное имя: ".darkGray())
                lore.add("   $name".gray())
                itemMeta.lore(lore)
                playerHead.itemMeta = itemMeta
            }
        } else {
            lore.add("* нет имени *".gray().italic())
            itemMeta.lore(lore)
            playerHead.itemMeta = itemMeta
        }
        return playerHead
    }

}