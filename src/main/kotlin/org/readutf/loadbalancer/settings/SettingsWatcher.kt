package org.readutf.loadbalancer.settings

import com.sksamuel.hoplite.watch.Watchable
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchEvent
import java.nio.file.WatchService

class SettingsWatcher(
    path: Path,
) : Watchable {
    lateinit var callback: () -> Unit

    override fun watch(
        callback: () -> Unit,
        errorHandler: (Throwable) -> Unit,
    ) {
        this.callback = callback
    }

    init {
        val fs: FileSystem = FileSystems.getDefault()
        val ws: WatchService = fs.newWatchService()
        path.register(
            ws,
            arrayOf<WatchEvent.Kind<*>>(
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
            ),
        )
        Thread {
            while (true) {
                val k = ws.take()
                for (e in k.pollEvents()) {
                    val c: Any? = e.context()
                    callback()
                }
                k.reset()
            }
        }.also { it.isDaemon = false }.start()
    }
}
