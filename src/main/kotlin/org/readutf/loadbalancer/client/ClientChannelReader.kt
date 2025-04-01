package org.readutf.loadbalancer.client

import net.minestom.server.network.NetworkBuffer
import net.minestom.server.utils.time.Tick.client
import java.util.function.Consumer

class ClientChannelReader(
    val client: Client,
) : Consumer<NetworkBuffer> {
    override fun accept(buffer: NetworkBuffer) {
        buffer.readChannel(client.socketChannel)
    }
}
