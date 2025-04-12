package org.readutf.loadbalancer.settings

import com.sksamuel.hoplite.ConfigAlias
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.watch.ReloadableConfig
import org.readutf.loadbalancer.finder.TargetServer
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.notExists

data class BalancerSettings(
    val host: String = "0.0.0.0",
    val port: Int = 25565,
    val motd: String = "<rainbow>Default loadbalancer config",
    @ConfigAlias("connection-error") val connectionError: String = "<red>Could not connect you to backend server.</red>",
    val static: StaticBalancerConfig = StaticBalancerConfig(),
    val http: HttpBalancerConfig = HttpBalancerConfig(),
    val geo: GeoBalancerConfig = GeoBalancerConfig(),
) {
    fun createInetSocket(): InetSocketAddress = InetSocketAddress(host, port)

    companion object {
        fun load(workDir: Path): ReloadableConfig<BalancerSettings> {
            val inputStream: InputStream? = BalancerSettings::class.java.getResourceAsStream("/settings.yml")

            if (inputStream == null) error("Could not load settings from $workDir")

            var destination = workDir.resolve("settings.yml")
            if (destination.notExists()) {
                Files.copy(inputStream, destination)
            }

            val loader =
                ConfigLoaderBuilder
                    .default()
                    .withReportPrintFn { }
                    .addFileSource("settings.yml")
                    .build()

            return ReloadableConfig(loader, BalancerSettings::class)
                .addWatcher(SettingsWatcher(workDir))
        }
    }
}

data class StaticBalancerConfig(
    val enabled: Boolean = true,
    val targets: List<TargetServer> =
        listOf(
            TargetServer("localhost", 25566),
            TargetServer("localhost", 25567),
        ),
)

data class GeoBalancerConfig(
    val token: String = "",
    val enabled: Boolean = true,
    val fallback: TargetServer = TargetServer("localhost", 25567),
    val filters: List<GeoFilter> =
        listOf(
            GeoFilter("country", "US", listOf(TargetServer("localhost", 25568))),
            GeoFilter("country", "DE", listOf(TargetServer("localhost", 25569))),
        ),
)

data class GeoFilter(
    val type: String,
    val value: String,
    val targets: List<TargetServer>,
)

data class HttpBalancerConfig(
    val enabled: Boolean = false,
    val host: String = "https://example.com/{username}/{uuid}",
    @ConfigAlias("host-path") val hostPath: String = "$.host",
    @ConfigAlias("port-path") val portPath: String = "$.port",
)
