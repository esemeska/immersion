package ua.senalll.immersionapi.util

import org.bukkit.Material

object BlockUtils {
    fun isBlockSign(material: Material): Boolean{
        return  material.toString().lowercase().contains("sign")
    }
}