package org.readutf.loadbalancer.listeners

import com.sksamuel.hoplite.watch.ReloadableConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import net.minestom.server.network.packet.client.login.ClientLoginStartPacket
import net.minestom.server.network.packet.server.login.LoginSuccessPacket
import net.minestom.server.network.player.GameProfile
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.packet.PacketListener
import org.readutf.loadbalancer.settings.BalancerSettings
import org.readutf.loadbalancer.utils.AddressUtils

class LoginStartListener(
    val reloadableConfig: ReloadableConfig<BalancerSettings>,
) : PacketListener<ClientLoginStartPacket> {
    private val logger = KotlinLogging.logger { }

    override fun handlePacket(
        client: Client,
        packet: ClientLoginStartPacket,
    ) {
        val latest = reloadableConfig.getLatest()

        logger.info { "New connection from ${AddressUtils.redact(client.socketChannel.remoteAddress, latest)}" }

        var gameProfile = GameProfile(packet.profileId, packet.username)
        client.gameProfile = gameProfile
        client.sendPacket(LoginSuccessPacket(gameProfile))
    }
}
