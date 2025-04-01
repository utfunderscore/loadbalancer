package org.readutf.loadbalancer.listeners

import net.minestom.server.network.packet.client.common.ClientPingRequestPacket
import net.minestom.server.network.packet.server.common.PingResponsePacket
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.packet.PacketListener

class PingRequestListener : PacketListener<ClientPingRequestPacket> {
    override fun handlePacket(
        client: Client,
        packet: ClientPingRequestPacket,
    ) {
        client.sendPacket(PingResponsePacket(packet.number))
    }
}
