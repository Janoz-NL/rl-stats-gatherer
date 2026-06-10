package com.janoz.rl.statgatherer.service

import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import com.janoz.rl.statgatherer.repository.MatchRepository
import jakarta.enterprise.context.ApplicationScoped
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class MatchService(
    private val repository: MatchRepository,
    private val playerService: PlayerService,
    private val teamPlayerService: TeamPlayerService,
) {
    fun create(
        jsonUpdateStateData: JsonUpdateStateData,
        firstSeen: Instant,
        lastSeen: Instant,
    ) {
        if (!jsonUpdateStateData.game.isValid()) throw IllegalArgumentException("Invalid game state")
        val uuid: Uuid = Uuid.parse(jsonUpdateStateData.matchGuid)

        if (repository.findById(uuid) != null) return // already stored
        val match: Match = Match.fromJson(jsonUpdateStateData.game, uuid, firstSeen, lastSeen)
        repository.insert(match) // TODO: make this more atomic

        val teams = mapOf(0 to match.homeTeam, 1 to match.awayTeam)
        jsonUpdateStateData.game.teams.forEach { jsonTeam ->
            val team = teams[jsonTeam.teamNum] ?: throw IllegalArgumentException("Unreachble when valid state")

            jsonUpdateStateData.players
                .filter { it.teamNum == jsonTeam.teamNum }
                .forEach {
                    teamPlayerService.create(it, team, playerService.findOrCreate(it))
                }
        }
    }

    fun find(uuid: Uuid) =
        repository.findById(uuid).also {
            if (it != null) {
                teamPlayerService.fillTeamWithPLayers(it.homeTeam)
                teamPlayerService.fillTeamWithPLayers(it.awayTeam)
            }
        }

    fun findAll() = repository.findAll()
}
