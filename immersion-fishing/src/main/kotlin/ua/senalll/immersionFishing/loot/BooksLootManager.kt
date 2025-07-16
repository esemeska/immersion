package ua.senalll.immersionFishing.loot

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.jetbrains.annotations.NotNull
import kotlin.collections.setOf

class BooksLootManager {
    enum class EnchantedBook(
        private val enchantment: Enchantment,
        private val level: Int,
        val categories: Set<BookCategory> = setOf()
    ) {
        THORNS(Enchantment.THORNS, 2, setOf(BookCategory.RIVER, BookCategory.DEFAULT)),
        SMITE(Enchantment.SMITE, 3, setOf(BookCategory.RIVER, BookCategory.DEFAULT)),
        RESPIRATION(Enchantment.RESPIRATION, 2, setOf(BookCategory.RIVER, BookCategory.WARM)),
        POWER(Enchantment.POWER, 3, setOf(BookCategory.RIVER, BookCategory.DEFAULT)),
        PROTECTION(Enchantment.PROTECTION, 2, setOf(BookCategory.DEFAULT, BookCategory.WARM)),
        UNBREAKING(Enchantment.UNBREAKING, 2, setOf(BookCategory.DEFAULT, BookCategory.WARM, BookCategory.ICE)),
        FORTUNE(Enchantment.FORTUNE, 2, setOf(BookCategory.DEFAULT, BookCategory.WARM, BookCategory.ICE)),
        EFFICIENCY(Enchantment.EFFICIENCY, 3, setOf(BookCategory.DEFAULT, BookCategory.ICE)),
        BLAST_PROTECTION(Enchantment.BLAST_PROTECTION, 2, setOf(BookCategory.RIVER)),
        PROJECTILE_PROTECTION(Enchantment.PROJECTILE_PROTECTION, 2, setOf(BookCategory.RIVER)),
        SHARPNESS(Enchantment.SHARPNESS, 3, setOf(BookCategory.RIVER)),
        SWEEPING_EDGE(Enchantment.SWEEPING_EDGE, 2, setOf(BookCategory.DEFAULT)),
        LOYALTY(Enchantment.LOYALTY, 2, setOf(BookCategory.WARM)),
        CURSE_OF_VANISHING(Enchantment.VANISHING_CURSE, 1, setOf(BookCategory.WARM)),
        CURSE_OF_BINDING(Enchantment.BINDING_CURSE, 1, setOf(BookCategory.WARM)),
        DEPTH_STRIDER(Enchantment.DEPTH_STRIDER, 2, setOf(BookCategory.WARM)),
        BANE_OF_ARTHROPODS(Enchantment.BANE_OF_ARTHROPODS, 3, setOf(BookCategory.WARM)),
        FROST_WALKER(Enchantment.FROST_WALKER, 1, setOf(BookCategory.ICE)),
        SILK_TOUCH(Enchantment.SILK_TOUCH, 1, setOf(BookCategory.ICE));

        enum class BookCategory {
            RIVER, DEFAULT, WARM, ICE
        }

        fun createBook(): ItemStack {
            val book = ItemStack(Material.ENCHANTED_BOOK, 1)
            val meta = book.itemMeta as EnchantmentStorageMeta
            meta.addStoredEnchant(enchantment, level, false)
            book.itemMeta = meta
            return book
        }
    }

    data class WeightedBook(val book: EnchantedBook, val weight: Double = 1.0)

    companion object {
        @NotNull
        fun getBooksLoot(category: EnchantedBook.BookCategory): Map<ItemStack, Double> {
            val booksInCategory = EnchantedBook.entries
                .filter { it.categories.contains(category) }
                .map { it.createBook() }

            return createWeightedBooksMap(*booksInCategory.toTypedArray())
        }

        @NotNull
        fun getCustomBooksLoot(vararg weightedBooks: WeightedBook): Map<ItemStack, Double> {
            val totalWeight = weightedBooks.sumOf { it.weight }
            return weightedBooks.associate {
                it.book.createBook() to (it.weight * 10.0 / totalWeight)
            }
        }

        private fun createWeightedBooksMap(vararg books: ItemStack): Map<ItemStack, Double> {
            if (books.isEmpty()) return emptyMap()

            val weight = 10.0 / books.size
            return books.associateWith { weight }
        }

        @NotNull
        fun getRiverBooksLoot(): Map<ItemStack, Double> {
            return getBooksLoot(EnchantedBook.BookCategory.RIVER)
        }

        @NotNull
        fun getDefaultBooksLoot(): Map<ItemStack, Double> {
            return getBooksLoot(EnchantedBook.BookCategory.DEFAULT)
        }

        @NotNull
        fun getWarmBooksLoot(): Map<ItemStack, Double> {
            return getBooksLoot(EnchantedBook.BookCategory.WARM)
        }

        @NotNull
        fun getIceBooksLoot(): Map<ItemStack, Double> {
            return getBooksLoot(EnchantedBook.BookCategory.ICE)
        }
    }
}