package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Player(
    val uuid: Uuid = Uuid.random(),
    val onlineId: String,
    val name: String,
) {
    companion object {
        fun fromJson(jsonPlayer: JsonPlayer) =
            Player(
                onlineId = jsonPlayer.botSaveId(),
                name = jsonPlayer.name,
            )
    }
}
