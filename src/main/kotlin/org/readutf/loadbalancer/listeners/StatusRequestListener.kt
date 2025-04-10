package org.readutf.loadbalancer.listeners

import com.sksamuel.hoplite.watch.ReloadableConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.minestom.server.network.packet.client.handshake.ClientHandshakePacket
import net.minestom.server.network.packet.client.status.StatusRequestPacket
import net.minestom.server.network.packet.server.status.ResponsePacket
import org.readutf.loadbalancer.Loadbalancer
import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.packet.PacketListener
import org.readutf.loadbalancer.packet.StatusRequestResponse
import org.readutf.loadbalancer.settings.BalancerSettings

class StatusRequestListener(
    val balancerSettings: ReloadableConfig<BalancerSettings>,
) : PacketListener<StatusRequestPacket> {
    private val logger = KotlinLogging.logger { }

    private val componentSerializer = GsonComponentSerializer.gson()
    private val miniMessage = MiniMessage.miniMessage()

    override fun handlePacket(
        client: Client,
        packet: StatusRequestPacket,
    ) {
        logger.info { "Server list ping from ${client.socketChannel.remoteAddress}" }

        val handshakeInfo: ClientHandshakePacket? = client.handshakeInfo

        if (handshakeInfo == null) {
            logger.warn { "Handshake info is null" }
            client.disconnect(Component.text("Internal error occurred").color(NamedTextColor.RED))
            return
        }

        var jsonResponse =
            Loadbalancer.gson.toJson(
                StatusRequestResponse(
                    protocolName = "Loadbalancer",
                    protocolVersion = handshakeInfo.protocolVersion(),
                    maxPlayers = 1000,
                    onlinePlayers = 0,
                    description = miniMessage.deserialize(balancerSettings.getLatest().motd),
                    enforcesSecureChat = false,
                    previewsChat = false,
                ).build(componentSerializer),
            )

        client.sendPacket(
            ResponsePacket(
                jsonResponse,
            ),
        )
    }
}
