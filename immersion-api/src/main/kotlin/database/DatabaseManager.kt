package ua.senalll.immersionapi.database

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.job
import kotlinx.coroutines.withContext
import org.bukkit.plugin.Plugin
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import ua.senalll.immersionapi.util.Utils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.logging.Level

object DatabaseManager {
    private var logger = Utils.createLogger<DatabaseManager>()
    private lateinit var asyncDispatcher: CoroutineDispatcher
    private lateinit var scope: CoroutineScope
    private lateinit var executorService: ExecutorService

    fun init(plugin: Plugin) {
        logger = plugin.logger
        executorService = Executors.newFixedThreadPool(4) { runnable ->
            Thread(runnable, "DatabaseManager-Worker").apply { isDaemon = true }
        }
        asyncDispatcher = executorService.asCoroutineDispatcher()
        scope = CoroutineScope(SupervisorJob() + asyncDispatcher)
    }

    suspend fun <T> db(block: () -> T): T = withContext(asyncDispatcher) {
        transaction {
            try {
                block()
            } catch (e: Exception) {
                logger.log(Level.SEVERE, "Ошибка в операции с базой данных: ${e.message}", e)
                throw e
            }
        }
    }

    suspend fun shutdown(waitForTasksToComplete: Boolean = false) {
        try {
            if (waitForTasksToComplete) {
                scope.coroutineContext.job.children.forEach { it.join() }
            } else {
                scope.coroutineContext.cancel()
            }
        } finally {
            executorService.close()
            executorService.awaitTermination(5, TimeUnit.SECONDS)
            logger.info("DatabaseManager shutdown")
        }
    }

}