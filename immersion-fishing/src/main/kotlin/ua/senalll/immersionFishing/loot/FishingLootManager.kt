package ua.senalll.immersionFishing.loot

import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.inventory.ItemStack
import java.util.EnumSet
import java.util.concurrent.ThreadLocalRandom
import kotlin.collections.iterator

enum class FishingLootManager(
    private val biomes: Set<Biome>,
    private val lootCategory: FishingLootData.LootCategory
) {
    LOOT_TABLE_WARM(
        EnumSet.of(Biome.WARM_OCEAN, Biome.LUKEWARM_OCEAN, Biome.DEEP_LUKEWARM_OCEAN),
        FishingLootData.LootCategory.WARM
    ),
    LOOT_TABLE_ICE(
        EnumSet.of(Biome.FROZEN_OCEAN, Biome.DEEP_FROZEN_OCEAN, Biome.COLD_OCEAN, Biome.DEEP_COLD_OCEAN),
        FishingLootData.LootCategory.ICE
    ),
    LOOT_TABLE_DEFAULT(
        EnumSet.of(Biome.OCEAN, Biome.DEEP_OCEAN),
        FishingLootData.LootCategory.DEFAULT
    ),
    LOOT_TABLE_RIVER(
        EnumSet.of(Biome.RIVER, Biome.FROZEN_RIVER),
        FishingLootData.LootCategory.RIVER
    ),
    LOOT_TABLE_END(
        EnumSet.of(Biome.THE_END),
        FishingLootData.LootCategory.END
    );


    companion object {
        private const val GARBAGE_CHANCE = 60.0
        private const val TOP_CHANCE = 10.0

        fun getByBiome(biome: Biome): FishingLootManager {
            return entries.find { it.biomes.contains(biome) } ?: LOOT_TABLE_DEFAULT
        }
    }

    fun getEndLoot(luckEnchantmentModifier: Double): ItemStack {
        return getRandomLootLoop(
            FishingLootData.getLoot(FishingLootData.LootCategory.END),
            ThreadLocalRandom.current().nextDouble() * 100 * luckEnchantmentModifier
        )
    }

    fun getRandomLoot(luckEnchantmentModifier: Double): ItemStack {
        val randomValue = ThreadLocalRandom.current().nextDouble() * 100 * luckEnchantmentModifier
        return when {
            randomValue >= GARBAGE_CHANCE -> {
                getRandomLootLoop(FishingLootData.getLoot(FishingLootData.LootCategory.GARBAGE),
                    randomValue)
            }
            randomValue >= TOP_CHANCE -> {
                getRandomLootLoop(FishingLootData.getLoot(FishingLootData.LootCategory.TOP),
                    randomValue)
            }
            else -> {
                getRandomLootLoop(FishingLootData.getLoot(FishingLootData.LootCategory.TOP), randomValue)
            }
        }
    }

    private fun getRandomLootLoop(map: Map<ItemStack, Double>, randomValue: Double): ItemStack {
        var remaining = randomValue
        for ((itemStack, value) in map) {
            remaining -= value
            if (remaining <= 0.0) {
                return itemStack.clone()
            }
        }
        return ItemStack(Material.OAK_PLANKS, 1)
    }
}