package org.readutf.loadbalancer.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.packet.PacketListener

class ClientHandshakeListener : PacketListener<ClientHandshakePacket> {
    private val logger = KotlinLogging.logger { }

    override fun handlePacket(
        client: Client,
        packet: ClientHandshakePacket,
    ) {
        client.handshakeInfo = packet
    }
}
