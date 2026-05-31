package com.janoz.rl.statgatherer.domain.json

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class JsonPlayer(
    @field:JsonProperty("Name")
    val name: String = "",
    @field:JsonProperty("PrimaryId")
    val primaryId: String = "",
    @field:JsonProperty("Shortcut")
    val shortcut: Int = 0,
    @field:JsonProperty("TeamNum")
    val teamNum: Int = 0,
    @field:JsonProperty("Score")
    val score: Int = 0,
    @field:JsonProperty("Goals")
    val goals: Int = 0,
    @field:JsonProperty("Shots")
    val shots: Int = 0,
    @field:JsonProperty("Assists")
    val assists: Int = 0,
    @field:JsonProperty("Saves")
    val saves: Int = 0,
    @field:JsonProperty("Demos")
    val demos: Int = 0,
) {
    fun botSaveId(): String = if (primaryId.startsWith("UNKNOWN")) "Bot|$name|0" else primaryId
}
