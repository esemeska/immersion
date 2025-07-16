package ua.senalll.immersionCore.config.yml

class ChatConfig(private val config: CoreConfig) {

    fun ensureChatDefaults(){
        val joinMessages = listOf(
            "%player% зашёл на сервер... зачем только?",
            "%player% подключился",
            "Самый главный фанат Куроми %player% зашёл на сервер",
            "%player% решил зайти на сервер, чтобы докопать ары для сеналла",
            "%player% зашёл на сервер, чтобы помочь твину построить город"
        )

        val quitMessages = listOf(
            "%player% сказал, что больше никогда не зайдёт на сервер",
            "%player% решил не тратить свою жизнь на Куроми...",
            "%player% отключился... от сервера в плане"
        )

        val chatDefaults = mapOf<String, Any?>(
            "chat.join.types" to joinMessages,
            "chat.quit.types" to quitMessages,
            "chat.local.radius" to 100,
            "chat.global.prefix" to "!"
        )

        val chatRPCommands = mapOf<String, Any?>(
            "chat.commands.coin.enabled" to true,
            "chat.commands.coin.format" to "§8*§6 %sender% §fподбросил монетку (§6%result%§f) §8",
            "chat.commands.coin.radius" to 22,
            "chat.commands.coin.first-face" to "§6Куроми",
            "chat.commands.coin.second-face" to "§6Мелоди",

            "chat.commands.do.enabled" to true,
            "chat.commands.do.format" to "§6*§f %sender% %action% §e",
            "chat.commands.do.usage" to "[§c!§f] §cИспользуй: /do <действие>",
            "chat.commands.do.radius" to 22,

            "chat.commands.me.enabled" to true,
            "chat.commands.me.format" to "§7* %sender% §o%message% *",
            "chat.commands.me.usage" to "[§c!§f] §cИспользуй: /me <сообщение>",
            "chat.commands.me.radius" to 22,

            "chat.commands.try.enabled" to true,
            "chat.commands.try.format" to "§e*§f %sender%: %action% (%result%§f)",
            "chat.commands.try.usage" to "[§c!§f] §cИспользуй: /try <действие>",
            "chat.commands.try.radius" to 22,
            "chat.commands.try.results" to listOf("§aУспешно", "§cНеуспешно"),

            "chat.commands.whisp.enabled" to true,
            "chat.commands.whisp.format" to "§7§o%sender% шепчет: %message%",
            "chat.commands.whisp.usage" to "[§c!§f] §cИспользуй: /whisper <сообщение>",
            "chat.commands.whisp.radius" to 10,
        )
        config.ensureDefaults(chatDefaults)
        config.ensureDefaults(chatRPCommands)
    }

    fun coinCmd(): Map<String, Any?> {
        return mapOf(
            "enabled" to config.getBoolean("chat.commands.coin.enabled"),
            "format" to config.getString("chat.commands.coin.format"),
            "radius" to config.getInt("chat.commands.coin.radius"),
            "first-face" to config.getString("chat.commands.coin.first-face"),
            "second-face" to config.getString("chat.commands.coin.second-face")
        )
    }

    fun doCmd(): Map<String, Any?> {
        return mapOf(
            "enabled" to config.getBoolean("chat.commands.do.enabled"),
            "format" to config.getString("chat.commands.do.format"),
            "usage" to config.getString("chat.commands.do.usage"),
            "radius" to config.getInt("chat.commands.do.radius")
        )
    }

    fun meCmd(): Map<String, Any?> {
        return mapOf(
            "enabled" to config.getBoolean("chat.commands.me.enabled"),
            "format" to config.getString("chat.commands.me.format"),
            "usage" to config.getString("chat.commands.me.usage"),
            "radius" to config.getInt("chat.commands.me.radius")
        )
    }

    fun tryCmd(): Map<String, Any?> {
        return mapOf(
            "enabled" to config.getBoolean("chat.commands.try.enabled"),
            "format" to config.getString("chat.commands.try.format"),
            "usage" to config.getString("chat.commands.try.usage"),
            "radius" to config.getInt("chat.commands.try.radius"),
            "results" to config.getStringList("chat.commands.try.results")
        )
    }

    fun whispCmd(): Map<String, Any?> {
        return mapOf(
            "enabled" to config.getBoolean("chat.commands.whisp.enabled"),
            "format" to config.getString("chat.commands.whisp.format"),
            "usage" to config.getString("chat.commands.whisp.usage"),
            "radius" to config.getInt("chat.commands.whisp.radius")
        )
    }

    fun localChatRadius(): Int {
        return config.getInt("chat.local.radius")
    }

    fun globalChatSymbol(): String {
        return config.getString("chat.global.prefix")!!
    }
}