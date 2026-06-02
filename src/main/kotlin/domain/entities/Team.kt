package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonTeam
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toColor
import java.awt.Color
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Team(
    val uuid: Uuid = Uuid.random(),
    val name: String,
    val score: Int,
    val primaryColor: Color,
    val secondaryColor: Color,
) {
    val playerMatches: MutableSet<TeamPlayer> = HashSet()

    companion object {
        fun fromJson(jsonTeam: JsonTeam): Team =
            Team(
                name = jsonTeam.name,
                score = jsonTeam.score,
                primaryColor = jsonTeam.colorPrimary.toColor(),
                secondaryColor = jsonTeam.colorSecondary.toColor(),
            )
    }
}
