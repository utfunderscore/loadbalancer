package org.readutf.loadbalancer.finder.impl

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.loadbalancer.client.Player
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.TargetServer
import org.readutf.loadbalancer.settings.GeoBalancerConfig
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GeoServerFinder(
    val geoBalancerConfig: GeoBalancerConfig,
) : ServerFinder {
    private val client: HttpClient = HttpClient.newHttpClient()
    private val gson = Gson()

    private val logger = KotlinLogging.logger { }

    override fun findServer(player: Player): Result<TargetServer, Throwable> {
        var address = player.getAddress()

        if (address == "127.0.0.1") {
            address = "82.30.166.230"
        }

        logger.info { "Finding region for $address" }

        val uri = URI.create("https://ipinfo.io/$address?token=${geoBalancerConfig.token}")

        logger.info { "Finding server for ${player.getAddress()}" }

        val request =
            HttpRequest
                .newBuilder()
                .uri(uri)
                .build()

        val jsonString =
            client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply { obj: HttpResponse<*>? -> obj!!.body().toString() }
                .join()

        val json = gson.fromJson(jsonString, GeoLocation::class.java)

        for (filter in geoBalancerConfig.filters) {
            when (filter.type) {
                "region" -> {
                    if (json.region == filter.value) {
                        return Ok(filter.targets.random())
                    }
                }
                "country" -> {
                    if (json.country == filter.value) {
                        return Ok(filter.targets.random())
                    }
                }
                "city" -> {
                    if (json.city == filter.value) {
                        return Ok(filter.targets.random())
                    }
                }
            }
        }

        return Ok(geoBalancerConfig.fallback)
    }

    data class GeoLocation(
        val ip: String,
        val hostname: String,
        val city: String,
        val region: String,
        val country: String,
        val loc: String,
        val org: String,
        val postal: String,
        val timezone: String,
        val anycast: Boolean,
    )
}
