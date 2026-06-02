package com.janoz.rl.statgatherer.service

import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.TeamPlayerRepository
import jakarta.enterprise.context.ApplicationScoped
import kotlin.uuid.ExperimentalUuidApi

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class TeamPlayerService(
    private val repository: TeamPlayerRepository,
) {
    fun create(
        jsonPlayer: JsonPlayer,
        team: Team,
        player: Player,
    ): TeamPlayer =
        repository
            .insert(
                TeamPlayer.fromJson(
                    jsonPlayer = jsonPlayer,
                    team = team,
                    player = player,
                ),
            ).also { team.playerMatches.add(it) }

    fun fillTeamWithPLayers(team: Team) {
        repository.listByTeam(team)
    }
}
