package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
class JsonRocketLeagueMessage(
    @field:JsonProperty("Event")
    val event: String = "",
    @field:JsonProperty("Data")
    val data: String = "",
)
