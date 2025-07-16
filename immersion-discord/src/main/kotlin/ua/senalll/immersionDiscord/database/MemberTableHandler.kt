package ua.senalll.immersionDiscord.database

import org.jetbrains.exposed.v1.core.QueryBuilder
import org.jetbrains.exposed.v1.core.SqlExpressionBuilder.eq
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.insertIgnore
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.update
import ua.senalll.immersionapi.database.DatabaseManager
import ua.senalll.immersionapi.util.Utils
import java.util.UUID
import java.util.logging.Level


object MemberTableHandler {
    private val logger = Utils.createLogger<MemberTableHandler>()

    object DiscordMembers : Table("discord_members") {
        val memberId = varchar("member_id", 64).uniqueIndex()
        val playerUUID = uuid("player_uuid").uniqueIndex()

        override val primaryKey = PrimaryKey(memberId)
    }

    suspend fun recreateDiscordMembersTable() {
        DatabaseManager.db {
            SchemaUtils.drop(DiscordMembers)
            SchemaUtils.create(DiscordMembers)
            logger.info("DiscordMembers table recreated successfully")
        }
    }

    suspend fun init() {
        DatabaseManager.db {
            SchemaUtils.create(DiscordMembers)
            logger.info("DiscordMembers table initialized successfully")
        }
    }

    suspend fun saveMemberData(discordId: String, uuid: UUID) {
        DatabaseManager.db {
            val affectedRows = DiscordMembers.insertIgnore {
                it[memberId] = discordId
                it[playerUUID] = uuid
            }

            if (affectedRows.insertedCount == 0) {
                DiscordMembers.update({ DiscordMembers.memberId eq discordId }) {
                    it[playerUUID] = uuid
                }
            }

            logger.log(Level.INFO, "Added/Updated discord member: player=$uuid, discordId=$discordId")
        }
    }

    suspend fun isMemberExist(playerUUID: UUID): Boolean {
        return DatabaseManager.db {
            DiscordMembers
                .select ( DiscordMembers.playerUUID eq playerUUID )
                .limit(1)
                .count() > 0
        }
    }

    suspend fun isMemberLinked(discordId: String): Boolean {
        return DatabaseManager.db {
            DiscordMembers
                .select ( DiscordMembers.memberId eq discordId )
                .limit(1)
                .count() > 0
        }
    }

    suspend fun getMemberUUID(discordId: String): UUID {
        return DatabaseManager.db {
            val query = DiscordMembers
                .selectAll()
                .where { DiscordMembers.memberId eq discordId }

            logger.info("Generated SQL query: ${query.prepareSQL(QueryBuilder(false))}")

            val row = query.singleOrNull()
                ?: throw NoSuchElementException("Member not found with Discord ID: $discordId")

            row[DiscordMembers.playerUUID]
        }
    }

    suspend fun getMemberDiscordID(uuid: UUID): String {
        return DatabaseManager.db {
            val query = DiscordMembers
                .selectAll()
                .where { DiscordMembers.playerUUID eq uuid }

            logger.info("Generated SQL query: ${query.prepareSQL(QueryBuilder(false))}")

            val row = query.singleOrNull()
                ?: throw NoSuchElementException("No discord ID for UUID: $uuid")

            row[DiscordMembers.memberId]
        }
    }
}