package com.janoz.rl.statgatherer.service

import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.PlayerRepository
import jakarta.enterprise.context.ApplicationScoped
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class PlayerService(
    private val repository: PlayerRepository,
) {
    /**
     * Finds a PlayerResource by its online id or creates a new one if it does not exist yet.
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

    fun find(uuid: Uuid) = repository.findDetails(uuid)

    fun findAll() = repository.findAll()
}
