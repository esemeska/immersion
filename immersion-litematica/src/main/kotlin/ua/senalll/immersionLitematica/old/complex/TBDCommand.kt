package ua.senalll.immersionLitematica.old.complex

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.FloatArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.tree.LiteralCommandNode
import io.papermc.paper.command.brigadier.CommandSourceStack
import io.papermc.paper.command.brigadier.Commands
import org.bukkit.Location
import org.bukkit.entity.Player
import org.joml.Vector3f
import ua.senalll.immersionLitematica.display.outline.CubeFace
import javax.script.ScriptEngineManager

object TBDCommand {

    fun createCommand(commandName: String="tbd"): LiteralCommandNode<CommandSourceStack> {
        return Commands.literal(commandName)
            .then(Commands.argument("offset", FloatArgumentType.floatArg())
                .then(Commands.argument("structure", StringArgumentType.string())
                    .then(Commands.argument("string", StringArgumentType.string())
                        .executes { ctx ->
                            val sender = ctx.source.sender as Player
                            val playerLoc = sender.location
                            val locationAt = Location(
                                playerLoc.world,
                                playerLoc.blockX.toDouble()+1,
                                playerLoc.blockY.toDouble()+1,
                                playerLoc.blockZ.toDouble()+1
                            )

                            val complexTest = ComplexTest(locationAt)
                            val pixelOffset = FloatArgumentType.getFloat(ctx, "offset")
                            val vectors = parseOffsetVectors(StringArgumentType.getString(ctx, "string"),
                                pixelOffset)
                            val map = parseFaceMap(StringArgumentType.getString(ctx, "structure"))

                            complexTest.createCustom(map, vectors[0], vectors[1], vectors[2])

                            //val complexButton = ComplexTextButtonDisplay(locationAt)
                            //button /tbd 0.0625 "[(TOP)-6,4;(FRONT,BACK)-6,2;(LEFT,RIGHT)-4,2]" "[(-offset*3+.025, -offset*2, -.5+(offset*2)),(-offset*3+.025, -.5, offsett*2),(-offset*2+.025, -.5, offset*3)]"

                            Command.SINGLE_SUCCESS
                        }
                    )

                )
            )
            .build()
    }

    fun parseFaceMap(input: String): Map<Set<CubeFace>, Pair<Int, Int>> {
        val map = mutableMapOf<Set<CubeFace>, Pair<Int, Int>>()
        val cleaned = input.trim().removePrefix("[").removeSuffix("]")

        val parts = cleaned.split(";")

        for (part in parts) {
            val faceAndSize = part.split("-")
            if (faceAndSize.size != 2) continue

            val facesRaw = faceAndSize[0].trim()
            val sizeRaw = faceAndSize[1].trim()

            val faceNames = facesRaw.removePrefix("(").removeSuffix(")").split(",")
            val faces = faceNames.map { CubeFace.valueOf(it.trim().uppercase()) }.toSet()

            val (w, h) = sizeRaw.split(",").map { it.trim().toInt() }
            map[faces] = Pair(w, h)
        }

        return map
    }

    fun parseOffsetVectors(input: String, pixelOffset: Float): List<Vector3f> {
        val vectors = mutableListOf<Vector3f>()

        val content = input.trim().removePrefix("[").removeSuffix("]")
        val tuples = content.split(Regex("\\),\\s*\\(")).map {
            it.replace("(", "").replace(")", "")
        }

        for (tuple in tuples) {
            val components = tuple.split(",").map { it.trim() }
            if (components.size != 3) continue

            val x = evalExpr(components[0], pixelOffset)
            val y = evalExpr(components[1], pixelOffset)
            val z = evalExpr(components[2], pixelOffset)

            vectors.add(Vector3f(x, y, z))
        }

        return vectors
    }

    fun evalExpr(expr: String, offset: Float): Float {
        val replaced = expr
            .replace("offsett", offset.toString())
            .replace("offset", offset.toString())
            .replace("-.", "-0.")
            .replace("+.", "+0.")
            .replace(Regex("(?<!\\d)\\."), "0.") // .5 -> 0.5

        return try {
            val engine = ScriptEngineManager().getEngineByName("JavaScript")
            (engine.eval(replaced) as Number).toFloat()
        } catch (e: Exception) {
            throw IllegalArgumentException("Не удалось вычислить выражение: '$expr'")
        }
    }



}