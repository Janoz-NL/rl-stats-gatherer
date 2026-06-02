package com.janoz.rl.statgatherer.repository

import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.entities.TeamPlayer
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid

@OptIn(ExperimentalUuidApi::class)
@ApplicationScoped
class TeamPlayerRepository(
    private val client: Pool,
) {
    fun listByTeam(team: Team): List<TeamPlayer> =
        client
            .preparedQuery("SELECT ${COLUMNS_PLAYER} FROM ${TABLE_PLAYER} WHERE TEAM_ID = $1")
            .execute(Tuple.of(team.uuid.toJavaUuid()))
            .onItem()
            .transform { rows ->
                rows.map {
                    rowMapper(it, team, PlayerRepository.rowMapper(it))
                }
            }.await()
            .indefinitely()

    fun findByTeamAndPlayer(
        team: Team,
        player: Player,
    ): TeamPlayer? =
        client
            .preparedQuery(
                "SELECT $COLUMNS FROM $TABLE WHERE TEAM_ID = $1 AND PLAYER_ID = $2",
            ).execute(Tuple.of(team.uuid.toJavaUuid(), player.uuid.toJavaUuid()))
            .onItem()
            .transform { row -> row.firstOrNull()?.let { rowMapper(it, team, player) } }
            .await()
            .indefinitely()

    fun insert(teamPlayer: TeamPlayer): TeamPlayer {
        client
            .preparedQuery(
                "INSERT INTO TEAM_PLAYERS (" +
                    "TEAM_ID, " +
                    "PLAYER_ID," +
                    "SCORE," +
                    "GOALS," +
                    "SHOTS ," +
                    "ASSISTS," +
                    "SAVES," +
                    "DEMOS )  " +
                    "VALUES ($1, $2, $3, $4, $5, $6, $7, $8)",
            ).execute(
                Tuple.from(
                    listOf(
                        teamPlayer.team.uuid.toJavaUuid(),
                        teamPlayer.player.uuid.toJavaUuid(),
                        teamPlayer.score,
                        teamPlayer.goals,
                        teamPlayer.shots,
                        teamPlayer.assists,
                        teamPlayer.saves,
                        teamPlayer.demos,
                    ),
                ),
            ).await()
            .indefinitely()
        return teamPlayer
    }

    companion object {
        val COLUMNS = "SCORE, GOALS, SHOTS, ASSISTS, SAVES, DEMOS"
        val TABLE = "TEAM_PLAYERS"

        val COLUMNS_PLAYER = "PLAYER_ID AS ID, NAME, ONLINE_ID, $COLUMNS"
        val TABLE_PLAYER = "TEAM_PLAYERS JOIN PLAYERS ON PLAYER_ID = PLAYERS.ID "

        fun rowMapper(
            row: Row,
            team: Team,
            player: Player,
        ): TeamPlayer =
            TeamPlayer(
                team = team,
                player = player,
                score = row.getInteger("score"),
                goals = row.getInteger("goals"),
                shots = row.getInteger("shots"),
                assists = row.getInteger("assists"),
                saves = row.getInteger("saves"),
                demos = row.getInteger("demos"),
            ).also { team.playerMatches.add(it) }
    }
}
