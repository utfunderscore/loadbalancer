package org.readutf.loadbalancer.finder.impl

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.google.gson.Gson
import io.github.oshai.kotlinlogging.KotlinLogging
import org.readutf.loadbalancer.client.Player
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.TargetServer
import org.readutf.loadbalancer.settings.BalancerSettings
import org.readutf.loadbalancer.utils.AddressUtils
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class GeoServerFinder(
    val balancerSettings: BalancerSettings,
) : ServerFinder {
    private val client: HttpClient = HttpClient.newHttpClient()
    private val gson = Gson()

    private val logger = KotlinLogging.logger { }

    override fun findServer(player: Player): Result<TargetServer, Throwable> {
        val geoSettings = balancerSettings.geo

        var address = player.getAddress()
        logger.info { "Finding region for ${AddressUtils.redact(address, balancerSettings)}" }

        val uri = URI.create("https://ipinfo.io/$address?token=${geoSettings.token}")

        val request =
            HttpRequest
                .newBuilder()
                .uri(uri)
                .build()

        val jsonString =
            client
                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply { response -> response.body().toString() }
                .join()

        val json = gson.fromJson(jsonString, GeoLocation::class.java)

        for (filter in geoSettings.filters) {
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

        return Ok(geoSettings.fallback)
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
