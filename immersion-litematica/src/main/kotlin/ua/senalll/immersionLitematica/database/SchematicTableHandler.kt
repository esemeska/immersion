package ua.senalll.immersionLitematica.database

import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ua.senalll.immersionapi.database.DatabaseManager
import ua.senalll.immersionapi.util.Utils
import java.util.UUID

object SchematicTableHandler {
    private val logger = Utils.createLogger<SchematicTableHandler>()

    object Schematics : Table("schematics") {
        val schematicUUID = uuid("schematic_uuid").uniqueIndex()
        val schematicName = varchar("name", 255)
        val playerUUID = uuid("owner_uuid")
        val schematicFilePath = text("file_path")

        override val primaryKey = PrimaryKey(schematicUUID)
    }

    suspend fun init() {
        DatabaseManager.db {
            SchemaUtils.create(Schematics)
            logger.info("Schematics table initialized successfully")
        }
    }

    suspend fun saveSchematic(
        schematicUUID: UUID,
        name: String,
        playerUUID: UUID,
        filePath: String
    ) {
        DatabaseManager.db {
            Schematics.insert {
                it[Schematics.schematicUUID] = schematicUUID
                it[Schematics.schematicName] = name
                it[Schematics.playerUUID] = playerUUID
                it[Schematics.schematicFilePath] = filePath
            }
            logger.info("Saved schematic $name for player $playerUUID")
        }
    }

    suspend fun getSchematicsForPlayer(playerUUID: UUID): List<String> {
        return DatabaseManager.db {
            Schematics
                .select ( Schematics.playerUUID eq playerUUID )
                .map { it[Schematics.schematicName] }
        }
    }

    suspend fun deleteSchematicByUUID(uuid: UUID): Boolean {
        return DatabaseManager.db {
            val deleted = Schematics.deleteWhere { Schematics.schematicUUID eq uuid }
            deleted > 0
        }
    }

    suspend fun getSchematicFilePath(name: String, playerUUID: UUID): String {
        return DatabaseManager.db {
            Schematics
                .select ( (Schematics.schematicName eq name) and (Schematics.playerUUID eq playerUUID) )
                .singleOrNull()
                ?.get(Schematics.schematicFilePath)
                ?: throw NoSuchElementException("No schematic found for name=$name and player=$playerUUID")
        }
    }

    suspend fun isSchematicExist(name: String, playerUUID: UUID): Boolean {
        return DatabaseManager.db {
            Schematics
                .select ( (Schematics.schematicName eq name) and (Schematics.playerUUID eq playerUUID) )
                .limit(1)
                .count() > 0
        }
    }

    suspend fun updateSchematicFilePath(uuid: UUID, newPath: String): Boolean {
        return DatabaseManager.db {
            val updated = Schematics.update({ Schematics.schematicUUID eq uuid }) {
                it[schematicFilePath] = newPath
            }
            updated > 0
        }
    }

    suspend fun getAllSchematics(): List<Pair<UUID, String>> {
        return DatabaseManager.db {
            Schematics
                .selectAll()
                .map { it[Schematics.schematicUUID] to it[Schematics.schematicName] }
        }
    }
}