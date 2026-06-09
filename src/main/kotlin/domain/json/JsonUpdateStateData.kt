package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import io.vertx.core.json.Json

@RegisterForReflection
data class JsonUpdateStateData(
    @field:JsonProperty("MatchGuid") val matchGuid: String = "",
    @field:JsonProperty("Players") val players: List<JsonPlayer> = emptyList(),
    @field:JsonProperty("Game") val game: JsonGameState = JsonGameState(),
) {
    companion object {
        fun parse(jsonString: String): JsonUpdateStateData = Json.decodeValue(jsonString, JsonUpdateStateData::class.java)
    }
}
