package ua.senalll.immersionLitematica.display.outline

import org.joml.Vector3f

enum class CubeFace(
    val normal: Vector3f,
    val yaw: Float,
    val pitch: Float
) {
    FRONT(Vector3f(0f, 0f, 1f), 0f, 0f),
    BACK(Vector3f(0f, 0f, -1f), 180f, 0f),
    LEFT(Vector3f(-1f, 0f, 0f), 270f, 0f),
    RIGHT(Vector3f(1f, 0f, 0f), 90f, 0f),
    TOP(Vector3f(0f, 1f, 0f), 0f, -90f),
    BOTTOM(Vector3f(0f, -1f, 0f), 0f, 90f);

    companion object {
        fun fromNormal(normal: Vector3f): CubeFace? {
            return entries.find { it.normal == normal }
        }
        fun getSide(): List<CubeFace>{
            return listOf(FRONT, BACK, LEFT, RIGHT)
        }
    }
}