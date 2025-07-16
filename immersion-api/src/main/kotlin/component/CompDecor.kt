package ua.senalll.immersionapi.component

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

fun Component.italic(): Component = this.decorate(TextDecoration.ITALIC)
fun Component.bold(): Component = this.decorate(TextDecoration.BOLD)
fun Component.underline(): Component = this.decorate(TextDecoration.UNDERLINED)
fun Component.strikethrough(): Component = this.decorate(TextDecoration.STRIKETHROUGH)
fun Component.obfuscated(): Component = this.decorate(TextDecoration.OBFUSCATED)

fun Component.resetComponent(): Component = this
    .decoration(TextDecoration.ITALIC, false)
    .decoration(TextDecoration.BOLD, false)
    .decoration(TextDecoration.UNDERLINED, false)
    .decoration(TextDecoration.STRIKETHROUGH, false)
    .decoration(TextDecoration.OBFUSCATED, false)

fun Component.plainText(): String = PlainTextComponentSerializer.plainText().serialize(this)

fun Component.reset(vararg decorations: TextDecoration): Component =
    decorations.fold(this) { comp, deco -> comp.decoration(deco, false) }