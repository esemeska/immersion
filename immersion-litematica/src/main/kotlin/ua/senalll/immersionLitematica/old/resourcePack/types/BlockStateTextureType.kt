package ua.senalll.immersionLitematica.old.resourcePack.types

enum class BlockStateTextureType {
    VARIANTS, MULTIPART
}

object VariantsParameter{

    enum class FACE {
        CEILING, FLOOR, WALL,
    }

    enum class FACING{
        EAST, NORTH, SOUTH, WEST
    }

    enum class POWERED{
        TRUE, FALSE
    }

    enum class AXIS{
        X, Y, Z
    }

    enum class TYPE{
        BOTTOM, DOUBLE, TOP
    }

    enum class HALF{
        BOTTOM, TOP
    }

    enum class SHAPE{
        OUTER_LEFT, OUTER_RIGHT, INNER_LEFT, INNER_RIGHT, STRAIGHT
    }
}


