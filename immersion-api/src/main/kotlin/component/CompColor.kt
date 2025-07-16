package ua.senalll.immersionapi.component

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

fun String.red(): Component = Component.text(this).color(red)
fun String.white(): Component = Component.text(this).color(white)
fun String.green(): Component = Component.text(this).color(green)
fun String.yellow(): Component = Component.text(this).color(yellow)
fun String.lightPurple(): Component = Component.text(this).color(lightPurple)
fun String.darkGray(): Component = Component.text(this).color(darkGray)
fun String.gray(): Component = Component.text(this).color(gray)
fun String.twitch(): Component = Component.text(this).color(twitch)
fun String.youtube(): Component = Component.text(this).color(youtube)
fun String.modrinth(): Component = Component.text(this).color(modrinth)

fun String.color(color: TextColor): Component = Component.text(this).color(color)