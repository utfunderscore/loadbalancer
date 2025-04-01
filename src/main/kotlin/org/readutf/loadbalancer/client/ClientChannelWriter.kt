package org.readutf.loadbalancer.client

import net.minestom.server.network.NetworkBuffer
import java.util.function.Consumer

class ClientChannelWriter(
    val client: Client,
) : Consumer<NetworkBuffer> {
    override fun accept(t: NetworkBuffer) {
        t.writeChannel(client.socketChannel)
    }
}
