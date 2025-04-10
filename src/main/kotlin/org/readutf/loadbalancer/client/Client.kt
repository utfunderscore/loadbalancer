package org.readutf.loadbalancer.client

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import net.kyori.adventure.text.Component
import net.minestom.scratch.network.NetworkContext
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket
import net.minestom.server.network.packet.server.ServerPacket
import net.minestom.server.network.packet.server.common.DisconnectPacket
import net.minestom.server.network.player.GameProfile
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicBoolean

class Client(
    val socketChannel: SocketChannel,
    val networkContext: NetworkContext.Async = NetworkContext.Async(),
) {
    var writerThread: Thread? = null
    var readerThread: Thread? = null
    var gameProfile: GameProfile? = null
    var handshakeInfo: ClientHandshakePacket? = null
    val connected = AtomicBoolean(true)

    fun sendPacket(serverPacket: ServerPacket) {
        networkContext.write(serverPacket)
    }

    fun disconnect(message: Component) {
        sendPacket(DisconnectPacket(message))
    }

    fun toPlayer(): Result<Player, Throwable> {
        val gameProfile = gameProfile ?: return Err(IllegalStateException("Game profile is not set"))
        val handshakeInfo = handshakeInfo ?: return Err(IllegalStateException("Handshake info is not set"))
        val clientSettings =
            ClientSettings(
                protocolVersion = handshakeInfo.protocolVersion,
                serverAddress = handshakeInfo.serverAddress,
                serverPort = handshakeInfo.serverPort,
            )
        return Ok(Player(this, gameProfile, clientSettings))
    }
}
