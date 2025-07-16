package ua.senalll.immersionCore.hook

import com.google.gson.JsonParser
import net.skinsrestorer.api.SkinsRestorer
import net.skinsrestorer.api.SkinsRestorerProvider
import net.skinsrestorer.api.VersionProvider
import net.skinsrestorer.api.event.EventBus
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import ua.senalll.immersionapi.util.Utils
import java.net.URI
import java.util.Base64
import java.util.UUID

object SkinsRestorerHook {

    var skinsRestorer: SkinsRestorer? = null
    var eventBus: EventBus? = null
    private val logger = Utils.createLogger<SkinsRestorerHook>()

    fun initHandler() {
        if (!VersionProvider.isCompatibleWith("15")) {
            logger.info("This plugin was made for SkinsRestorer v15, but ${VersionProvider.getVersionInfo()} is installed. There may be errors!")
        }
        skinsRestorer = SkinsRestorerProvider.get()
        eventBus = skinsRestorer?.eventBus
    }

    fun getPlayerHead(player: Player): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val skullMeta = head.itemMeta as? SkullMeta ?: return head

        if (skinsRestorer == null) {
            logger.warning("SkinsRestorer is not initialized!")
            return head
        }

        val skinIdentifier = skinsRestorer?.playerStorage?.getSkinIdOfPlayer(player.uniqueId)

        if (skinIdentifier?.isPresent == true) {
            val skinProperty = skinsRestorer?.skinStorage?.getSkinDataByIdentifier(skinIdentifier.get())
            if (skinProperty?.isPresent == true) {
                try {
                    val property = skinProperty.get()
                    val value = property.value

                    val decodedValue = String(Base64.getDecoder().decode(value))
                    val jsonObject = JsonParser.parseString(decodedValue).asJsonObject
                    val texturesObject = jsonObject.getAsJsonObject("textures")
                    val skinObject = texturesObject.getAsJsonObject("SKIN")
                    val textureUrl = skinObject.get("url").asString

                    val playerProfile = Bukkit.createProfile(UUID.randomUUID(), null)
                    val textures = playerProfile.textures
                    textures.skin = URI(textureUrl).toURL()
                    playerProfile.setTextures(textures)

                    skullMeta.playerProfile = playerProfile
                    head.itemMeta = skullMeta
                    return head
                } catch (ex: Exception) {
                    logger.warning("Failed to apply custom skin for player ${player.name}: ${ex.message}")
                }
            }
        }

        val profile = player.server.createProfile(player.uniqueId, player.name)
        skullMeta.playerProfile = profile
        head.itemMeta = skullMeta
        return head
    }
}