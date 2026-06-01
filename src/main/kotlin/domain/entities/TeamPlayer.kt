package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import kotlin.math.max

data class TeamPlayer(
    val team: Team,
    val player: Player,
    val score: Int = 0,
    val goals: Int = 0,
    val shots: Int = 0,
    val assists: Int = 0,
    val saves: Int = 0,
    val demos: Int = 0,
) {
    override fun toString(): String = "TeamPlayer(team=${team.name}, player=${player.name}, score=$score)"

    fun combine(other: TeamPlayer): TeamPlayer {
        if (other.team != team) throw IllegalStateException("Cannot combine players from different teams")
        if (other.player != player) throw IllegalStateException("Cannot combine different players")

        return TeamPlayer(
            team = this.team,
            player = this.player,
            score = max(this.score, other.score),
            goals = max(this.goals, other.goals),
            shots = max(this.shots, other.shots),
            assists = max(this.assists, other.assists),
            saves = max(this.saves, other.saves),
            demos = max(this.demos, other.demos),
        )
    }

    companion object {
        fun fromJson(
            jsonPlayer: JsonPlayer,
            team: Team,
            player: Player,
        ) = TeamPlayer(
            team = team,
            player = player,
            score = jsonPlayer.score,
            goals = jsonPlayer.goals,
            shots = jsonPlayer.shots,
            assists = jsonPlayer.assists,
            saves = jsonPlayer.saves,
            demos = jsonPlayer.demos,
        )
    }
}
