package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonGameState(
    @field:JsonProperty("Teams")
    val teams: List<JsonTeam> = emptyList(),
    @field:JsonProperty("Arena")
    val arena: String = "",
) {
    fun getTeamByNum(teamNum: Int): JsonTeam? = teams.firstOrNull { it.teamNum == teamNum }
}
