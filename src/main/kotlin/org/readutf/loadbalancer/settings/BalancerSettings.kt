package org.readutf.loadbalancer.settings

import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addFileSource
import com.sksamuel.hoplite.watch.ReloadableConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.readutf.loadbalancer.finder.TargetServer
import java.io.InputStream
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.notExists

data class BalancerSettings(
    val host: String = "0.0.0.0",
    val port: Int = 25565,
    val motd: String = "<rainbow>Default loadbalancer config",
    val connectionError: String = "<red>Could not connect you to backend server.</red>",
    val static: StaticBalancer = StaticBalancer(),
    val http: HttpBalancer = HttpBalancer(),
) {
    fun createInetSocket(): InetSocketAddress = InetSocketAddress(host, port)

    companion object {
        val miniMessage = MiniMessage.miniMessage()

        fun load(workDir: Path): ReloadableConfig<BalancerSettings> {
            val inputStream: InputStream? = BalancerSettings::class.java.getResourceAsStream("/settings.yml")

            if (inputStream == null) error("Could not load settings from $workDir")

            var destination = workDir.resolve("settings.yml")
            if (destination.notExists()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING)
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

data class StaticBalancer(
    val enabled: Boolean = true,
    val targets: List<TargetServer> =
        listOf(
            TargetServer("localhost", 25566),
            TargetServer("localhost", 25567),
        ),
)

data class HttpBalancer(
    val enabled: Boolean = false,
    val host: String = "https://example.com/{username}/{uuid}",
    val hostPath: String = "$.host",
    val portPath: String = "$.port",
)
