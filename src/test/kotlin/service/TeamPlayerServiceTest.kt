package com.janoz.rl.statgatherer.service

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isSameInstanceAs
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createTeam
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import com.janoz.rl.statgatherer.repository.TeamPlayerRepository
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class TeamPlayerServiceTest {
    @Inject
    private lateinit var cut: TeamPlayerService

    @InjectMock
    private lateinit var teamPlayerRepository: TeamPlayerRepository

    @Test
    fun `insert new teamplayer`() {
        val jsonPlayer =
            JsonPlayer(
                name = "TestGebruiker",
                primaryId = "steam|1|2",
                score = 10,
                goals = 9,
                shots = 8,
                assists = 7,
                saves = 6,
                demos = 5,
            )
        val player = Player(name = "TestGebruiker", onlineId = "steam|1|2")
        val team = createTeam(name = "TestTeam")

        whenever(teamPlayerRepository.insert(any())).thenAnswer {
            (it.arguments[0] as TeamPlayer)
        }

        val actual = cut.create(jsonPlayer, team, player)

        verify(teamPlayerRepository).insert(any())
        verifyNoMoreInteractions(teamPlayerRepository)
        assertThat(actual).isNotNull().all {
            prop(TeamPlayer::player).isSameInstanceAs(player)
            prop(TeamPlayer::team).isSameInstanceAs(team)
            prop(TeamPlayer::score).isEqualTo(10)
            prop(TeamPlayer::goals).isEqualTo(9)
            prop(TeamPlayer::shots).isEqualTo(8)
            prop(TeamPlayer::assists).isEqualTo(7)
            prop(TeamPlayer::saves).isEqualTo(6)
            prop(TeamPlayer::demos).isEqualTo(5)
        }
        assertThat(team.playerMatches).contains(actual)
    }
}
