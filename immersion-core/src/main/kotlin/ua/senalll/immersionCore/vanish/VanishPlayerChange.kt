package ua.senalll.immersionCore.vanish

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ua.senalll.immersionCore.ImmersionCore
import ua.senalll.immersionCore.chat.VanillaChat
import ua.senalll.immersionapi.component.gray
import java.util.EnumSet

object VanishPlayerChange {
    private val plugin = ImmersionCore.instance
    private val vanishConfig = plugin.dataConfig.vanishConfig
    private val vanishPDC = plugin.pdcRegistry.vanishPDC()
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    fun updateVanishForPlayers(){
        if (vanishConfig.getVanishedPlayers().isEmpty()) return
        for (vanished in vanishConfig.getVanishedPlayers()){
            val player = Bukkit.getPlayer(vanished)
            if(player is Player && player.isOnline) vanishPlayer(Bukkit.getPlayer(vanished)!!)
        }
    }

    fun vanishPlayer(player: Player) {
        if (!vanishConfig.isPlayerVanished(player)){
            vanishPDC.setVanished(player, true)
            vanishConfig.addVanishedPlayer(player)

            val quitMessage = VanillaChat.createQuitMsg(player)
            for (viewer in Bukkit.getOnlinePlayers()) {
                if (viewer != player) {
                    sendPlayerInfoPacket(viewer, player, true)
                    sendEntityDestroyPacket(viewer, player)
                    viewer.hidePlayer(plugin, player)
                    viewer.sendMessage(quitMessage)
                } else {
                    player.sendMessage("* ты зашёл в ваниш *".gray())
                }
            }
        }else{
            vanishPDC.setVanished(player, true)
            vanishConfig.addVanishedPlayer(player)

            for (viewer in Bukkit.getOnlinePlayers()) {
                sendPlayerInfoPacket(viewer, player, true)
                sendEntityDestroyPacket(viewer, player)
                viewer.hidePlayer(plugin, player)
            }
        }
    }

    fun deVanishPlayer(player: Player) {
        if (!vanishConfig.isPlayerVanished(player)) return
        vanishPDC.setVanished(player, false)
        vanishConfig.removeVanishedPlayer(player)

        val joinMessage = VanillaChat.createJoinMsg(player)
        for (viewer in Bukkit.getOnlinePlayers()) {
            if (viewer != player) {
                viewer.showPlayer(plugin, player)
                sendPlayerInfoPacket(viewer, player, false)
                sendSpawnPlayerPacket(viewer, player)
                viewer.sendMessage(joinMessage)
            } else {
                player.sendMessage("* ты вышел из ваниша *".gray())
            }
        }
    }

    private fun sendSpawnPlayerPacket(receiver: Player, vanishedPlayer: Player) {
        if (protocolManager.getEntityTrackers(vanishedPlayer).contains(receiver)) {
            protocolManager.updateEntity(vanishedPlayer, listOf(receiver))
        }
    }

    private fun sendEntityDestroyPacket(receiver: Player, vanishedPlayer: Player) {
        if (ProtocolLibrary.getProtocolManager().getEntityTrackers(vanishedPlayer).contains(receiver)) {
            val packetContainer = PacketContainer(PacketType.Play.Server.ENTITY_DESTROY)

            packetContainer.intLists.write(0, listOf(vanishedPlayer.entityId))

            ProtocolLibrary.getProtocolManager().sendServerPacket(receiver, packetContainer)
        }
    }

    private fun sendPlayerInfoPacket(receiver: Player, vanishedPlayer: Player, vanished: Boolean) {
        val packetContainer = PacketContainer(PacketType.Play.Server.PLAYER_INFO)

        packetContainer.playerInfoActions.write(
            0, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_LISTED)
        )

        val displayName = LegacyComponentSerializer.legacySection().serialize(vanishedPlayer.displayName())

        val pid = PlayerInfoData(
            vanishedPlayer.uniqueId,
            vanishedPlayer.ping,
            !vanished,
            EnumWrappers.NativeGameMode.fromBukkit(vanishedPlayer.gameMode),
            WrappedGameProfile.fromPlayer(vanishedPlayer),
            WrappedChatComponent.fromText(displayName)
        )

        packetContainer.playerInfoDataLists.write(1, listOf(pid))
        protocolManager.sendServerPacket(receiver, packetContainer)
    }
}