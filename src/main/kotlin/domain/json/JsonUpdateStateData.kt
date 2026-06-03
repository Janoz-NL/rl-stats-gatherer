package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class JsonUpdateStateData(
    @field:JsonProperty("MatchGuid") val matchGuid: String = "",
    @field:JsonProperty("Players") val players: List<JsonPlayer> = emptyList(),
    @field:JsonProperty("Game") val game: JsonGameState = JsonGameState(),
)
