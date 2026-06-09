package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection
import io.vertx.core.json.Json

@RegisterForReflection
class JsonRocketLeagueMessage(
    @field:JsonProperty("Event")
    val event: String = "",
    @field:JsonProperty("Data")
    val data: String = "",
) {
    companion object {
        fun parse(jsonString: String): JsonRocketLeagueMessage = Json.decodeValue(jsonString, JsonRocketLeagueMessage::class.java)
    }
}
