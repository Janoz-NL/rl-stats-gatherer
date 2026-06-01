package com.janoz.rl.statgatherer.repository

import assertk.all
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import assertk.assertions.message
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createMatch
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createTeam
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class TeamPlayerRepositoryTest {
    @Inject
    private lateinit var playerRepo: PlayerRepository

    @Inject
    private lateinit var matchRepo: MatchRepository

    @Inject
    private lateinit var cut: TeamPlayerRepository

    @Test
    fun `find non existing teamplayer`() {
        val team = createTeam("teamName")
        val player = Player(id(1), "steam|2|1", "name")

        assertThat(cut.findByTeamAndPlayer(team, player)).isNull()
    }

    @Test
    fun `insert and find it`() {
        val match = createMatch(id(1))
        val team = match.homeTeam
        val player = Player(id(2), "steam|2|2", "name")

        playerRepo.insert(player)
        matchRepo.insert(match)

        val teamPlayer =
            TeamPlayer(
                team = team,
                player = player,
                score = 10,
                goals = 9,
                shots = 8,
                assists = 7,
                saves = 6,
                demos = 5,
            )
        cut.insert(teamPlayer)

        val actual = cut.findByTeamAndPlayer(team, player)

        assertThat(actual).isEqualTo(teamPlayer)
        assertThat(actual).isNotNull().all {
            prop(TeamPlayer::team).isEqualTo(team)
            prop(TeamPlayer::player).isEqualTo(player)
            prop(TeamPlayer::team).all {
                prop(Team::playerMatches).contains(actual)
            }
        }
    }

    @Test
    fun `insert with unknown team should fail`() {
        val team = createTeam("unknownTeam")
        val player = Player(id(3), "steam|2|3", "name")

        playerRepo.insert(player)

        val teamPlayer =
            TeamPlayer(
                team = team,
                player = player,
                score = 10,
                goals = 9,
                shots = 8,
                assists = 7,
                saves = 6,
                demos = 5,
            )
        assertFailure { cut.insert(teamPlayer) }.message().isNotNull().matchesPredicate {
            it.contains("foreign key constraint")
            it.contains("\"team_players_team_id_fkey\"")
        }
    }

    @Test
    fun `insert with unknown player should fail`() {
        val match = createMatch(id(2))
        val team = match.awayTeam
        val player = Player(id(2), "steam|2|4", "name")
        matchRepo.insert(match)

        val teamPlayer =
            TeamPlayer(
                team = team,
                player = player,
            )
        assertFailure { cut.insert(teamPlayer) }.message().isNotNull().matchesPredicate {
            it.contains("foreign key constraint")
            it.contains("\"team_players_player_id_fkey\"")
        }
    }

    private fun id(Id: Long) =
        Uuid.fromLongs(
            Id,
            2,
        )
}
