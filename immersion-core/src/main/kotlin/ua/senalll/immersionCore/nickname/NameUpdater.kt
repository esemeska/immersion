package ua.senalll.immersionCore.nickname

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.PlayerInfoData
import com.comphenix.protocol.wrappers.WrappedChatComponent
import com.comphenix.protocol.wrappers.WrappedGameProfile
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import ua.senalll.immersionCore.database.RelationTableHandler
import ua.senalll.immersionapi.component.green
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.white
import java.util.Collections
import java.util.EnumSet
import java.util.concurrent.CompletableFuture

object NameUpdater {

    fun updateToAll(target: Player){
        for (viewer in Bukkit.getOnlinePlayers()){
            updateNameForBoth(viewer, target)
        }
    }

    fun updateNameForBoth(viewer: Player, target: Player) {
        getNameToShow(viewer, target).thenAccept { name ->
            sendNewNamePacket(viewer, target, name)
        }
        if (viewer.uniqueId != target.uniqueId) {
            getNameToShow(target, viewer).thenAccept { name ->
                sendNewNamePacket(target, viewer, name)
            }
        }
    }

    fun getNameToShow(viewer: Player, target: Player): CompletableFuture<String> {
        return RelationTableHandler.isRelationExistAsync(viewer.uniqueId, target.uniqueId)
            .thenCompose { isRelated ->
                NickName.getNickNameFromPlayer(target).thenApply { nickName ->
                    if (isRelated || viewer.uniqueId == target.uniqueId) {
                        if (nickName.nickNameType == NickNameType.NO_NICK) target.name else nickName.name
                    } else {
                        "Незнакомец"
                    }
                }
            }
    }

    fun updateFriendDisplayName(viewer: Player, target: Player) {
        RelationTableHandler.isRelationExistAsync(viewer.uniqueId, target.uniqueId)
            .thenAccept { isFriend ->
                if (isFriend) {
                    val prefix = "[👬]     ".green()
                    val name = target.name.white()
                    val display = prefix.append(name)

                    val displayName = display.plainText()
                    sendNewNamePacket(viewer, target, displayName)
                }
            }
    }

    private fun sendNewNamePacket(viewer: Player, target: Player, displayName: String) {
        val packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.PLAYER_INFO)

        packet.playerInfoActions.write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.UPDATE_DISPLAY_NAME))
        packet.playerInfoDataLists.write(1, Collections.singletonList(
            PlayerInfoData(
                WrappedGameProfile(target.uniqueId, displayName),
                target.ping,
                EnumWrappers.NativeGameMode.SURVIVAL,
                WrappedChatComponent.fromText(displayName)
            )
        ))

        ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, packet)
    }

}