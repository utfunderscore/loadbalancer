package org.readutf.loadbalancer

import com.google.gson.Gson
import com.sksamuel.hoplite.watch.ReloadableConfig
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.impl.GeoServerFinder
import org.readutf.loadbalancer.finder.impl.HttpServerFinder
import org.readutf.loadbalancer.finder.impl.StaticServerFinder
import org.readutf.loadbalancer.listeners.ClientHandshakeListener
import org.readutf.loadbalancer.listeners.ClientSettingsListener
import org.readutf.loadbalancer.listeners.LoginStartListener
import org.readutf.loadbalancer.listeners.PingRequestListener
import org.readutf.loadbalancer.listeners.StatusRequestListener
import org.readutf.loadbalancer.network.NetworkManager
import org.readutf.loadbalancer.packet.PacketManager
import org.readutf.loadbalancer.settings.BalancerSettings
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Properties
import java.util.Scanner
import java.util.function.Supplier
import kotlin.io.path.Path
import kotlin.system.exitProcess

class Loadbalancer {
    private val logger = KotlinLogging.logger { }

    init {
        logger.info { "Starting loadbalancer..." }
    }

    private val settings = BalancerSettings.load(workDir = Path(System.getProperty("user.dir")))

    val loadBalancer: Supplier<ServerFinder> = createServerFinder(settings)

    val packetManager =
        PacketManager.also {
            it.registerListener(LoginStartListener())
            it.registerListener(ClientHandshakeListener())
            it.registerListener(StatusRequestListener(settings))
            it.registerListener(PingRequestListener())
            it.registerListener(ClientSettingsListener(loadBalancer, settings))
        }

    init {

        val settings = settings.getLatest()
        val networkManager =
            NetworkManager(
                packetManager.createHandlerRegistry(),
                settings.createInetSocket(),
            )

        logger.info { "Server started on ${settings.host}:${settings.port}." }

        val commandScanner = Scanner(System.`in`)
        while (commandScanner.hasNextLine()) {
            val line = commandScanner.nextLine()
            if ("stop".equals(line, ignoreCase = true)) {
                logger.info { "Shutting down..." }
                break
            }
        }

        networkManager.shutdown()
        exitProcess(1)
    }

    fun createServerFinder(settings: ReloadableConfig<BalancerSettings>): Supplier<ServerFinder> =
        Supplier {
            val settings = settings.getLatest()
            if (settings.http.enabled) {
                HttpServerFinder(settings.http.host, settings.http.hostPath, settings.http.portPath)
            } else if (settings.static.enabled) {
                StaticServerFinder(settings.static.targets)
            } else if (settings.geo.enabled) {
                GeoServerFinder(settings.geo)
            } else {
                error("No server finder is enabled")
            }
        }

    companion object {
        val gson = Gson()
    }
}

fun main() {
    val properties = Properties()
    properties.load(Loadbalancer::class.java.getResourceAsStream("/version.properties"))

    val version = properties.getOrDefault("version", "UNKNOWN")
    val builtAt = properties.getOrDefault("buildTime", "UNKNOWN") as String

    val formattedBuildTime = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss").format(Date(builtAt.toLong()))
    println("Running Loadbalancer v$version built on $formattedBuildTime")

    Loadbalancer()
}
