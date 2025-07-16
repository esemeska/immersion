package ua.senalll.immersionCore.nickname

enum class NickNameType(val typeString: String) {
    NO_NICK("default"),
    FULLNAME("fullname"),
    NICKNAME("nickname");

    companion object {
        fun fromString(value: String): NickNameType {
            return entries.find { it.typeString.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Unknown NickNameType: $value")
        }
    }
}