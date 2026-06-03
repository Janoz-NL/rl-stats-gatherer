package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonGameState(
    @field:JsonProperty("Teams")
    val teams: List<JsonTeam> = emptyList(),
    @field:JsonProperty("Arena")
    val arena: String = "",
    @field:JsonProperty("bHasWinner")
    val isDone: Boolean = false,
) {
    fun getTeamByNum(teamNum: Int): JsonTeam = teams.first { it.teamNum == teamNum }

    fun getTeamByNumMaybe(teamNum: Int): JsonTeam? = teams.firstOrNull { it.teamNum == teamNum }

    fun isValid(): Boolean = teams.size == 2 && getTeamByNumMaybe(0) != null && getTeamByNumMaybe(1) != null
}
