package com.janoz.rl.statgatherer.repository

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import assertk.assertions.message
import com.janoz.rl.statgatherer.domain.entities.Player
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class PlayerRepositoryTest {
    @Inject
    private lateinit var cut: PlayerRepository

    @Test
    fun `find non existing player`() {
        assertThat(cut.findByOnlineId("steam|1|1")).isNull()
    }

    @Test
    fun `insert player and finding it by onlineId`() {
        val player = Player(name = "TestGebruiker", onlineId = "steam|1|2")
        cut.insert(player)

        assertThat(cut.findByOnlineId("steam|1|2")).isEqualTo(player)
        assertThat(cut.findByOnlineId("steam|1|3")).isNull()
    }

    @Test
    fun `update player changes name`() {
        val player = Player(name = "TestGebruiker", onlineId = "steam|1|4")
        cut.insert(player)
        val newPlayer = player.copy(name = "NewName")
        cut.update(newPlayer)
        assertThat(cut.findByOnlineId("steam|1|4")).isEqualTo(newPlayer)
    }

    @Test
    fun `insert player with same onlineId fails`() {
        val player = Player(name = "TestGebruiker", onlineId = "steam|1|5")
        cut.insert(player)

        assertFailure { cut.insert(player) }.message().isNotNull().matchesPredicate {
            it.contains("unique constraint")
            it.contains("\"players_pkey\"")
        }

        val samePlayer = Player(name = "TestGebruiker", onlineId = "steam|1|5")
        assertFailure { cut.insert(samePlayer) }.message().isNotNull().matchesPredicate {
            it.contains("unique constraint")
            it.contains("\"players_online_id_key\"")
        }
    }
}
