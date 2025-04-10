package org.readutf.loadbalancer.network

import org.readutf.loadbalancer.client.Client
import org.readutf.loadbalancer.client.ClientChannelReader
import org.readutf.loadbalancer.client.ClientChannelWriter
import org.readutf.loadbalancer.client.ClientPacketHandler
import org.readutf.loadbalancer.packet.PacketHandlerRegistry
import java.net.InetSocketAddress
import java.net.StandardProtocolFamily
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.atomic.AtomicBoolean

class NetworkManager(
    val handlerRegistry: PacketHandlerRegistry,
    address: InetSocketAddress,
) {
    private val socketChannel = ServerSocketChannel.open(StandardProtocolFamily.INET)
    private val active: AtomicBoolean = AtomicBoolean(true)

    val connectionThread = Thread(NetworkConnectionHandler(active, socketChannel, ::initClient))

    private val clients = mutableListOf<Client>()

    init {
        socketChannel.bind(address)
        connectionThread.isDaemon = true
        connectionThread.start()
    }

    fun initClient(channel: SocketChannel) {
        val client = Client(channel)
        val writerThread =
            Thread.startVirtualThread {
                clientReader(client)
            }
        val readerThread =
            Thread.startVirtualThread {
                clientWriter(client)
            }
        client.writerThread = writerThread
        client.readerThread = readerThread

        synchronized(client) { clients.add(client) }
    }

    /**
     * While the client is still active, continue to read from it, translate the packets,
     * and pass them to the packet handler.
     */
    private fun clientReader(client: Client) {
        while (client.connected.get()) {
            val connected = client.networkContext.read(ClientChannelReader(client), ClientPacketHandler(client, handlerRegistry))
            client.connected.set(connected)

            if (!connected) {
                clients.remove(client)
                break
            }
        }
    }

    /**
     * While the client is still active, flush the packet buffer to the client.
     */
    private fun clientWriter(client: Client) {
        while (client.connected.get()) {
            val connected = client.networkContext.write(ClientChannelWriter(client))
            client.connected.set(connected)
        }
    }

    fun shutdown() {
        active.set(false)
        socketChannel.close()
        clients.forEach {
            it.readerThread?.interrupt()
            it.writerThread?.interrupt()
        }
        clients.clear()
        connectionThread.interrupt()
    }

    class NetworkConnectionHandler(
        val activeTracker: AtomicBoolean,
        val serverSocketChannel: ServerSocketChannel,
        val initChannel: (SocketChannel) -> Unit,
    ) : Runnable {
        override fun run() {
            while (activeTracker.get()) {
                try {
                    val channel = serverSocketChannel.accept()
                    initChannel(channel)
                } catch (e: Exception) {
                }
            }
        }
    }
}
