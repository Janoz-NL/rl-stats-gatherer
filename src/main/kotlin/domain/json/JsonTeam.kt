package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonProperty
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class JsonTeam(
    @field:JsonProperty("Name") val name: String = "",
    @field:JsonProperty("TeamNum") val teamNum: Int = 0,
    @field:JsonProperty("Score") val score: Int = 0,
    @field:JsonProperty("ColorPrimary") val colorPrimary: String = "",
    @field:JsonProperty("ColorSecondary") val colorSecondary: String = "",
)
