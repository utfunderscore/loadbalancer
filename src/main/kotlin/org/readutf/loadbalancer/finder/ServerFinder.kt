package org.readutf.loadbalancer.finder

import com.github.michaelbull.result.Result
import java.util.UUID

interface ServerFinder {
    fun findServer(playerId: UUID): Result<TargetServer, Throwable>
}
