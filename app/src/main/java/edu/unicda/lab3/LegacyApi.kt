package edu.unicda.lab3

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Backend simulado del profesor.
 * Conserva sus datos, errores y operaciones con callbacks.
 */
object LegacyApi {
    private val snapshots = ConcurrentHashMap<String, AtomicInteger>()

    fun reset() = snapshots.clear()

    fun login(username: String, cb: Callback<Session>): Cancellable =
        SimulatedIo.async("login", 300, {
            if (username.isBlank()) throw AuthException("usuario vacio")

            Session(
                username,
                "tok-" + Integer.toHexString(username.hashCode())
            )
        }, cb)

    fun fetchMatch(
        session: Session,
        matchId: String,
        cb: Callback<Match>
    ): Cancellable =
        SimulatedIo.async("fetchMatch", 500, {
            when (matchId) {
                "M-001" -> Match(
                    "M-001",
                    "T-TIGRES",
                    "T-HALCONES",
                    "LIVE"
                )

                "M-002" -> Match(
                    "M-002",
                    "T-TIGRES",
                    "T-FAIL",
                    "LIVE"
                )

                "M-003" -> Match(
                    "M-003",
                    "T-HALCONES",
                    "T-TIGRES",
                    "FINISHED"
                )

                else -> throw NotFoundException(
                    "partido $matchId no existe"
                )
            }
        }, cb)

    fun fetchTeamStats(
        session: Session,
        teamId: String,
        cb: Callback<TeamStats>
    ): Cancellable {
        val ms = if (teamId == "T-FAIL") 250L else 700L

        return SimulatedIo.async("stats:$teamId", ms, {
            val (points, raids, tackles) = when (teamId) {
                "T-TIGRES" -> Triple(28, 12, 9)
                "T-HALCONES" -> Triple(24, 10, 11)

                "T-FAIL" -> throw NetworkException(
                    "sin conexion con el servidor de estadisticas"
                )

                else -> throw NotFoundException(
                    "equipo $teamId no existe"
                )
            }

            val snap = snapshots
                .computeIfAbsent(teamId) { AtomicInteger(0) }
                .incrementAndGet()

            TeamStats(
                teamId,
                points + (snap - 1) * 2,
                raids + (snap - 1),
                tackles,
                snap
            )
        }, cb)
    }
}

