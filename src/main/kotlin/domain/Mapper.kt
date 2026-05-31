package com.janoz.rl.statgatherer.domain

import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class Mapper {
    fun map(updateState: JsonUpdateStateData): Match {
        val match = Match.findOrMake(Uuid.parse(updateState.matchGuid), updateState.game)
        val teams = mapOf(0 to match.homeTeam, 1 to match.awayTeam)

        updateState.game.teams.forEach { jsonTeam ->
            teams[jsonTeam.teamNum]!!.apply(jsonTeam)
            updateState.players.filter { it.teamNum in 0..1 }.forEach { jsonPlayer ->
                val player = Player.findOrMake(jsonPlayer)
                TeamPlayer.createOrUpdate(teams[jsonPlayer.teamNum]!!, player, jsonPlayer)
            }
        }
        return match
    }
}
