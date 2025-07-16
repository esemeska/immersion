package ua.senalll.immersionLitematica.database

import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.update
import ua.senalll.immersionapi.database.DatabaseManager
import ua.senalll.immersionapi.util.Utils
import java.util.UUID

object SchematicSettingsTableHandler {
    private val logger = Utils.createLogger<SchematicSettingsTableHandler>()

    object SchematicSettings : Table("schematics_settings") {
        val playerUUID = uuid("player_uuid")
        val schematicUUID = uuid("schematic_uuid")
        val layerSettings = text("layer_settings")
        val generatorBlock = text("generator_block")
        val vector = text("vector")

        override val primaryKey = PrimaryKey(playerUUID, schematicUUID)
    }

    suspend fun init() {
        DatabaseManager.db {
            SchemaUtils.create(SchematicSettings)
            logger.info("SchematicSettings table initialized successfully")
        }
    }

    suspend fun saveSettings(
        playerUUID: UUID,
        schematicUUID: UUID,
        layerSettings: String,
        generatorBlock: String,
        vector: String
    ) {
        DatabaseManager.db {
            SchematicSettings.insertIgnore {
                it[SchematicSettings.playerUUID] = playerUUID
                it[SchematicSettings.schematicUUID] = schematicUUID
                it[SchematicSettings.layerSettings] = layerSettings
                it[SchematicSettings.generatorBlock] = generatorBlock
                it[SchematicSettings.vector] = vector
            }
            logger.info("Saved schematic settings for $schematicUUID and player $playerUUID")
        }
    }

    suspend fun getSettings(playerUUID: UUID, schematicUUID: UUID): Map<String, String> {
        return DatabaseManager.db {
            val row = SchematicSettings
                .select ( (SchematicSettings.playerUUID eq playerUUID) and (SchematicSettings.schematicUUID eq schematicUUID) )
                .singleOrNull()
                ?: throw NoSuchElementException("No settings found for player $playerUUID and schematic $schematicUUID")

            mapOf(
                "layerSettings" to row[SchematicSettings.layerSettings],
                "generatorBlock" to row[SchematicSettings.generatorBlock],
                "vector" to row[SchematicSettings.vector]
            )
        }
    }

    suspend fun updateSettings(
        playerUUID: UUID,
        schematicUUID: UUID,
        layerSettings: String,
        generatorBlock: String,
        vector: String
    ): Boolean {
        return DatabaseManager.db {
            val updated = SchematicSettings.update({
                (SchematicSettings.playerUUID eq playerUUID) and (SchematicSettings.schematicUUID eq schematicUUID)
            }) {
                it[SchematicSettings.layerSettings] = layerSettings
                it[SchematicSettings.generatorBlock] = generatorBlock
                it[SchematicSettings.vector] = vector
            }
            updated > 0
        }
    }

    suspend fun deleteSettings(playerUUID: UUID, schematicUUID: UUID): Boolean {
        return DatabaseManager.db {
            val deleted = SchematicSettings.deleteWhere {
                (SchematicSettings.playerUUID eq playerUUID) and (SchematicSettings.schematicUUID eq schematicUUID)
            }
            deleted > 0
        }
    }

    suspend fun getAllSettingsForPlayer(playerUUID: UUID): List<Pair<UUID, Map<String, String>>> {
        return DatabaseManager.db {
                SchematicSettings
                    .select ( SchematicSettings.playerUUID eq playerUUID )
                    .map {
                        it[SchematicSettings.schematicUUID] to mapOf(
                            "layerSettings" to it[SchematicSettings.layerSettings],
                            "generatorBlock" to it[SchematicSettings.generatorBlock],
                            "vector" to it[SchematicSettings.vector]
                        )
                    }
        }
    }
}