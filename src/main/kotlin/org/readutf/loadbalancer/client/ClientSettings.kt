package org.readutf.loadbalancer.client

data class ClientSettings(
    val protocolVersion: Int,
    val serverAddress: String,
    val serverPort: Int,
)
