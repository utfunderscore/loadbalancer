package org.readutf.loadbalancer.finder

import com.github.michaelbull.result.Result
import org.readutf.loadbalancer.client.Player

interface ServerFinder {
    fun findServer(player: Player): Result<TargetServer, Throwable>
}
