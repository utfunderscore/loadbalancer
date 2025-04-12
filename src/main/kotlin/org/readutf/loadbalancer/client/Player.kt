package org.readutf.loadbalancer.client

import net.minestom.server.network.player.GameProfile

class Player(
    val client: Client,
    val gameProfile: GameProfile,
    val clientSettings: ClientSettings,
) {
    fun getConnection() = client.socketChannel

    fun getUsername() = gameProfile.name

    fun getPlayerId() = gameProfile.uuid

    fun getServerListAddress() = clientSettings.serverAddress

    fun getAddress() = client.socketChannel.remoteAddress
}
