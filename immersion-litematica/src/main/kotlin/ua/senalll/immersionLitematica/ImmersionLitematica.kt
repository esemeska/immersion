package ua.senalll.immersionLitematica

import com.github.shynixn.mccoroutine.bukkit.SuspendingJavaPlugin
import org.bukkit.Bukkit
import org.bukkit.Location
import ua.senalll.immersionLitematica.database.SchematicSettingsTableHandler
import ua.senalll.immersionLitematica.old.complex.ComplexTextBlockDisplay
import ua.senalll.immersionLitematica.schematic.json.JsonSchematic
import ua.senalll.immersionLitematica.schematic.json.SchematicaConverter
import ua.senalll.immersionLitematica.schematic.SchematicVisualizer
import ua.senalll.immersionLitematica.old.complex.ComplexTextSignDisplay
import ua.senalll.immersionapi.database.DatabaseConnectionManager
import java.io.File
import ua.senalll.immersionapi.database.DatabaseManager
import java.util.logging.Level


class ImmersionLitematica : SuspendingJavaPlugin() {
    companion object {
        lateinit var instance: ImmersionLitematica
        var connectionManager: DatabaseConnectionManager = DatabaseConnectionManager()
    }

    override suspend fun onEnableAsync() {
        instance = this

        if (!dataFolder.exists()) {
            dataFolder.mkdirs()
        }

        initializeDatabase()
        DatabaseManager.init(this)

        try {
            SchematicSettingsTableHandler.init()
            logger.info("Database initialized successfully")
        } catch (e: Exception) {
            logger.log(Level.SEVERE, "Failed to initialize database", e)
            server.pluginManager.disablePlugin(this)
        }
    }

    fun initializeDatabase() {
        val dbFile = File(dataFolder, "db.sqlite")

        dbFile.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()
                println("Создана директория для базы данных: ${parent.absolutePath}")
            }
        }

        println("Инициализация SQLite базы: ${dbFile.absolutePath}")

       connectionManager.setup("jdbc:sqlite:${dbFile.absolutePath}")
    }

    override suspend fun onDisableAsync() {
        DatabaseManager.shutdown(waitForTasksToComplete = true)
    }

    fun handleTBD(){
        this.server.scheduler.runTask(this, Runnable {
            val world = Bukkit.getWorlds().first()
            val location = Location(world, .0, 150.0, 0.0)
            //val tBD = TextBlockDisplay(location.subtract(2.0, .0, .0), 40)
            //tBD.createTBD()

            val complexTDB = ComplexTextBlockDisplay(location.clone().subtract(4.0, .0, .0))
            complexTDB.create()

            val complexSign = ComplexTextSignDisplay(location.clone().subtract(6.0, .0, .0))
            complexSign.create()


        })
    }

    fun handlerSchematic(){
        val converter = SchematicaConverter()

        val inputFile = File(dataFolder, "schem.litematic")
        val formattedOutputFile = File(dataFolder, "formatted_output.json")

        if (!inputFile.exists()) {
            logger.warning("Файл ${inputFile.name} не найден в папке плагина! Помести его туда и перезапусти сервер.")
            return
        }

        try {
            converter.convertToFormattedJson(inputFile.absolutePath, formattedOutputFile.absolutePath)

            logger.info("Готово! JSON файлы записаны в папку плагина.")
        } catch (e: Exception) {
            logger.severe("Ошибка при конвертации: ${e.message}")
            e.printStackTrace()
        }

        val schematic = JsonSchematic.fromJsonFile(formattedOutputFile.absolutePath)

        if (schematic != null) {
            this.server.scheduler.runTask(this, Runnable {
                val world = Bukkit.getWorlds().first()

                val visualizer = SchematicVisualizer(world, schematic)
                visualizer.visualize()
            })
        }
    }


}

