package org.readutf.loadbalancer.finder.impl

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import com.github.michaelbull.result.runCatching
import com.jayway.jsonpath.JsonPath
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.TargetServer
import java.net.URI
import java.net.URL
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.UUID

class HttpServerFinder(
    val endpoint: String,
    val hostPath: String,
    val portPath: String,
) : ServerFinder {
    override fun findServer(playerId: UUID): Result<TargetServer, Throwable> {
        return runCatching {
            val json = makeRequest().getOrThrow()
            val jsonHost = JsonPath.parse(json)?.read<String>(hostPath)
            val jsonPort = JsonPath.parse(json).read<String>(portPath).toIntOrNull()

            if (jsonHost == null || jsonPort == null) {
                return error("Invalid server")
            }

            TargetServer(jsonHost, jsonPort)
        }
    }

    fun makeRequest(): Result<String, Throwable> =
        runCatching {
            val url: URL = URI.create(endpoint).toURL()
            val client: HttpClient = HttpClient.newHttpClient()

            // Create an HttpRequest
            val request =
                HttpRequest
                    .newBuilder()
                    .uri(URI.create("https://reqbin.com/echo/get/json"))
                    .build()

            // Send the request and handle the response
            client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply({ obj: HttpResponse<*>? -> obj!!.body().toString() })
                .join()
        }
}
