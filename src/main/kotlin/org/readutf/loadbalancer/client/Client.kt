package org.readutf.loadbalancer.client

import net.minestom.scratch.network.NetworkContext
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.player.GameProfile
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicBoolean

class Client(
    val socketChannel: SocketChannel,
    val networkContext: NetworkContext.Async = NetworkContext.Async(),
) {
    val start = System.nanoTime()

    val connected = AtomicBoolean(true)
    lateinit var writerThread: Thread
    lateinit var readerThread: Thread
    lateinit var gameProfile: GameProfile

    lateinit var handshakeInfo: ClientHandshakePacket

    fun sendPacket(serverPacket: ServerPacket) {
        networkContext.write(serverPacket)
    }

    fun onDisconnect() {
    }
}
