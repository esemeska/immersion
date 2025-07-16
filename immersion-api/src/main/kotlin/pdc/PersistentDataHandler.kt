package ua.senalll.immersionapi.pdc

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType

interface PersistentDataHandler<T: Any> {
    val key: NamespacedKey
    val type: PersistentDataType<*, T>

    fun set(container: PersistentDataContainer, value: T) {
        container.set(key, type, value)
    }

    fun get(container: PersistentDataContainer): T? =
        if (container.has(key, type)) container.get(key, type) else null

    fun has(container: PersistentDataContainer): Boolean =
        container.has(key, type)

    fun remove(container: PersistentDataContainer) {
        container.remove(key)
    }
}