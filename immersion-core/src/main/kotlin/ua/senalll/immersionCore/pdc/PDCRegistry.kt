package ua.senalll.immersionCore.pdc

import ua.senalll.immersionapi.pdc.PersistentDataRegistry

class PDCRegistry {

    private val pdcRegistry = PersistentDataRegistry()

    private val tabPDCHandler = TabPDCHandler()
    private val vanishPDCHandler = VanishPDCHandler()
    private val relationPDCHandler = RelationPDCHandler()
    private val nickNamePDCHandler = NickNamePDCHandler()

    init {
        pdcRegistry.register(tabPDCHandler)
        pdcRegistry.register(vanishPDCHandler)
        pdcRegistry.register(relationPDCHandler)
        pdcRegistry.register(nickNamePDCHandler)
    }

    @Suppress("UNCHECKED_CAST")
    fun tabPDC(): TabPDCHandler {
        return pdcRegistry.getHandler(tabPDCHandler.key) as TabPDCHandler
    }

    @Suppress("UNCHECKED_CAST")
    fun vanishPDC(): VanishPDCHandler{
        return pdcRegistry.getHandler(vanishPDCHandler.key) as VanishPDCHandler
    }

    @Suppress("UNCHECKED_CAST")
    fun relationPDC(): RelationPDCHandler{
        return pdcRegistry.getHandler(relationPDCHandler.key) as RelationPDCHandler
    }

    @Suppress("UNCHECKED_CAST")
    fun nicknamePDC(): NickNamePDCHandler{
        return pdcRegistry.getHandler(nickNamePDCHandler.key) as NickNamePDCHandler
    }
}