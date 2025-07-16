package ua.senalll.immersionapi.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import ua.senalll.immersionapi.util.Utils
import java.util.logging.Level
import java.util.logging.Logger
import javax.sql.DataSource

class DatabaseConnectionManager {

    private val logger: Logger = Utils.createLogger<DatabaseConnectionManager>()

    private var hikariDataSource: HikariDataSource? = null
    private var database: Database? = null

    fun setup(
        setupJdbcUrl: String = "jdbc:sqlite:data/database.db",
        setupDriverClassName: String = "org.sqlite.JDBC",
        setupMaxPoolSize: Int = 10
    ) {
        try {
            val config = HikariConfig().apply {
                jdbcUrl =  setupJdbcUrl
                driverClassName = setupDriverClassName
                maximumPoolSize = setupMaxPoolSize
                isReadOnly = false
                transactionIsolation = "TRANSACTION_SERIALIZABLE"
            }
            hikariDataSource = HikariDataSource(config)

            database = Database.connect(hikariDataSource!!)
            TransactionManager.defaultDatabase = database

            logger.info("Подключение к базе данных через JDBC HikariCP установлено: $setupJdbcUrl")
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Ошибка при инициализации подключения к базе данных через JDBC", e)
            throw DatabaseException("Не удалось подключиться к базе данных", e)
        }
    }

    fun getDatabase(): Database {
        return database ?: throw IllegalStateException("Database not initialized. Call setup() first.")
    }

    fun getDataSource(): DataSource {
        return hikariDataSource ?: throw IllegalStateException("DataSource not initialized. Call setup() first.")
    }

    fun close() {
        try {
            hikariDataSource?.close()
            logger.info("HikariDataSource закрыт")
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Ошибка при закрытии HikariDataSource", e)
        }
    }
}


class DatabaseException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)