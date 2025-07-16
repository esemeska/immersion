package ua.senalll.immersionapi.pdc

import org.bukkit.NamespacedKey


/***
 * ImmersionAPI.instance.pdcRegistry.register(CustomPDCHandler)
 */
class PersistentDataRegistry {
    private val handlers = mutableMapOf<NamespacedKey, PersistentDataHandler<*>>()

    fun <T : Any> register(handler: PersistentDataHandler<T>) {
        if (handlers.containsKey(handler.key)) {
            throw IllegalArgumentException("Handler with key '${handler.key}' is already registered!")
        }
        handlers[handler.key] = handler
    }

    fun getHandler(key: NamespacedKey): PersistentDataHandler<*> = handlers[key]!!
}