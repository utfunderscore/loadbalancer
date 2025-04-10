package org.readutf.loadbalancer.listeners

import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.sksamuel.hoplite.watch.ReloadableConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.network.packet.client.common.ClientSettingsPacket
import net.minestom.server.network.packet.server.common.DisconnectPacket
import net.minestom.server.network.packet.server.common.TransferPacket
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.packet.PacketListener
import org.readutf.loadbalancer.settings.BalancerSettings
import java.util.function.Supplier

class ClientSettingsListener(
    val serverFinder: Supplier<ServerFinder>,
    val settings: ReloadableConfig<BalancerSettings>,
) : PacketListener<ClientSettingsPacket> {
    private val logger = KotlinLogging.logger { }
    private val miniMessage = MiniMessage.miniMessage()

    override fun handlePacket(
        client: Client,
        packet: ClientSettingsPacket,
    ) {
        var player =
            client.toPlayer().getOrElse {
                client.sendPacket(DisconnectPacket(Component.text("Internal error occured.").color(NamedTextColor.RED)))
                logger.error { "Failed to get player from client: ${it.message}" }
                return
            }

        serverFinder
            .get()
            .findServer(player)
            .onSuccess { server ->
                logger.info { "Transferred ${player.getUsername()} to ${server.address}" }
                client.sendPacket(TransferPacket(server.address, server.port))
            }.onFailure {
                logger.error { "Server not found for ${player.getUsername()}" }
                client.disconnect(miniMessage.deserialize(settings.getLatest().connectionError))
            }
    }
}
