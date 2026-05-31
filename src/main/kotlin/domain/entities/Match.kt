package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonGameState
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Entity
class Match : PanacheEntity() {
    @Column(name = MATCH_ID_COLUMN)
    lateinit var uuid: Uuid

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = HOME_TEAM_ID_COLUMN) // Name of the column in the parent entity, denotes this is the owner of the relation
    lateinit var homeTeam: Team

    @OneToOne(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    @JoinColumn(name = AWAY_TEAM_ID_COLUMN) // Name of the column in the parent entity, denotes this is the owner of the relation
    lateinit var awayTeam: Team

    companion object : PanacheCompanion<Match> {
        const val TABLE_NAME = "MATCHES"
        const val MATCH_ID_COLUMN = "MATCH_ID"
        const val HOME_TEAM_ID_COLUMN = "HOME_TEAM_ID"
        const val AWAY_TEAM_ID_COLUMN = "AWAY_TEAM_ID"

        fun findByMatchId(matchGuid: Uuid): Match? = find(MATCH_ID_COLUMN, matchGuid).firstResult()

        fun findOrMake(
            matchGuid: Uuid,
            jsonMatch: JsonGameState,
        ): Match =
            findByMatchId(matchGuid) ?: Match()
                .apply {
                    uuid = matchGuid
                    homeTeam = Team.make(jsonMatch.getTeamByNum(0))
                    awayTeam = Team.make(jsonMatch.getTeamByNum(1))
                }.apply { persist() }
    }
}
