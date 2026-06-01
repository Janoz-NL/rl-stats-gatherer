package com.janoz.rl.statgatherer.service

import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped
import kotlin.uuid.ExperimentalUuidApi

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class PlayerService(
    private val repository: PlayerRepository,
) {
    /**
     * Finds a player by its online id or creates a new one if it does not exist yet.
     */
    fun findOrCreate(jsonPLayer: JsonPlayer): Player {
        val candidate = repository.findByOnlineId(jsonPLayer.botSaveId())
        if (candidate != null) {
            if (candidate.name != jsonPLayer.name) {
                repository.update(candidate.copy(name = jsonPLayer.name))
            }
            return candidate
        }
        return Player.fromJson(jsonPLayer).apply { repository.insert(this) }
    }
}
