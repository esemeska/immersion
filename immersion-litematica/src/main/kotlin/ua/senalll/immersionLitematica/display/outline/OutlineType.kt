package ua.senalll.immersionLitematica.display.outline

import org.bukkit.Color
import org.bukkit.entity.Display

enum class OutlineType(val color: Color, val brightness: Display.Brightness) {
    DEFAULT(
        Color.fromARGB(50, 85,155,255),
                Display.Brightness(15, 15)
    ),
    MISSBLOCK(
        Color.fromARGB(50, 255,0,0),
    Display.Brightness(15, 15)
    ),
    MISSVARIANT(
        Color.fromARGB(50, 125,125,125),
    Display.Brightness(15, 15)
    )
}