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
    override fun toString(): String = "Player(name=$name, onlineId=$onlineId)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (uuid != other.uuid) return false
        if (onlineId != other.onlineId) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int = uuid.hashCode()

    companion object {
        fun fromJson(jsonPlayer: JsonPlayer) =
            Player(
                onlineId = jsonPlayer.botSaveId(),
                name = jsonPlayer.name,
            )
    }
}
