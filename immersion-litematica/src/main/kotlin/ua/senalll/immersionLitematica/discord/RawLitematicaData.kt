package ua.senalll.immersionLitematica.discord

import java.nio.file.Path
import java.util.UUID

data class RawLitematicaData (
    val filePath: Path,
    val fileName: String,
    val playerUUID: UUID
)
