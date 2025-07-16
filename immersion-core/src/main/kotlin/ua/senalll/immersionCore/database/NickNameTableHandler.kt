package ua.senalll.immersionCore.database

import org.bukkit.entity.Player
import ua.senalll.immersionCore.nickname.NickName
import ua.senalll.immersionCore.nickname.NickNameType
import ua.senalll.immersionCore.nickname.defaultNickName
import ua.senalll.immersionapi.database.DatabaseConnectionManager
import ua.senalll.immersionapi.database.sql.SQLDataType
import ua.senalll.immersionapi.database.table.AsyncTableHandler
import ua.senalll.immersionapi.util.Utils
import java.util.*
import java.util.concurrent.CompletableFuture

object NickNameTableHandler {
    val PLAYER_UUID_FIELD = TableField.create("uuid", SQLDataType.UUID).notNull()
    val NAME_FIELD = TableField.create("name", SQLDataType.SHORT_TEXT).notNull()
    val NAME_TYPE_FIELD = TableField.create("name_type", SQLDataType.SHORT_TEXT).notNull()
    private val logger = Utils.createLogger<NickNameTableHandler>()

    lateinit var playerTableHandler: AsyncTableHandler

    fun init() {
        val nickNameTable = Table(DatabaseConnectionManager, "player_names")
            .column(PLAYER_UUID_FIELD)
            .column(NAME_FIELD)
            .column(NAME_TYPE_FIELD)
            .primaryKey(PLAYER_UUID_FIELD)
        nickNameTable.build()

        playerTableHandler = AsyncTableHandler(DatabaseConnectionManager, nickNameTable)
    }


    fun saveData(uuid: UUID, name: String, nameType: String){
        val updateValues = mapOf(
            PLAYER_UUID_FIELD to uuid,
            NAME_FIELD to name,
            NAME_TYPE_FIELD to nameType
        )
        playerTableHandler.save(updateValues)
    }

    fun saveNickName(name: NickName): CompletableFuture<Unit>{
        val updateValues = mapOf(
            PLAYER_UUID_FIELD to name.player.uniqueId,
            NAME_FIELD to name.name,
            NAME_TYPE_FIELD to name.nickNameType
        )
        return playerTableHandler.save(updateValues)
    }

    fun fetchData(uuid: UUID): CompletableFuture<Map<String, Any?>?> {
        return playerTableHandler.fetch(uuid)
    }

    fun fetchNickName(player: Player): CompletableFuture<NickName>{
        return fetchData(player.uniqueId).thenApply { data ->
            if (data == null) return@thenApply player.defaultNickName()
            val name = data[NAME_FIELD.name]
            val nameType = data[NAME_TYPE_FIELD.name]
            return@thenApply NickName(player, name as String, NickNameType.fromString(nameType as String))
        }
    }

    fun isPlayerExist(uuid: UUID): Boolean {
        return playerTableHandler.isExist(uuid).get()
    }

    fun isHasNoName(uuid: UUID): CompletableFuture<Boolean?> {
        return playerTableHandler.isExist(uuid).thenCompose { exists ->
            if (!exists) {
                CompletableFuture.completedFuture(false)
            } else {
                fetchData(uuid).thenApply { data ->
                    data != null && (data[NAME_TYPE_FIELD.name] as String) == NickNameType.NO_NICK.typeString
                }
            }
        }
    }


}