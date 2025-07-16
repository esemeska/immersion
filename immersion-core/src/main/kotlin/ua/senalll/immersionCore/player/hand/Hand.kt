package ua.senalll.immersionCore.player.hand

import org.bukkit.Bukkit
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import ua.senalll.immersionCore.pdc.HandHighTaskIdPDCHandler
import ua.senalll.immersionCore.pdc.HandIDPDCHandler
import ua.senalll.immersionCore.pdc.HandMoveTaskIdPDCHandler
import ua.senalll.immersionCore.pdc.HandsPlayerUUIDPDCHandler
import ua.senalll.immersionCore.pdc.IsHandPDCHandler
import ua.senalll.immersionCore.pdc.PlayerHasHandPDCHandler
import ua.senalll.immersionCore.pdc.PlayersHandUUIDPDCHandler
import ua.senalll.immersionCore.player.hand.HandItemDisplay.handLoc
import java.util.UUID

object Hand {
    fun createHand(p: Player) {
        HandItemDisplay.spawnHand(p)
            .withScale(1.2)
            .execute { itemDisplay ->
                val handUUID = UUID.randomUUID()
                val targetLocation = arrayOf(p.location.handLoc())

                val handMoveTaskId = HandTask.startHandMovementTask(itemDisplay, p, targetLocation)
                val highTaskId = HandTask.startHighFiveDetectionTask(itemDisplay, p, handUUID)

                makeItemDisplayAHand(itemDisplay, p, handUUID, handMoveTaskId, highTaskId)
                addHandToPlayer(p, itemDisplay)

                HandTask.scheduleHandDestruction(itemDisplay, p)

                HandSound.playHandSound(p, HandSound.Sound.HAND_CAST)
            }
    }

    fun makeItemDisplayAHand(itemDisplay: ItemDisplay, player: Player, itemUUID: UUID, moveTaskId: Int, highTaskId: Int) {
        IsHandPDCHandler.setIsHand(itemDisplay)
        HandIDPDCHandler.setHandUUID(itemDisplay, itemUUID)
        HandsPlayerUUIDPDCHandler.setPlayerUUID(itemDisplay, player.uniqueId)
        HandMoveTaskIdPDCHandler.setTaskId(itemDisplay, moveTaskId)
        HandHighTaskIdPDCHandler.setTaskId(itemDisplay, highTaskId)
    }


    fun addHandToPlayer(player: Player, itemDisplay: ItemDisplay) {
        PlayerHasHandPDCHandler.setPlayerHasHand(player)
        val handUUID = HandIDPDCHandler.getHandUUID(itemDisplay)
        if (handUUID != null) {
            PlayersHandUUIDPDCHandler.setHandUUID(player, handUUID)
        }
    }

    fun removeHandFromPlayer(player: Player) {
        PlayerHasHandPDCHandler.setPlayerHasHand(player, false)
        PlayersHandUUIDPDCHandler.removeHandUUID(player)
    }

    fun finalizeHandImpact(itemDisplay: ItemDisplay, p: Player) {
        removeHandFromPlayer(p)
        HandSound.playHandSound(p, HandSound.Sound.HAND_IMPACT)
        itemDisplay.remove()
    }

    fun finalizeHandFail(itemDisplay: ItemDisplay, p: Player) {
        removeHandFromPlayer(p)
        HandSound.playHandSound(p, HandSound.Sound.HAND_FAIL)
        itemDisplay.remove()
    }

    fun getPlayerFromHand(itemDisplay: ItemDisplay): Player? {
        val playerUUID = HandsPlayerUUIDPDCHandler.getPlayerUUID(itemDisplay)
        return playerUUID?.let { Bukkit.getPlayer(it) }
    }

    fun getHandFromPlayer(player: Player): ItemDisplay? {
        if (PlayerHasHandPDCHandler.playerHasHand(player)) {
            val playerUUID = player.uniqueId
            for (ent in player.getNearbyEntities(3.0, 3.0, 3.0)) {
                if (ent is ItemDisplay && IsHandPDCHandler.isHand(ent)) {
                    val handOwner = HandsPlayerUUIDPDCHandler.getPlayerUUID(ent)
                    if (playerUUID == handOwner) {
                        return ent
                    }
                }
            }
        }
        return null
    }
}