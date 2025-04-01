package org.readutf.loadbalancer.packet

import net.minestom.server.network.packet.client.ClientPacket
import org.readutf.loadbalancer.client.Client

fun interface PacketListener<T : ClientPacket> {
    fun handlePacket(
        client: Client,
        packet: T,
    )
}
