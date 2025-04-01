package org.readutf.loadbalancer.listeners

import com.github.michaelbull.result.getOrElse
import com.sksamuel.hoplite.watch.ReloadableConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.network.packet.client.common.ClientSettingsPacket
import net.minestom.server.network.packet.server.common.DisconnectPacket
import net.minestom.server.network.packet.server.common.TransferPacket
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.packet.PacketListener
import org.readutf.loadbalancer.settings.BalancerSettings
import org.slf4j.LoggerFactory
import java.util.function.Supplier

class ClientSettingsListener(
    val serverFinder: Supplier<ServerFinder>,
    val settings: ReloadableConfig<BalancerSettings>,
) : PacketListener<ClientSettingsPacket> {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val miniMessage = MiniMessage.miniMessage()

    override fun handlePacket(
        client: Client,
        packet: ClientSettingsPacket,
    ) {
        val server =
            serverFinder.get().findServer(client.gameProfile.uuid).getOrElse {
                client.sendPacket(DisconnectPacket(miniMessage.deserialize(settings.getLatest().connectionError)))
                logger.error("Server not found for ${client.gameProfile.name}", it)
                return
            }

        client.sendPacket(TransferPacket(server.address, server.port))
    }
}
