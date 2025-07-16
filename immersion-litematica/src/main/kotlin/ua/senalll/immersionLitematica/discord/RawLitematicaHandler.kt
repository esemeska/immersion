package ua.senalll.immersionLitematica.discord

import ua.senalll.immersionLitematica.schematic.SchematicManager

class RawLitematicaHandler private constructor() {
    companion object {
        private var litematicaInstance: RawLitematicaHandler? = null

        fun getInstance(): RawLitematicaHandler {
            if (litematicaInstance == null) {
                litematicaInstance = RawLitematicaHandler()
            }
            return litematicaInstance!!
        }
    }

    fun convertLitematicFile(rawLitematicaData: RawLitematicaData) {
        println("convertLitematicFile")
        SchematicManager.initializeJsonSchematic(rawLitematicaData)
    }
}