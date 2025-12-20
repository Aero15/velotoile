package xyz.doocode.velotoile.core.api

import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonDeserializationContext
import java.lang.reflect.Type
import java.time.Instant

object DateDeserializer : JsonDeserializer<Long> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Long {
        return try {
            when {
                json?.isJsonPrimitive == true -> {
                    val value = json.asString
                    // Si c'est déjà un nombre (timestamp en millisecondes)
                    value.toLongOrNull() ?: run {
                        // Sinon, essayer de parser une date ISO 8601
                        val instant = Instant.parse(value)
                        instant.toEpochMilli()
                    }
                }
                else -> 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}
