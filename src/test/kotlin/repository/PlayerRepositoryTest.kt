package com.janoz.rl.statgatherer.repository

import assertk.all
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.extracting
import assertk.assertions.hasSize
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import assertk.assertions.message
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.Fixtures
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.enums.Order
import com.janoz.rl.statgatherer.domain.entities.enums.SortColumnPlayer
import com.janoz.rl.statgatherer.domain.entities.views.PlayerDetail
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class PlayerRepositoryTest {
    @Inject
    private lateinit var testSupport: TestSupport

    @Inject
    private lateinit var cut: PlayerRepository

    @BeforeEach
    fun setup() {
        testSupport.clear()
    }

    @Test
    fun `find non existing player should return null`() {
        assertThat(cut.findByOnlineId("steam|1|1")).isNull()
    }

    @Test
    fun `get non existing player should`() {
        assertFailure { cut.get(Uuid.random()) }.message().isEqualTo("Collection is empty.")
    }

    @Test
    fun `insert player and finding it by onlineId`() {
        val player = Player(name = "TestGebruiker", onlineId = "steam|1|2")
        cut.insert(player)

        assertThat(cut.findByOnlineId("steam|1|2")).isEqualTo(player)
        assertThat(cut.findByOnlineId("steam|1|3")).isNull()
    }

    @Test
    fun `insert player and getting it by uuid`() {
        val expected = Player(name = "TestGebruiker", onlineId = "steam|1|2")
        cut.insert(expected)

        val actual = cut.get(expected.uuid)

        assertThat(actual).isEqualTo(expected)
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

    @Test
    fun `find player details`() {
        testSupport.insertMatch()
        val player = cut.findByOnlineId(Fixtures.aPlayerId)!!

        val actual = cut.findDetails(player.uuid)

        assertThat(actual).isNotNull().all {
            prop(PlayerDetail::player).isEqualTo(player)
            prop(PlayerDetail::matches).isEqualTo(1)
            prop(PlayerDetail::demos).isEqualTo(4)
        }
    }

    @Test
    fun `find all player details`() {
        testSupport.insertMatch()

        val actual = cut.findAll()

        assertThat(actual).hasSize(6)
    }

    @Test
    fun `find all player details, ordered by name`() {
        testSupport.insertMatch()

        val actualAsc = cut.findAll(SortColumnPlayer.NAME, Order.ASC)
        val actualDesc = cut.findAll(SortColumnPlayer.NAME, Order.DESC)

        assertThat(actualAsc).extracting(PlayerDetail::player).extracting(Player::name).containsExactly(
            "Isa",
            "Jasper",
            "Stijn",
            "Twan",
            "Xavier",
            "Yasmine",
        )
        assertThat(actualDesc).extracting(PlayerDetail::player).extracting(Player::name).containsExactly(
            "Yasmine",
            "Xavier",
            "Twan",
            "Stijn",
            "Jasper",
            "Isa",
        )
    }
}
