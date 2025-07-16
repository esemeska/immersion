package ua.senalll.immersionLitematica.schematic.json

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import net.querz.nbt.tag.*

object JsonCompoundTag {
    fun compoundTagToJsonString(tag: CompoundTag): String {
        val gson = Gson()
        val json = compoundToJsonObject(tag)
        return gson.toJson(json)
    }

    fun compoundToJsonObject(tag: CompoundTag): JsonObject {
        val json = JsonObject()

        tag.keySet().forEach { key ->
            val value = tag.get(key)
            json.add(key, convertTagToJson(value))
        }

        return json
    }

    fun convertTagToJson(tag: Tag<*>?): JsonElement {
        return when (tag) {
            is CompoundTag -> compoundToJsonObject(tag)
            is ListTag<*> -> {
                val jsonArray = JsonArray()
                tag.forEach { item -> jsonArray.add(convertTagToJson(item)) }
                jsonArray
            }
            is ByteTag -> JsonPrimitive(tag.asByte())
            is ShortTag -> JsonPrimitive(tag.asShort())
            is IntTag -> JsonPrimitive(tag.asInt())
            is LongTag -> JsonPrimitive(tag.asLong())
            is FloatTag -> JsonPrimitive(tag.asFloat())
            is DoubleTag -> JsonPrimitive(tag.asDouble())
            is StringTag -> JsonPrimitive(tag.valueToString())
            is IntArrayTag -> {
                val jsonArray = JsonArray()
                tag.value.forEach { jsonArray.add(it) }
                jsonArray
            }
            is LongArrayTag -> {
                val jsonArray = JsonArray()
                tag.value.forEach { jsonArray.add(it) }
                jsonArray
            }
            is ByteArrayTag -> {
                val jsonArray = JsonArray()
                tag.value.forEach { jsonArray.add(it.toInt()) }
                jsonArray
            }
            else -> JsonNull.INSTANCE
        }
    }

}