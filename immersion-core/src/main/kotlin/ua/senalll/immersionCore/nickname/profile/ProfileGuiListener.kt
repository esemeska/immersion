package ua.senalll.immersionCore.nickname.profile

import io.papermc.paper.event.player.AsyncChatEvent
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import ua.senalll.immersionCore.database.NickNameTableHandler
import ua.senalll.immersionCore.nickname.NameUpdater
import ua.senalll.immersionCore.nickname.NickName
import ua.senalll.immersionCore.pdc.NickNamePDCHandler
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.red
import ua.senalll.immersionapi.component.white

class ProfileGuiListener(private val nicknamePDC: NickNamePDCHandler) : Listener {

    companion object {
        fun onAsyncPreLogin(e: AsyncPlayerPreLoginEvent) {
            if (NickNameTableHandler.isPlayerExist(e.uniqueId)) return
            NickName.initFirstLogin(e.uniqueId)
        }
    }

    @EventHandler
    fun onPlayerInteractLectern(e: PlayerInteractEvent) {
        if (!e.action.isRightClick) return
        if (e.clickedBlock == null) return
        if (!e.player.isSneaking) return
        if ((e.clickedBlock as Block).type != Material.LECTERN) return
        if (e.player.inventory.itemInMainHand.type != Material.AIR) return
        ProfileGui.openSettingsGui(e.player)
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.clickedInventory == null) return
        if (e.clickedInventory?.holder !is ProfileInventoryHolder) return
        if (e.currentItem == null || !(e.currentItem as ItemStack).hasItemMeta()) return

        if (ProfileGui.isHeadSlot(e.slot)) {
            e.isCancelled = true
        } else if (ProfileGui.isNameSlot(e.slot)) {
            e.isCancelled = true

            val hasNoName = NickName.hasNoName(e.whoClicked as Player).get()
            if (!hasNoName){
                ProfileGui.changeFullNameItem(e.whoClicked as Player, e.clickedInventory as Inventory)
                return
            }

            val p = e.whoClicked as Player

            p.closeInventory()
            nicknamePDC.setChangingNickName(p, true)
            p.sendMessage(
                ("Напиши своё полное имя в одной из таких форм: ".gray()
                    .appendNewline()
                    .append("Фамилия Имя".white())
                    .appendNewline()
                    .append("Никнейм".white()))
            )
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    fun onPlayerChat(e: AsyncChatEvent) {
        val p = e.player
        if (!nicknamePDC.isChangingNickName(p)) return
        val name = e.message().plainText()

        val nickNameType = NickName.getNameFormat(name)

        if (nickNameType != null) {
            nicknamePDC.setChangingNickName(p, false)
            val playerNickName = NickName(p, name, nickNameType)
            playerNickName.savePlayerNickName()
            p.sendMessage(
                (
                    "Поздравляю, теперь ты ".gray()
                        .append(name.white())
                        .append(". Хотя, скорее соболезную...".gray())
                )
            )
            NameUpdater.updateToAll(p)
        } else {
            p.sendMessage("Ты неправильно заполнин форму! Попробуй ещё раз ".  red())
        }
    }

    @EventHandler
    fun onPlayerDead(e: PlayerDeathEvent) {
        val p = e.player
        if (!nicknamePDC.isChangingNickName(p)) return
        p.sendMessage(("Ой, ты умер. Давай потом тогда, напишешь имя.".gray()))
        nicknamePDC.setChangingNickName(p, false)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent) {
        if (!nicknamePDC.isChangingNickName(e.player)) return
        nicknamePDC.setChangingNickName(e.player, false)
    }

    @EventHandler
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (!nicknamePDC.isChangingNickName(e.player)) return
        if (e.from.z == e.to.z && e.from.x == e.to.x) return
        e.player.sendMessage(

                "Извини, но если ты будешь писать на ходу," +
                        " то я не пойму, что ты написал.".gray()
        )
        nicknamePDC.setChangingNickName(e.player, false)
    }
}