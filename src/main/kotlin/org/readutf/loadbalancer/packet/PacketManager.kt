package org.readutf.loadbalancer.packet

import net.minestom.server.network.packet.client.ClientPacket
import kotlin.reflect.KClass
import kotlin.reflect.cast

object PacketManager {
    private val packetHandlers: MutableList<PacketHandlerRegistry.RegisteredListener<*>> = mutableListOf()

    inline fun <reified T : ClientPacket> registerListener(packetListener: PacketListener<T>) {
        registerListener(T::class, packetListener)
    }

    fun <T : ClientPacket> registerListener(
        packetClass: KClass<T>,
        packetListener: PacketListener<T>,
    ) {
        packetHandlers.add(
            PacketHandlerRegistry.RegisteredListener(packetClass) { client, packet ->
                packetListener.handlePacket(client, packetClass.cast(packet))
            },
        )
    }

    fun createHandlerRegistry() = PacketHandlerRegistry(packetHandlers)
}
