package com.janoz.rl.statgatherer.service

import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.isSameInstanceAs
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.views.PlayerDetail
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.PlayerRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class PlayerServiceTest {
    @Inject
    private lateinit var cut: PlayerService

    @InjectMock
    private lateinit var playerRepository: PlayerRepository

    @Test
    fun `insert new player`() {
        whenever(playerRepository.findByOnlineId("steam|1|2")).thenReturn(null)
        var actualPlayer: Player? = null
        whenever(playerRepository.insert(any())).thenAnswer {
            (it.arguments[0] as Player).also { p -> actualPlayer = p }
        }

        cut.findOrCreate(JsonPlayer("TestGebruiker", "steam|1|2"))

        verify(playerRepository).findByOnlineId("steam|1|2")
        verify(playerRepository).insert(any())
        verifyNoMoreInteractions(playerRepository)

        assertThat(actualPlayer).isNotNull().all {
            prop(Player::name).isEqualTo("TestGebruiker")
            prop(Player::onlineId).isEqualTo("steam|1|2")
        }
    }

    @Test
    fun `update existing player`() {
        whenever(playerRepository.findByOnlineId("steam|1|2")).thenReturn(Player(name = "Existing", onlineId = "steam|1|2"))
        var actualPlayer: Player? = null
        whenever(playerRepository.update(any())).thenAnswer {
            (it.arguments[0] as Player).also { p -> actualPlayer = p }
        }

        cut.findOrCreate(JsonPlayer("TestGebruiker", "steam|1|2"))

        verify(playerRepository).findByOnlineId("steam|1|2")
        verify(playerRepository).update(any())
        verifyNoMoreInteractions(playerRepository)

        assertThat(actualPlayer).isNotNull().all {
            prop(Player::name).isEqualTo("TestGebruiker")
            prop(Player::onlineId).isEqualTo("steam|1|2")
        }
    }

    @Test
    fun `no update for existing player when no change`() {
        whenever(playerRepository.findByOnlineId("steam|1|2")).thenReturn(Player(name = "TestGebruiker", onlineId = "steam|1|2"))

        cut.findOrCreate(JsonPlayer("TestGebruiker", "steam|1|2"))

        verify(playerRepository).findByOnlineId("steam|1|2")
        verifyNoMoreInteractions(playerRepository)
    }

    @Test
    fun `find unknown playerdetails should return null`() {
        val uuid = Uuid.random()
        whenever(playerRepository.findDetails(uuid)).thenReturn(null)

        val actual = cut.find(uuid)

        assertThat(actual).isNull()
        verify(playerRepository).findDetails(uuid)
        verifyNoMoreInteractions(playerRepository)
    }

    @Test
    fun `find player should return player`() {
        val uuid = Uuid.random()
        val expected = PlayerDetail(player = Player(uuid = uuid, name = "TestGebruiker", onlineId = "steam|1|2"))
        whenever(playerRepository.findDetails(uuid)).thenReturn(expected)

        val actual = cut.find(uuid)

        assertThat(actual).isSameInstanceAs(expected)
        verify(playerRepository).findDetails(uuid)
        verifyNoMoreInteractions(playerRepository)
    }

    @Test
    fun `find all players`() {
        val expected = listOf(PlayerDetail(player = Player(name = "Existing", onlineId = "steam|1|2")))
        whenever(playerRepository.findAll()).thenReturn(expected)

        val actual = cut.findAll()

        assertThat(actual).isSameInstanceAs(expected)
        verify(playerRepository).findAll()
        verifyNoMoreInteractions(playerRepository)
    }
}
