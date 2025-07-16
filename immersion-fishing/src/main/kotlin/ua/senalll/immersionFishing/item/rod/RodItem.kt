package ua.senalll.immersionFishing.item.rod

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.inventory.ItemStack
import ua.senalll.immersionFishing.pdc.BaitCountPDCHandler
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import ua.senalll.immersionapi.component.plainText
import ua.senalll.immersionapi.component.reset
import ua.senalll.immersionapi.component.white
import java.util.concurrent.ThreadLocalRandom

object RodItem {
    fun processAddBaitToRod(oldRod: ItemStack, baitCount: Int = 0): Int {
        val currentBaitCount = if (BaitCountPDCHandler.hasBait(oldRod))
            BaitCountPDCHandler.getBaitCount(oldRod) else 0
        val maxCanApply = 64 - currentBaitCount
        val appliedBaitAmount = minOf(baitCount, maxCanApply)

        if (appliedBaitAmount > 0) {
            val newBaitCount = currentBaitCount + appliedBaitAmount
            BaitCountPDCHandler.setBaitCount(oldRod, newBaitCount)
            changeRodLore(oldRod, newBaitCount)
            setRodDisplayName(oldRod, oldRod, true)
        }

        RodUtils.makeRodBaited(oldRod)
        return baitCount - appliedBaitAmount
    }

    fun processRemoveBaitFromRod(oldRod: ItemStack): Int {
        if (BaitCountPDCHandler.hasBait(oldRod)){
            val oldRodBaitCount = BaitCountPDCHandler.getBaitCount(oldRod)
            BaitCountPDCHandler.removeBaitData(oldRod)
            changeRodLore(oldRod, 0)
            setRodDisplayName(oldRod, oldRod, false)

            RodUtils.makeRodDefault(oldRod)

            return oldRodBaitCount
        }

        return 0
    }

    fun removeSingleBaitFromRod(rod: ItemStack){
        if (BaitCountPDCHandler.hasBait(rod)){
            val oldRodBaitCount = BaitCountPDCHandler.getBaitCount(rod)
            val newBaitCount = oldRodBaitCount-1
            if (newBaitCount == 0){
                BaitCountPDCHandler.removeBaitData(rod)
                changeRodLore(rod, 0)
                setRodDisplayName(rod, rod, false)
                RodUtils.makeRodDefault(rod)
            }else{
                BaitCountPDCHandler.setBaitCount(rod, newBaitCount)
                changeRodLore(rod, newBaitCount)
                setRodDisplayName(rod, rod, true)
                RodUtils.makeRodBaited(rod)
            }
        }
    }

    fun changeRodLore(rod: ItemStack, baitCount: Int){
        val itemMeta = rod.itemMeta
        val lore: MutableList<Component?> =
            if (baitCount != 0){
                mutableListOf(
                    "Приманка: ".gray().reset(TextDecoration.ITALIC)
                        .append("$baitCount".white().reset(TextDecoration.ITALIC))
                )
            }else{
                mutableListOf(
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        "".white()
                    } else {
                        "* тут когда-то была приманка *".gray().italic()
                    }
                )
            }

        itemMeta.lore(lore)
        rod.itemMeta = itemMeta
    }

    private fun setRodDisplayName(from: ItemStack, to: ItemStack, withBait: Boolean) {
        val originalMeta = from.itemMeta
        val newMeta = to.itemMeta

        val displayName = originalMeta.displayName()?.plainText()

        when {
            !originalMeta.hasDisplayName() -> {
                val name = if (withBait) RodType.BAITED_ROD.rodName else RodType.DEFAULT_ROD.rodName
                newMeta.displayName(name)
            }
            displayName == RodType.BAITED_ROD.rodName.plainText() && !withBait -> {
                newMeta.displayName(RodType.DEFAULT_ROD.rodName)
            }
            displayName == RodType.DEFAULT_ROD.rodName.plainText() && withBait -> {
                newMeta.displayName(RodType.BAITED_ROD.rodName)
            }
            else -> {
                newMeta.displayName(originalMeta.displayName())
            }
        }
    }
}