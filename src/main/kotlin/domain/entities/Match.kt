package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonGameState
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Match(
    val uuid: Uuid,
    val homeTeam: Team,
    val awayTeam: Team,
    val isFinished: Boolean,
    val firstSeen: Instant,
) {
    override fun toString(): String = "Match(uuid=$uuid, homeTeam=${homeTeam.name}, awayTeam=${awayTeam.name}, isFinished=$isFinished)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Match

        if (isFinished != other.isFinished) return false
        if (uuid != other.uuid) return false
        if (homeTeam != other.homeTeam) return false
        if (awayTeam != other.awayTeam) return false
        if (firstSeen != other.firstSeen) return false

        return true
    }

    override fun hashCode(): Int = uuid.hashCode()

    companion object {
        fun fromJson(
            jsonMatch: JsonGameState,
            matchGuid: Uuid,
            firstSeen: Instant,
        ): Match =
            Match(
                uuid = matchGuid,
                homeTeam = Team.fromJson(jsonMatch.getTeamByNum(0)),
                awayTeam = Team.fromJson(jsonMatch.getTeamByNum(1)),
                isFinished = jsonMatch.isDone,
                firstSeen = firstSeen,
            )
    }
}
