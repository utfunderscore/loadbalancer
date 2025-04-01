package org.readutf.loadbalancer.packet

import com.google.gson.reflect.TypeToken
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.readutf.loadbalancer.Loadbalancer

class StatusRequestResponse(
    val protocolName: String,
    val protocolVersion: Int,
    val maxPlayers: Int,
    val onlinePlayers: Int,
    val description: Component,
    val enforcesSecureChat: Boolean,
    val previewsChat: Boolean,
) {
    fun build(componentSerializer: GsonComponentSerializer): Map<String, Any> {
        var component = componentSerializer.serialize(description)

        return mapOf(
            "version" to
                mapOf(
                    "name" to protocolName,
                    "protocol" to protocolVersion,
                ),
            "players" to
                mapOf(
                    "max" to maxPlayers,
                    "online" to onlinePlayers,
                ),
            "description" to stringToMap(component),
            "enforcesSecureChat" to enforcesSecureChat,
            "previewsChat" to previewsChat,
        )
    }

    fun stringToMap(jsonComponent: String): Map<String, Any> =
        Loadbalancer.gson.fromJson(jsonComponent, object : TypeToken<Map<String, Any>>() {})
}
