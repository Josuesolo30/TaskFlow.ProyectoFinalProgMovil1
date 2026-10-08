package edu.unicda.lab3.coroutines

import edu.unicda.lab3.SimulatedIo
import edu.unicda.lab3.TeamStats
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MatchSyncService(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parentJob: Job? = null,
    private val logger: (String, String) -> Unit = SimulatedIo::log
) {
    private val supervisor = SupervisorJob(parentJob)

    private val handler = CoroutineExceptionHandler { context, error ->
        val name = context[CoroutineName]?.name ?: "polling"

        logger(
            "handler",
            "$name: ${error::class.simpleName}: ${error.message}"
        )
    }

    private val scope = CoroutineScope(
        supervisor + dispatcher + handler
    )

    fun startPolling(
        username: String,
        teamId: String,
        polls: Int,
        intervalMs: Long,
        onUpdate: (TeamStats) -> Unit
    ): Job = scope.launch(CoroutineName("poll:$teamId")) {
        try {
            pollTeamStats(
                username,
                teamId,
                polls,
                intervalMs,
                onUpdate
            )

            logger("polling", "FINISHED $teamId")
        } catch (cancelled: CancellationException) {
            logger("polling", "CANCELLED $teamId")
            throw cancelled
        }

        // Otros errores se registran mediante el handler.
    }

    fun close() {
        scope.cancel()
    }
}

