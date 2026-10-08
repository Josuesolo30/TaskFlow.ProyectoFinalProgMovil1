package edu.unicda.lab3

data class Session(val userId: String, val token: String)

data class Match(val id: String, val teamAId: String, val teamBId: String, val status: String)

/** [snapshot] sube en cada consulta del mismo equipo: sirve para ver que el polling avanza. */
data class TeamStats(
    val teamId: String,
    val points: Int,
    val raids: Int,
    val tackles: Int,
    val snapshot: Int,
)

data class MatchSummary(val match: Match, val statsA: TeamStats, val statsB: TeamStats)

/** [ok] va en el mismo orden de los ids pedidos; [failed] mapea matchId -> error. */
data class SummariesResult(val ok: List<MatchSummary>, val failed: Map<String, Throwable>)