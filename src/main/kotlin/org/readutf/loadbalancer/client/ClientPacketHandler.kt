package org.readutf.loadbalancer.client

import net.minestom.server.network.packet.client.ClientPacket
import org.readutf.loadbalancer.packet.PacketHandlerRegistry
import java.util.function.Consumer

class ClientPacketHandler(
    val client: Client,
    val packetHandlerRegistry: PacketHandlerRegistry,
) : Consumer<ClientPacket> {
    override fun accept(clientPacket: ClientPacket) {
        packetHandlerRegistry.handlePacket(client, clientPacket)
    }
}
