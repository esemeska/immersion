package ua.senalll.immersionCore.nickname

import org.bukkit.entity.Player
import ua.senalll.immersionCore.database.NickNameTableHandler
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.util.Utils
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern
import javax.annotation.Nullable

class NickName(
    val player: Player,
    var name: String,
    var nickNameType: NickNameType
) {
    companion object {
        private val logger = Utils.createLogger<NickName>()

        @Nullable
        fun getNameFormat(input: String): NickNameType? {
            val fullnamePattern = Pattern.compile(
                "^[A-ZА-ЯЁІЇЄҐ][a-zа-яёіїєґ]+\\s[A-ZА-ЯЁІЇЄҐ][a-zа-яёіїєґ]+$",
                Pattern.UNICODE_CASE or Pattern.CASE_INSENSITIVE
            )
            val anotherPattern = Pattern.compile(
                "^[а-яёіїєґ0-9_]{3,16}$",
                Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE
            )

            val fullnameMatcher = fullnamePattern.matcher(input)
            val anotherMatcher = anotherPattern.matcher(input)

            return when {
                fullnameMatcher.matches() -> NickNameType.FULLNAME
                anotherMatcher.matches() -> NickNameType.NICKNAME
                else -> null
            }
        }

        fun initFirstLogin(uuid: UUID) {
            NickNameTableHandler.saveData(uuid, uuid.toString(), NickNameType.NO_NICK.typeString)
        }

        fun getNickNameFromPlayer(player: Player): CompletableFuture<NickName> {
            return NickNameTableHandler.fetchNickName(player)
        }

        fun hasNoName(player: Player): CompletableFuture<Boolean> {
            return NickNameTableHandler.isHasNoName(player.uniqueId)
                .thenApply { it ?: false }
        }

        fun getNameFromPlayer(player: Player): CompletableFuture<String> {
            return getNickNameFromPlayer(player)
                .thenApply { nickName -> nickName?.name }
        }
    }

    fun savePlayerNickName() {
        NickNameTableHandler.saveNickName(this).thenRun {
            //NameUpdater.updateForAll(player);
        }
    }

    fun clearNickName() {
        initFirstLogin(this.player.uniqueId)
    }
}

fun Player.defaultNickName(): NickName {
    return NickName(this, this.displayName().plainText(), NickNameType.NO_NICK)
}