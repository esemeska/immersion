package ua.senalll.immersionLitematica.old

import org.joml.Vector3f

enum class TBDScaleType(val scale: Vector3f) {
    BLOCK(Vector3f(8.0f, 3.65f, 1f)),
    TEXTURE_PIXEL(Vector3f(BLOCK.scale.x / 16, BLOCK.scale.y / 16, 1f)),
    SIGN(Vector3f(TEXTURE_PIXEL.scale.x * .67f, TEXTURE_PIXEL.scale.y * .67f, 1f))
}