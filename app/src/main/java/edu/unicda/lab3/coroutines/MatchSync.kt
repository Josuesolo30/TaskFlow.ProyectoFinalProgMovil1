package edu.unicda.lab3.coroutines

import edu.unicda.lab3.MatchSummary
import edu.unicda.lab3.Session
import edu.unicda.lab3.SimulatedIo
import edu.unicda.lab3.SummariesResult
import edu.unicda.lab3.TeamStats
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.supervisorScope

// Función 1: obtiene el resumen de un partido.
suspend fun loadMatchSummary(
    username: String,
    matchId: String
): MatchSummary {
    val session = login(username)
    return loadWithSession(session, matchId)
}

private suspend fun loadWithSession(
    session: Session,
    matchId: String
): MatchSummary = coroutineScope {
    val match = fetchMatch(session, matchId)

    // Se inician ambas consultas antes de esperar sus resultados.
    val statsA = async(CoroutineName("stats-A:$matchId")) {
        fetchTeamStats(session, match.teamAId)
    }

    val statsB = async(CoroutineName("stats-B:$matchId")) {
        fetchTeamStats(session, match.teamBId)
    }

    // Si una consulta falla, coroutineScope cancela la otra.
    MatchSummary(
        match,
        statsA.await(),
        statsB.await()
    )
}

// Función 2: carga varios partidos y admite resultados parciales.
suspend fun loadSummaries(
    username: String,
    matchIds: List<String>
): SummariesResult {
    val session = login(username)

    return supervisorScope {
        val pending = matchIds.map { matchId ->
            async(CoroutineName("match:$matchId")) {
                loadWithSession(session, matchId)
            }
        }

        val ok = mutableListOf<MatchSummary>()
        val failed = linkedMapOf<String, Throwable>()

        // Conserva el orden de los identificadores solicitados.
        pending.forEachIndexed { index, deferred ->
            try {
                ok.add(deferred.await())
            } catch (cancelled: CancellationException) {
                // Nunca convertir una cancelación en un resultado normal.
                throw cancelled
            } catch (error: Exception) {
                failed[matchIds[index]] = error
            }
        }

        SummariesResult(
            ok.toList(),
            failed.toMap()
        )
    }
}

// Función 3: consulta estadísticas repetidamente.
suspend fun pollTeamStats(
    username: String,
    teamId: String,
    polls: Int,
    intervalMs: Long,
    onUpdate: (TeamStats) -> Unit
) {
    val session = login(username)

    repeat(polls.coerceAtLeast(0)) { index ->
        currentCoroutineContext().ensureActive()

        val stats = fetchTeamStats(session, teamId)

        currentCoroutineContext().ensureActive()
        onUpdate(stats)

        if (index < polls - 1) {
            // Espera cancelable; conserva el escalado del simulador.
            delay(
                (intervalMs * SimulatedIo.latencyScale).toLong()
            )
        }
    }

    currentCoroutineContext().ensureActive()
}

