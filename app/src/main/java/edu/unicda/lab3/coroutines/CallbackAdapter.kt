package edu.unicda.lab3.coroutines

import edu.unicda.lab3.Callback
import edu.unicda.lab3.Cancellable
import edu.unicda.lab3.LegacyApi
import edu.unicda.lab3.Match
import edu.unicda.lab3.Session
import edu.unicda.lab3.TeamStats
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// Convierte una operación con callback en una espera suspend cancelable.
suspend fun <T> awaitCallback(
    start: (Callback<T>) -> Cancellable
): T = suspendCancellableCoroutine { continuation ->

    val handle = start(object : Callback<T> {
        override fun onSuccess(value: T) {
            continuation.resume(value)
        }

        override fun onError(error: Throwable) {
            continuation.resumeWithException(error)
        }
    })

    // Cancelar la corrutina también cancela el I/O original.
    continuation.invokeOnCancellation {
        handle.cancel()
    }
}

suspend fun login(username: String): Session =
    awaitCallback { callback ->
        LegacyApi.login(username, callback)
    }

suspend fun fetchMatch(
    session: Session,
    matchId: String
): Match =
    awaitCallback { callback ->
        LegacyApi.fetchMatch(session, matchId, callback)
    }

suspend fun fetchTeamStats(
    session: Session,
    teamId: String
): TeamStats =
    awaitCallback { callback ->
        LegacyApi.fetchTeamStats(session, teamId, callback)
    }

