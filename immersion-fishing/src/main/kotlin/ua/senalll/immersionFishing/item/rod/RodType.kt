package ua.senalll.immersionFishing.item.rod

import net.kyori.adventure.text.Component
import ua.senalll.immersionapi.component.resetComponent
import ua.senalll.immersionapi.component.white

enum class RodType(
    val rodName: Component
) {
    DEFAULT_ROD("Удочка".white().resetComponent()),
    BAITED_ROD("Удочка с приманкой".white().resetComponent());
}