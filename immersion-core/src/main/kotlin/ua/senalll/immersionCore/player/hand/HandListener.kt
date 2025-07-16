package ua.senalll.immersionCore.player.hand

import org.bukkit.Material
import org.bukkit.entity.ItemDisplay
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPortalEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerChangedWorldEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import ua.senalll.immersionCore.pdc.IsHandPDCHandler
import ua.senalll.immersionCore.pdc.PlayerHasHandPDCHandler
import ua.senalll.immersionapi.util.BlockUtils

object HandListener : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent){
        HandTask.destroyPlayerHand(e.player)
    }

    @EventHandler
    fun onPlayerQuit(e: PlayerQuitEvent){
        HandTask.destroyPlayerHand(e.player)
    }

    @EventHandler
    fun onPlayerDead(e: PlayerDeathEvent){
        HandTask.destroyPlayerHand(e.player)
    }

    @EventHandler
    fun onPlayerChangeWorld(e: PlayerChangedWorldEvent){
        HandTask.destroyPlayerHand(e.player)
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent){
        val p = e.player
        if (!p.isSneaking) return
        if (!e.action.isRightClick) return
        if (p.inventory.itemInMainHand.type != Material.AIR) return

        val targetBlock =p.getTargetBlockExact(6)
        if (targetBlock == null) return
        if (BlockUtils.isBlockSign(targetBlock.type)) return
        if (PlayerHasHandPDCHandler.playerHasHand(p)){
            Hand.createHand(p)
        }
    }

    @EventHandler
    fun onHandPortal(e: EntityPortalEvent){
        if (e.entity !is ItemDisplay) return
        if (IsHandPDCHandler.isHand(e.entity)) return
        e.isCancelled = false
    }
}