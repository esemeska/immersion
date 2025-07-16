package ua.senalll.immersionLitematica.display.serialize

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.data.BlockData
import org.bukkit.util.Vector
import java.util.UUID

object Jackson {

    val objectMapper: ObjectMapper = ObjectMapper().apply {
        registerModule(kotlinModule())
        registerModule(createMinecraftModule())
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    private fun createMinecraftModule(): SimpleModule {
        val module = SimpleModule("MinecraftModule")

        module.addSerializer(Location::class.java, LocationSerializer())
        module.addDeserializer(Location::class.java, LocationDeserializer())

        module.addSerializer(Vector::class.java, VectorSerializer())
        module.addDeserializer(Vector::class.java, VectorDeserializer())

        module.addSerializer(BlockData::class.java, BlockDataSerializer())
        module.addDeserializer(BlockData::class.java, BlockDataDeserializer())

        module.addSerializer(UUID::class.java, UUIDSerializer())
        module.addDeserializer(UUID::class.java, UUIDDeserializer())

        return module
    }


    class LocationSerializer : JsonSerializer<Location>() {
        override fun serialize(location: Location, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeStartObject()
            gen.writeStringField("world", location.world?.name ?: "world")
            gen.writeNumberField("x", location.x)
            gen.writeNumberField("y", location.y)
            gen.writeNumberField("z", location.z)
            gen.writeNumberField("yaw", location.yaw)
            gen.writeNumberField("pitch", location.pitch)
            gen.writeEndObject()
        }
    }

    class LocationDeserializer : JsonDeserializer<Location>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Location {
            val node = p.codec.readTree<JsonNode>(p)
            val worldName = node.get("world").asText()
            val x = node.get("x").asDouble()
            val y = node.get("y").asDouble()
            val z = node.get("z").asDouble()
            val yaw = node.get("yaw")?.asDouble()?.toFloat() ?: 0f
            val pitch = node.get("pitch")?.asDouble()?.toFloat() ?: 0f

            val world = Bukkit.getWorld(worldName) ?: Bukkit.getWorlds()[0]
            return Location(world, x, y, z, yaw, pitch)
        }
    }

    class VectorSerializer : JsonSerializer<Vector>() {
        override fun serialize(vector: Vector, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeStartObject()
            gen.writeNumberField("x", vector.x)
            gen.writeNumberField("y", vector.y)
            gen.writeNumberField("z", vector.z)
            gen.writeEndObject()
        }
    }

    class VectorDeserializer : JsonDeserializer<Vector>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Vector {
            val node = p.codec.readTree<JsonNode>(p)
            val x = node.get("x").asDouble()
            val y = node.get("y").asDouble()
            val z = node.get("z").asDouble()
            return Vector(x, y, z)
        }
    }

    class BlockDataSerializer : JsonSerializer<BlockData>() {
        override fun serialize(blockData: BlockData, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString(blockData.asString)
        }
    }

    class BlockDataDeserializer : JsonDeserializer<BlockData>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BlockData {
            val asString = p.valueAsString
            return Bukkit.createBlockData(asString)
        }
    }

    class UUIDSerializer : JsonSerializer<UUID>() {
        override fun serialize(uuid: UUID, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString(uuid.toString())
        }
    }

    class UUIDDeserializer : JsonDeserializer<UUID>() {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): UUID {
            return UUID.fromString(p.valueAsString)
        }
    }
}