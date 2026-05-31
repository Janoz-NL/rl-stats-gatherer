package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class TeamPlayer : PanacheEntity() {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = TEAM_ID_COLUMN)
    lateinit var team: Team

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = PLAYER_ID_COLUMN)
    lateinit var player: Player

    @Column(name = SCORE_COLUMN)
    var score: Int = 0

    @Column(name = GOALS_COLUMN)
    var goals: Int = 0

    @Column(name = SHOTS_COLUMN)
    var shots: Int = 0

    @Column(name = ASSISTS_COLUMN)
    var assists: Int = 0

    @Column(name = SAVES_COLUMN)
    var saves: Int = 0

    @Column(name = DEMOS_COLUMN)
    var demos: Int = 0

    companion object : PanacheCompanion<TeamPlayer> {
        const val TABLE_NAME = "TEAM_PLAYER"
        const val TEAM_ID_COLUMN = "TEAM_ID"
        const val PLAYER_ID_COLUMN = "PLAYER_ID"
        const val SCORE_COLUMN = "SCORE"
        const val GOALS_COLUMN = "GOALS"
        const val SHOTS_COLUMN = "SHOTS"
        const val ASSISTS_COLUMN = "ASSISTS"
        const val SAVES_COLUMN = "SAVES"
        const val DEMOS_COLUMN = "DEMOS"

        fun findByTeamAndPlayer(
            team: Team,
            player: Player,
        ): TeamPlayer? =
            find(
                "team = :team and player = :player",
                mapOf(
                    "team" to team,
                    "player" to player,
                ),
            ).firstResult()

        fun createOrUpdate(
            team: Team,
            player: Player,
            jsonPlayer: JsonPlayer,
        ): TeamPlayer {
            val teamPlayer =
                findByTeamAndPlayer(team, player) ?: TeamPlayer()
                    .apply {
                        this.team = team
                        this.player = player
                    }
            return teamPlayer
                .apply {
                    score = jsonPlayer.score
                    goals = jsonPlayer.goals
                    shots = jsonPlayer.shots
                    assists = jsonPlayer.assists
                    saves = jsonPlayer.saves
                    demos = jsonPlayer.demos
                }.apply { persist() }
        }
    }
}
