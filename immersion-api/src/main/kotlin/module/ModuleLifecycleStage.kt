package ua.senalll.immersionapi.module

enum class ModuleLifecycleStage(val description: String) {
    LOAD("Loading module..."),
    ENABLE("Module enabled!"),
    UNLOAD("Unloading module..."),
    DISABLE("Module disabled!"),
    ERROR("Module ERROR!!!"),
    WARNING("Module Warning")
}