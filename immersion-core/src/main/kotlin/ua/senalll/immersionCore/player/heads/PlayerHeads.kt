package ua.senalll.immersionCore.player.heads

import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack
import ua.senalll.immersionCore.hook.SkinsRestorerHook
import ua.senalll.immersionapi.component.gray
import ua.senalll.immersionapi.component.italic
import java.net.MalformedURLException
import java.net.URISyntaxException

object PlayerHeads {

    @Throws(MalformedURLException::class, URISyntaxException::class)
    fun getHead(player: Player, causeEvent: EntityDamageEvent?): ItemStack {
        val head: ItemStack = SkinsRestorerHook.getPlayerHead(player)
        val lore: MutableList<Component?> = getLoreForDeathCause(causeEvent)
        val itemMeta = head.getItemMeta()
        if (itemMeta != null) {
            itemMeta.lore(lore)
            head.setItemMeta(itemMeta)
        }
        return head
    }


    private fun getLoreForDeathCause(causeEvent: EntityDamageEvent?): MutableList<Component?> {
        val lore: MutableList<Component?> = ArrayList()
        if (causeEvent == null) {
            lore.add(deathLore("Причина смерти неизвестна"))
            return lore
        }

        when (causeEvent.getCause()) {
            DamageCause.FALL -> lore.add(deathLore("Погиб при падении"))
            DamageCause.FIRE, DamageCause.FIRE_TICK -> lore.add(PlayerHeads.deathLore("Сгорел заживо"))
            DamageCause.LAVA -> lore.add(deathLore("Утонул в лаве"))
            DamageCause.DROWNING -> lore.add(deathLore("Утонул"))
            DamageCause.ENTITY_ATTACK -> lore.add(deathLore("Убит существом"))
            DamageCause.PROJECTILE -> lore.add(deathLore("Погиб от стрелы"))
            DamageCause.VOID -> lore.add(deathLore("Провалился в бездну"))
            DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION -> lore.add(PlayerHeads.deathLore("Взорван на кусочки"))
            DamageCause.MAGIC -> lore.add(deathLore("Погиб от магии"))
            DamageCause.SUICIDE -> lore.add(deathLore("Покончил с собой"))
            else -> lore.add(deathLore("Погиб загадочным образом (" + causeEvent.getCause().name + ")"))
        }
        return lore
    }

    private fun deathLore(text: String): Component {
        return text.gray().italic()
    }
}