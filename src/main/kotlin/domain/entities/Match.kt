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
    val lastSeen: Instant,
    val links: MutableList<Link> = ArrayList(),
) {
    companion object {
        fun fromJson(
            jsonMatch: JsonGameState,
            matchGuid: Uuid,
            firstSeen: Instant,
            lastSeen: Instant,
        ): Match =
            Match(
                uuid = matchGuid,
                homeTeam = Team.fromJson(jsonMatch.getTeamByNum(0)),
                awayTeam = Team.fromJson(jsonMatch.getTeamByNum(1)),
                isFinished = jsonMatch.isDone,
                firstSeen = firstSeen,
                lastSeen = lastSeen,
            )
    }
}
