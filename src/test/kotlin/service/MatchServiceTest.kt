package com.janoz.rl.statgatherer.service

import com.janoz.rl.statgatherer.domain.Fixtures
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createMatch
import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.MatchRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import io.vertx.core.impl.logging.LoggerFactory
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class MatchServiceTest {
    private val log = LoggerFactory.getLogger(MatchServiceTest::class.java)

    @Inject
    private lateinit var cut: MatchService

    @InjectMock
    private lateinit var matchRepository: MatchRepository

    @InjectMock
    private lateinit var playerService: PlayerService

    @InjectMock
    private lateinit var teamPlayerService: TeamPlayerService

    @Test
    fun `store valid match`() {
        val now = Instant.parse("2016-02-15T12:34:56Z")
        val updateStateData = Fixtures.updateStateData
        val uuid = Uuid.parse(updateStateData.matchGuid)

        whenever(matchRepository.findById(eq(uuid))).thenReturn(null)
        var match: Match?
        whenever(matchRepository.insert(argThat { this.uuid == uuid })).thenAnswer {
            (it.arguments[0] as Match).also { m -> match = m }
        }
        val players: MutableSet<Player> = HashSet()
        whenever(playerService.findOrCreate(any())).thenAnswer {
            val jp = it.arguments[0] as JsonPlayer
            log.info("Making player ${jp.name}")
            Fixtures.players[jp.name]!!.also { p -> players.add(p) }
        }
        val teamPlayers: MutableSet<TeamPlayer> = HashSet()
        whenever(teamPlayerService.create(any(), any(), any())).thenAnswer {
            val team = it.arguments[1] as Team
            val player = it.arguments[2] as Player
            TeamPlayer(team = team, player = player).also { tp -> teamPlayers.add(tp) }
        }

        cut.create(updateStateData, now)

        verify(matchRepository).findById(eq(uuid))
        verify(matchRepository).insert(any())
        verify(playerService, times(6)).findOrCreate(any())
        verify(teamPlayerService, times(6)).create(any(), any(), any())
        verifyNoMoreInteractions(matchRepository, playerService, teamPlayerService)
    }

    @Test
    fun `dont store valid match already stored`() {
        val now = Instant.parse("2016-02-15T12:34:56Z")
        val updateStateData = Fixtures.updateStateData
        val uuid = Uuid.parse(updateStateData.matchGuid)

        whenever(matchRepository.findById(eq(uuid))).thenReturn(createMatch(uuid))

        cut.create(updateStateData, now)

        verify(matchRepository).findById(eq(uuid))
        verifyNoMoreInteractions(matchRepository, playerService, teamPlayerService)
    }
}
