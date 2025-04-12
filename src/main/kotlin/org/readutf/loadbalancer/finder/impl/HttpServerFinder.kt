package org.readutf.loadbalancer.finder.impl

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.runCatching
import com.jayway.jsonpath.JsonPath
import org.readutf.loadbalancer.client.Player
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.TargetServer
import java.net.InetSocketAddress
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class HttpServerFinder(
    val endpoint: String,
    val hostPath: String,
    val portPath: String,
) : ServerFinder {
    override fun findServer(player: Player): Result<TargetServer, Throwable> =
        runCatching {
            val socket = player.getAddress() as InetSocketAddress

            var url =
                endpoint
                    .replace("{name}", player.getUsername())
                    .replace("{username}", player.getUsername())
                    .replace("{uuid}", player.getPlayerId().toString())
                    .replace("{ip}", socket.address.hostAddress)
                    .replace("{list_address}", player.getServerListAddress())

            val json = makeRequest(url).getOrThrow()
            val jsonHost = JsonPath.parse(json)?.read<String>(hostPath)
            val jsonPort = JsonPath.parse(json).read<String>(portPath).toIntOrNull()

            if (jsonHost == null || jsonPort == null) {
                error("Invalid server")
            }

            TargetServer(jsonHost, jsonPort)
        }

    fun makeRequest(url: String): Result<String, Throwable> =
        runCatching {
            val client: HttpClient = HttpClient.newHttpClient()

            // Create an HttpRequest
            val request =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create(url))
                    .build()

            // Send the request and handle the response
            client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply { obj: HttpResponse<*>? -> obj!!.body().toString() }
                .join()
        }
}
