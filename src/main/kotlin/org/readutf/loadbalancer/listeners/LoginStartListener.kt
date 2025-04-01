package org.readutf.loadbalancer.listeners

import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.network.packet.client.login.ClientLoginStartPacket
import net.minestom.server.network.packet.server.login.LoginSuccessPacket
import net.minestom.server.network.player.GameProfile
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.packet.PacketListener

class LoginStartListener : PacketListener<ClientLoginStartPacket> {
    private val logger = KotlinLogging.logger { }

    override fun handlePacket(
        client: Client,
        packet: ClientLoginStartPacket,
    ) {
        logger.info { "New connection from ${client.socketChannel.remoteAddress}" }
        var gameProfile = GameProfile(packet.profileId, packet.username)
        client.sendPacket(LoginSuccessPacket(gameProfile))
        client.gameProfile = gameProfile
    }
}
