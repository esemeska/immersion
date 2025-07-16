package ua.senalll.immersionCore.database

import ua.senalll.immersionapi.database.DatabaseConnectionManager
import ua.senalll.immersionapi.database.sql.SQLDataType
import ua.senalll.immersionapi.database.table.AsyncTableHandler
import java.util.UUID
import java.util.concurrent.CompletableFuture

object TabTableHandler {
    val SHOW_TPS_FIELD = TableField.create("show_tps", SQLDataType.BOOLEAN)
    val SHOW_ONLINE_FIELD = TableField.create("show_online", SQLDataType.BOOLEAN)
    val SHOW_MSPT_FIELD = TableField.create("show_mspt", SQLDataType.BOOLEAN)
    val SHOW_RESTART_FIELD = TableField.create("show_restart", SQLDataType.BOOLEAN)

    val TPS_HEADER_FIELD = TableField.create("tps_header", SQLDataType.BOOLEAN)
    val ONLINE_HEADER_FIELD = TableField.create("online_header", SQLDataType.BOOLEAN)
    val MSPT_HEADER_FIELD = TableField.create("mspt_header", SQLDataType.BOOLEAN)
    val RESTART_HEADER_FIELD = TableField.create("restart_header", SQLDataType.BOOLEAN)

    lateinit var playerTabTableHandler: AsyncTableHandler

    fun init() {
        val tabTable = Table(DatabaseConnectionManager, "tab_settings")
            .column(TableField.UUID.notNull())
            .column(SHOW_TPS_FIELD.default("1"))
            .column(TPS_HEADER_FIELD.default("0"))
            .column(SHOW_ONLINE_FIELD.default("1"))
            .column(ONLINE_HEADER_FIELD.default("1"))
            .column(SHOW_MSPT_FIELD.default("0"))
            .column(MSPT_HEADER_FIELD.default("0"))
            .column(SHOW_RESTART_FIELD.default("0"))
            .column(RESTART_HEADER_FIELD.default("0"))
            .primaryKey(TableField.UUID)
        tabTable.build()

        playerTabTableHandler = AsyncTableHandler(DatabaseConnectionManager, tabTable)
    }

    fun isPlayerExist(uuid: UUID): CompletableFuture<Boolean>{
        return playerTabTableHandler.isExist(uuid)
    }

    fun updateBooleanField(uuid: UUID, field: TableField, boolValue: Boolean) {
        val updateValues = mapOf(
            TableField.UUID to uuid,
            field to boolValue
        )

        playerTabTableHandler.save(updateValues)
    }

    fun getPlayerFields(uuid: UUID): CompletableFuture<Map<String, Any?>?> {
        return playerTabTableHandler.fetch(uuid)
    }

    fun getBooleanFieldValue(uuid: UUID, field: TableField): CompletableFuture<Boolean> {
        return getPlayerFields(uuid).thenApply { fieldsMap ->
            fieldsMap?.get(field.name) as? Boolean ?: field.defaultValue?.toString()?.toBoolean() ?: false
        }
    }

}