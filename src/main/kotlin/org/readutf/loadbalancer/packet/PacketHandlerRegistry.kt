package org.readutf.loadbalancer.packet

import net.minestom.server.network.packet.client.ClientPacket
import org.jctools.queues.SpmcArrayQueue
import org.readutf.loadbalancer.client.Client
import kotlin.reflect.KClass

class PacketHandlerRegistry(
    handlers: List<RegisteredListener<*>>,
) {
    private val packetHandlers = SpmcArrayQueue<RegisteredListener<*>>(handlers.size)

    init {
        handlers.forEach {
            packetHandlers.add(it)
        }
    }

    fun handlePacket(
        client: Client,
        clientPacket: ClientPacket,
    ) {
        packetHandlers.forEach { packet ->
            if (packet.kClass == clientPacket::class) {
                packet.packetListener(client, clientPacket)
            }
        }
    }

    class RegisteredListener<T : ClientPacket>(
        val kClass: KClass<T>,
        val packetListener: (Client, ClientPacket) -> Unit,
    )
}
