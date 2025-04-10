package org.readutf.loadbalancer.finder.impl

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import org.readutf.loadbalancer.client.Player
import org.readutf.loadbalancer.finder.ServerFinder
import org.readutf.loadbalancer.finder.TargetServer

/**
 * A circular server finder
 */
class StaticServerFinder(
    val servers: List<TargetServer>,
) : ServerFinder {
    init {
        if (servers.isEmpty()) error("Server list is empty")
    }

    private var index = 0

    @Synchronized
    override fun findServer(player: Player): Result<TargetServer, Throwable> {
        val server = servers[index++]

        if (index > servers.size) index = 0

        return Ok(server)
    }
}
