package tv.own.owntv.core.sync.work

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

/**
 * Caps how many catalog and guide syncs run at once, across both workers.
 *
 * WorkManager starts every job whose constraints are met, so a restore that brings in hundreds of
 * playlists enqueues them all and they all run together: on a 2 GB television that saturates the
 * CPU and the SQLite connection pool, the UI stops answering (ANR) and the process is killed. The
 * jobs that wait here hold no network or database resource; they enter [MAX_CONCURRENT] at a time
 * in the order WorkManager started them.
 */
object SyncGate {
    const val MAX_CONCURRENT = 10

    private val permits = Semaphore(MAX_CONCURRENT)

    suspend fun <T> run(block: suspend () -> T): T = permits.withPermit { block() }
}
