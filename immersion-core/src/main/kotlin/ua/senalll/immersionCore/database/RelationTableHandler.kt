package ua.senalll.immersionCore.database

import ua.senalll.immersionapi.database.DatabaseConnectionManager
import ua.senalll.immersionapi.database.sql.SQLDataType
import ua.senalll.immersionapi.database.table.AsyncTableHandler
import ua.senalll.immersionapi.util.Utils
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.logging.Level

object RelationTableHandler {
    private val logger = Utils.createLogger<RelationTableHandler>()

    val PLAYER_UUID_FIELD = TableField.create("uuid", SQLDataType.UUID).notNull()
    val PLAYER_RELATED_FIELD = TableField.create("related_uuid", SQLDataType.UUID).notNull()

    lateinit var playerTableHandler: AsyncTableHandler

    fun init() {
        val relationTable = Table(DatabaseConnectionManager, "player_relations")
            .column(PLAYER_UUID_FIELD)
            .column(PLAYER_RELATED_FIELD)
            .primaryKey(PLAYER_UUID_FIELD)
        relationTable.build()

        playerTableHandler = AsyncTableHandler(DatabaseConnectionManager, relationTable)
    }

    fun addRelation(uuid1: UUID, uuid2: UUID) {
        val pair = sortPair(uuid1, uuid2)
        val values = mapOf(
            PLAYER_UUID_FIELD to pair.first,
            PLAYER_RELATED_FIELD to pair.second
        )
        playerTableHandler.save(values)
    }

    fun asyncAddRelation(uuid1: UUID, uuid2: UUID): CompletableFuture<Void> {
        val pair = sortPair(uuid1, uuid2)
        val values = mapOf(
            PLAYER_UUID_FIELD to pair.first,
            PLAYER_RELATED_FIELD to pair.second
        )
        return playerTableHandler.save(values)
            .thenApply { result ->
                logger.log(Level.INFO, "Добавлено знакомство: player=${pair.first}, related=${pair.second}")
                null
            }
    }

    fun loadRelation(uuid: UUID): Map<String, Any?>? {
        return playerTableHandler.fetch(uuid).join()
    }

    fun asyncLoadRelation(uuid: UUID): CompletableFuture<Map<String, Any?>?> {
        return playerTableHandler.fetch(uuid)
            .exceptionally { e ->
                logger.log(Level.SEVERE, "Ошибка при загрузке связи для UUID: $uuid", e)
                null
            }
    }

    fun isPlayerExistAsync(uuid: UUID): CompletableFuture<Boolean> {
        return playerTableHandler.isExist(uuid)
    }

    fun isPlayerRelatedExistAsync(uuid: UUID): CompletableFuture<Boolean> {
        return playerTableHandler.existsWhere(PLAYER_RELATED_FIELD, uuid)
            .exceptionally { e ->
                logger.log(Level.SEVERE, "Ошибка при проверке существования связи для UUID: $uuid", e)
                throw RuntimeException(e)
            }
    }

    fun isRelationExistAsync(uuid1: UUID, uuid2: UUID): CompletableFuture<Boolean> {
        val pair = sortPair(uuid1, uuid2)
        val conditions = mapOf(
            PLAYER_UUID_FIELD.name to pair.first,
            PLAYER_RELATED_FIELD.name to pair.second
        )

        return playerTableHandler.existsWhere(conditions)
            .exceptionally { e ->
                logger.log(Level.SEVERE, "Ошибка при проверке существования отношения между $uuid1 и $uuid2", e)
                throw RuntimeException(e)
            }
    }

    private fun sortPair(a: UUID, b: UUID): Pair<UUID, UUID> {
        return if (a.toString().compareTo(b.toString()) < 0)
            Pair(a, b)
        else
            Pair(b, a)
    }
}