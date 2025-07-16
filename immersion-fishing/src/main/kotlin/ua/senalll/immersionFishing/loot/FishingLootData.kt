package ua.senalll.immersionFishing.loot

import com.google.common.collect.ImmutableMap
import io.papermc.paper.potion.SuspiciousEffectEntry
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.SuspiciousStewMeta
import org.bukkit.potion.PotionEffectType

object FishingLootData {
    enum class LootCategory {
        RIVER, DEFAULT, WARM, ICE, GARBAGE, END, TOP
    }

    enum class LootItem(
        val material: Material,
        val amount: Int = 1,
        val weight: Double,
        val categories: Set<LootCategory>,
        val customizer: ((ItemStack) -> ItemStack)? = null
    ) {
        // River loot items
        BOOK(Material.BOOK, weight = 1.0, categories = setOf(LootCategory.RIVER)),
        FLOWER_POT(Material.FLOWER_POT, weight = 2.0, categories = setOf(LootCategory.RIVER)),
        CHAIN(Material.CHAIN, weight = 2.0, categories = setOf(LootCategory.RIVER)),
        FEATHER(Material.FEATHER, weight = 2.0, categories = setOf(LootCategory.RIVER)),
        LEVER(Material.LEVER, weight = 2.0, categories = setOf(LootCategory.RIVER)),
        RAW_COPPER(Material.RAW_COPPER, amount = 3, weight = 3.0, categories = setOf(LootCategory.RIVER)),
        PAPER(Material.PAPER, amount = 3, weight = 3.0, categories = setOf(LootCategory.RIVER)),

        // Food river items
        WHEAT(Material.WHEAT, amount = 2, weight = 5.0/4.0, categories = setOf(LootCategory.RIVER)),
        POTATO(Material.POTATO, amount = 2, weight = 5.0/4.0, categories = setOf(LootCategory.RIVER)),
        CARROT(Material.CARROT, amount = 2, weight = 5.0/4.0, categories = setOf(LootCategory.RIVER)),
        BEETROOT(Material.BEETROOT, amount = 2, weight = 5.0/4.0, categories = setOf(LootCategory.RIVER)),

        // Default loot items
        TURTLE_SCUTE_DEFAULT(Material.TURTLE_SCUTE, weight = 8.0, categories = setOf(LootCategory.DEFAULT)),
        KELP_DEFAULT(Material.KELP, weight = 12.0, categories = setOf(LootCategory.DEFAULT)),

        // Warm loot items
        TRIDENT(Material.TRIDENT, weight = 0.1, categories = setOf(LootCategory.WARM)),
        SPONGE(Material.SPONGE, weight = 0.9, categories = setOf(LootCategory.WARM)),
        TURTLE_SCUTE_WARM(Material.TURTLE_SCUTE, weight = 2.0, categories = setOf(LootCategory.WARM)),
        SEA_PICKLE(Material.SEA_PICKLE, weight = 6.0, categories = setOf(LootCategory.WARM)),
        KELP_WARM(Material.KELP, weight = 7.0, categories = setOf(LootCategory.WARM)),

        // Dead corral items
        DEAD_BRAIN_CORAL(Material.DEAD_BRAIN_CORAL, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_BUBBLE_CORAL(Material.DEAD_BUBBLE_CORAL, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_FIRE_CORAL(Material.DEAD_FIRE_CORAL, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_HORN_CORAL(Material.DEAD_HORN_CORAL, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_TUBE_CORAL(Material.DEAD_TUBE_CORAL, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_BRAIN_CORAL_FAN(Material.DEAD_BRAIN_CORAL_FAN, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_BUBBLE_CORAL_FAN(Material.DEAD_BUBBLE_CORAL_FAN, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_FIRE_CORAL_FAN(Material.DEAD_FIRE_CORAL_FAN, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_HORN_CORAL_FAN(Material.DEAD_HORN_CORAL_FAN, weight = 0.4, categories = setOf(LootCategory.WARM)),
        DEAD_TUBE_CORAL_FAN(Material.DEAD_TUBE_CORAL_FAN, weight = 0.4, categories = setOf(LootCategory.WARM)),

        // Ice loot items
        LANTERN(Material.LANTERN, weight = 2.0, categories = setOf(LootCategory.ICE)),
        SNOWBALL(Material.SNOWBALL, weight = 8.0, categories = setOf(LootCategory.ICE)),

        // Ice pickaxe items
        STONE_PICKAXE(Material.STONE_PICKAXE, weight = 4.0/3.0, categories = setOf(LootCategory.ICE)),
        IRON_PICKAXE(Material.IRON_PICKAXE, weight = 4.0/3.0, categories = setOf(LootCategory.ICE)),
        GOLDEN_PICKAXE(Material.GOLDEN_PICKAXE, weight = 4.0/3.0, categories = setOf(LootCategory.ICE)),

        // Ice nugget items
        GOLD_NUGGET_4(Material.GOLD_NUGGET, amount = 4, weight = 6.0/4.0, categories = setOf(LootCategory.ICE)),
        IRON_NUGGET_4(Material.IRON_NUGGET, amount = 4, weight = 6.0/4.0, categories = setOf(LootCategory.ICE)),
        GOLD_NUGGET_5(Material.GOLD_NUGGET, amount = 5, weight = 6.0/4.0, categories = setOf(LootCategory.ICE)),
        IRON_NUGGET_5(Material.IRON_NUGGET, amount = 5, weight = 6.0/4.0, categories = setOf(LootCategory.ICE)),

        // Garbage loot items
        LEATHER(Material.LEATHER, weight = 3.0, categories = setOf(LootCategory.GARBAGE)),
        GLASS_BOTTLE(Material.GLASS_BOTTLE, weight = 5.0, categories = setOf(LootCategory.GARBAGE)),
        STRING(Material.STRING, weight = 6.0, categories = setOf(LootCategory.GARBAGE)),
        BONE(Material.BONE, weight = 7.0, categories = setOf(LootCategory.GARBAGE)),
        STICK(Material.STICK, weight = 11.0, categories = setOf(LootCategory.GARBAGE)),
        OAK_STAIRS(Material.OAK_STAIRS, weight = 13.0, categories = setOf(LootCategory.GARBAGE)),
        OAK_PLANKS(Material.OAK_PLANKS, weight = 15.0, categories = setOf(LootCategory.GARBAGE)),

        // End loot items
        PHANTOM_MEMBRANE_END(Material.PHANTOM_MEMBRANE, weight = 35.0, categories = setOf(LootCategory.END)),
        ENDER_PEARL(Material.ENDER_PEARL, weight = 5.0, categories = setOf(LootCategory.END)),
        CHORUS_FRUIT(Material.CHORUS_FRUIT, weight = 10.0, categories = setOf(LootCategory.END)),
        CHORUS_PLANT(Material.CHORUS_PLANT, weight = 5.0, categories = setOf(LootCategory.END)),
        END_STONE(Material.END_STONE, weight = 20.0, categories = setOf(LootCategory.END)),
        END_STONE_BRICKS(Material.END_STONE_BRICKS, weight = 10.0, categories = setOf(LootCategory.END)),
        SHULKER_SHELL(Material.SHULKER_SHELL, weight = 5.0, categories = setOf(LootCategory.END)),
        LEVITATION_STEW(Material.SUSPICIOUS_STEW, weight = 10.0, categories = setOf(LootCategory.END),
            customizer = {
                val stew = it
                val meta = stew.itemMeta as SuspiciousStewMeta
                meta.clearCustomEffects()
                meta.addCustomEffect(SuspiciousEffectEntry.create(PotionEffectType.LEVITATION, 5), true)
                stew.itemMeta = meta
                stew
            }
        ),

        // Top loot items
        MENDING_BOOK(Material.ENCHANTED_BOOK, weight = 0.55, categories = setOf(LootCategory.TOP),
            customizer = {
                val book = it
                val meta = book.itemMeta as EnchantmentStorageMeta
                meta.addStoredEnchant(Enchantment.MENDING, 1, false)
                book.itemMeta = meta
                book
            }
        ),
        CONDUIT(Material.CONDUIT, weight = 0.55, categories = setOf(LootCategory.TOP)),
        NAME_TAG(Material.NAME_TAG, weight = 1.2, categories = setOf(LootCategory.TOP)),
        LEAD(Material.LEAD, weight = 1.5, categories = setOf(LootCategory.TOP)),
        FLINT_AND_STEEL(Material.FLINT_AND_STEEL, weight = 1.8, categories = setOf(LootCategory.TOP)),
        FISHING_ROD(Material.FISHING_ROD, weight = 2.0, categories = setOf(LootCategory.TOP)),
        PHANTOM_MEMBRANE_TOP(Material.PHANTOM_MEMBRANE, weight = 2.5, categories = setOf(LootCategory.TOP));

        fun createItem(): ItemStack {
            val item = ItemStack(material, amount)
            return customizer?.invoke(item) ?: item
        }
    }

    fun getLoot(category: LootCategory): Map<ItemStack, Double> {
        val lootMap = ImmutableMap.builder<ItemStack, Double>()

        LootItem.entries
            .filter { it.categories.contains(category) }
            .forEach { lootMap.put(it.createItem(), it.weight) }

        when (category) {
            LootCategory.RIVER -> lootMap.putAll(BooksLootManager.getRiverBooksLoot())
            LootCategory.DEFAULT -> lootMap.putAll(BooksLootManager.getDefaultBooksLoot())
            LootCategory.WARM -> lootMap.putAll(BooksLootManager.getWarmBooksLoot())
            LootCategory.ICE -> lootMap.putAll(BooksLootManager.getIceBooksLoot())
            else -> { /* Для других категорий книги не добавляем */ }
        }

        return lootMap.build()
    }

    fun createFoodRiverMap(): Map<ItemStack, Double> {
        return LootItem.entries
            .filter { it in listOf(LootItem.WHEAT, LootItem.POTATO, LootItem.CARROT, LootItem.BEETROOT) }
            .associate { it.createItem() to it.weight }
    }

    fun createDeadCorralMap(): Map<ItemStack, Double> {
        return LootItem.entries
            .filter { it.material.name.startsWith("DEAD_") && it.categories.contains(LootCategory.WARM) }
            .associate { it.createItem() to it.weight }
    }

    fun createNuggetIceMap(): Map<ItemStack, Double> {
        return LootItem.entries
            .filter { (it.material == Material.GOLD_NUGGET || it.material == Material.IRON_NUGGET)
                    && it.categories.contains(LootCategory.ICE) }
            .associate { it.createItem() to it.weight }
    }

    fun createPickaxeIceMap(): Map<ItemStack, Double> {
        return LootItem.entries
            .filter { it.material.name.endsWith("PICKAXE") && it.categories.contains(LootCategory.ICE) }
            .associate { it.createItem() to it.weight }
    }
}