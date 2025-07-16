package ua.senalll.immersionapi.module

open class ModuleException(message: String, cause: Throwable? = null): Exception(message, cause)

class ModuleLoadException(
    moduleName: String,
    cause: Throwable? = null
) : ModuleException("Ошибка загрузки модуля: $moduleName", cause)

class ModuleEnableException(
    moduleName: String,
    cause: Throwable? = null
) : ModuleException("Ошибка включения модуля: $moduleName", cause)

class ModuleDisableException(
    moduleName: String,
    cause: Throwable? = null
) : ModuleException("Ошибка выключения модуля: $moduleName", cause)