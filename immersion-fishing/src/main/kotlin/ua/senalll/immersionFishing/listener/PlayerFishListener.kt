package ua.senalll.immersionFishing.listener

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fish
import org.bukkit.entity.FishHook
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
import org.bukkit.util.Vector
import ua.senalll.immersionFishing.ImmersionFishing
import ua.senalll.immersionFishing.bobber.BobberHandler
import ua.senalll.immersionFishing.item.rod.RodItem
import ua.senalll.immersionFishing.item.rod.RodUtils
import ua.senalll.immersionFishing.loot.FishingLootManager
import ua.senalll.immersionFishing.pdc.FishHookPDCHandler
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

object PlayerFishListener : Listener {
    @EventHandler
    fun onPlayerFish(e: PlayerFishEvent) {
        when (e.state) {
            PlayerFishEvent.State.FISHING -> {
            }

            PlayerFishEvent.State.LURED -> {
                e.isCancelled = true
                BobberHandler.createBobber(e)
            }

            PlayerFishEvent.State.CAUGHT_FISH -> {
                e.isCancelled = true
            }

            PlayerFishEvent.State.REEL_IN,
            PlayerFishEvent.State.FAILED_ATTEMPT -> {
                BobberHandler.removeBobberByHook(e.hook)
            }

            else -> {
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (e.action != Action.RIGHT_CLICK_AIR && e.action != Action.RIGHT_CLICK_BLOCK) return

        val item = e.item ?: return
        if (item.type != Material.FISHING_ROD) return

        val player = e.player

        val hook = player.getNearbyEntities(32.0, 32.0, 32.0)
            .filterIsInstance<FishHook>()
            .filter{ FishHookPDCHandler.hasPlayer(it) }
            .firstOrNull { FishHookPDCHandler.getUUID(it) == player.uniqueId }
        if (hook != null){
            BobberHandler.handleRightClick(player, hook)
        }
    }

    @EventHandler
    fun onPlayerItemHeld(e: PlayerItemHeldEvent) {
        val player = e.player
        val hook = player.getNearbyEntities(32.0, 32.0, 32.0)
            .filterIsInstance<FishHook>()
            .filter{ FishHookPDCHandler.hasPlayer(it) }
            .firstOrNull { FishHookPDCHandler.getUUID(it) == player.uniqueId }
        if (hook != null){
            BobberHandler.removeBobberByHook(hook)
        }
    }

    fun handleCatch(player: Player, hook: FishHook){
        val rod = RodUtils.getPlayerRod(player)
        if (RodUtils.isBaitedRod(rod)) {
            handleEntity(player, hook)
            RodItem.removeSingleBaitFromRod(rod)
            RodUtils.updateFishingRodDurability(rod)
        } else {
            handleCustomLoot(player, hook)
            RodUtils.updateFishingRodDurability(rod)
        }
    }

    private fun handleEntity(player: Player, hook: FishHook) {
        val entityType = if (player.location.world.name == "world_the_end") {
            listOf(EntityType.ENDERMITE, EntityType.PHANTOM, EntityType.SHULKER_BULLET)[ThreadLocalRandom.current().nextInt(2)]
        } else {
            getRandomFish()
        }

        val hookLoc = hook.location
        val entity = hook.world.spawnEntity(hookLoc.subtract(0.0, -2.0, 0.0), entityType)

        if (entity !is Fish) return
        val newFish = entity

        val velocity = calculateVelocity(player.location, newFish.location)
        newFish.velocity = newFish.velocity.add(velocity)
        hook.hookedEntity = newFish
        hook.pullHookedEntity()
    }

    private fun handleCustomLoot(player: Player, hook: FishHook) {
        val playerRod = RodUtils.getPlayerRod(player)

        val biome = hook.location.block.biome
        var luckEnchantmentModifier = 1.0

        if (playerRod.hasItemMeta() && playerRod.itemMeta.hasEnchants()
            && playerRod.itemMeta.hasEnchant(Enchantment.LUCK_OF_THE_SEA)) {
            val enchantmentLvl = playerRod.itemMeta.getEnchantLevel(Enchantment.LUCK_OF_THE_SEA)
            luckEnchantmentModifier = when(enchantmentLvl) {
                1 -> 1.1
                2 -> 1.2
                3 -> 1.3
                else -> 1.0
            }
        }

        val droppedItem = generateRandomItemByBiome(biome, luckEnchantmentModifier)

        val item = hook.world.dropItemNaturally(hook.location, droppedItem)

        val dx = player.location.x - item.location.x
        val dy = player.location.y - item.location.y
        val dz = player.location.z - item.location.z

        val length = sqrt(dx * dx + dy * dy + dz * dz)
        val normalizedDx = dx / length
        val normalizedDy = dy / length
        val normalizedDz = dz / length

        val distance = item.velocity.add(
            Vector(
                normalizedDx * PUSH_SPEED_ITEM,
                normalizedDy * PUSH_SPEED_ITEM,
                normalizedDz * PUSH_SPEED_ITEM
            )
        )

        item.velocity = distance
        hook.hookedEntity = item
        hook.pullHookedEntity()
    }

    private fun calculateVelocity(from: Location, to: Location): Vector {
        val dx = from.x - to.x
        val dy = from.y - to.y
        val dz = from.z - to.z
        val length = sqrt(dx * dx + dy * dy + dz * dz)
        if (length == 0.0) return Vector(0, 0, 0)
        return Vector(
            dx / length * PUSH_SPEED_FISH,
            dy / length * PUSH_SPEED_FISH,
            dz / length * PUSH_SPEED_FISH
        )
    }

    private const val PUSH_SPEED_ITEM = 0.05
    private const val PUSH_SPEED_FISH = 0.2

    fun generateRandomItemByBiome(fishingBiome: Biome, luckEnchantmentModifier: Double): ItemStack {
        val droppedItem = when(fishingBiome) {
            Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN ->
                FishingLootManager.LOOT_TABLE_WARM.getRandomLoot(luckEnchantmentModifier)
            Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN ->
                FishingLootManager.LOOT_TABLE_ICE.getRandomLoot(luckEnchantmentModifier)
            Biome.RIVER -> FishingLootManager.LOOT_TABLE_RIVER.getRandomLoot(luckEnchantmentModifier)
            Biome.THE_END -> FishingLootManager.LOOT_TABLE_END.getEndLoot(luckEnchantmentModifier)
            else -> FishingLootManager.LOOT_TABLE_DEFAULT.getRandomLoot(luckEnchantmentModifier)
        }

        val droppedMeta = droppedItem.itemMeta
        val droppedDamage = droppedMeta as Damageable
        var randomDamage = 0

        when {
            listOf(Material.FISHING_ROD, Material.FLINT_AND_STEEL).contains(droppedItem.type) -> {
                randomDamage = ThreadLocalRandom.current().nextInt(35, 42 + 1)
            }
            droppedItem.type == Material.TRIDENT -> {
                randomDamage = ThreadLocalRandom.current().nextInt(120, 230 + 1)
            }
            listOf(Material.STONE_PICKAXE, Material.IRON_PICKAXE, Material.GOLDEN_PICKAXE).contains(droppedItem.type) -> {
                randomDamage = when(droppedItem.type) {
                    Material.STONE_PICKAXE -> ThreadLocalRandom.current().nextInt(80, 100 + 1)
                    Material.IRON_PICKAXE -> ThreadLocalRandom.current().nextInt(180, 220 + 1)
                    Material.GOLDEN_PICKAXE -> ThreadLocalRandom.current().nextInt(10, 20 + 1)
                    else -> 0
                }
            }
        }

        droppedDamage.damage = randomDamage
        droppedItem.itemMeta = droppedMeta
        return droppedItem
    }

    fun getRandomFish(): EntityType {
        val fishList = listOf(EntityType.COD, EntityType.SALMON, EntityType.TROPICAL_FISH, EntityType.PUFFERFISH)
        val randomIndex = ThreadLocalRandom.current().nextInt(fishList.size)
        return fishList[randomIndex]
    }

}