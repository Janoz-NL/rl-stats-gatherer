package com.janoz.rl.statgatherer.repository

import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.views.PlayerDetail
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class PlayerRepository(
    private val client: Pool,
) {
    fun get(uuid: Uuid): Player =
        client
            .preparedQuery("SELECT $COLUMNS FROM $TABLE WHERE ID = $1")
            .execute(Tuple.of(uuid.toJavaUuid()))
            .onItem()
            .transform { row -> row.first().let { rowMapper(it) } }
            .await()
            .indefinitely()

    fun findAll(): List<PlayerDetail> =
        client
            .query("SELECT ${COLUMNS_DETAIL} FROM ${TABLE_DETAIL} GROUP BY P.ID")
            .execute()
            .onItem()
            .transform { rows -> rows.map { rowDetailMapper(it) } }
            .await()
            .indefinitely()

    fun findDetails(uuid: Uuid): PlayerDetail? =
        client
            .preparedQuery("SELECT ${COLUMNS_DETAIL} FROM ${TABLE_DETAIL} WHERE P.ID = $1 GROUP BY P.ID")
            .execute(Tuple.of(uuid.toJavaUuid()))
            .onItem()
            .transform { row -> row.firstOrNull()?.let { rowDetailMapper(it) } }
            .await()
            .indefinitely()

    fun findByOnlineId(onlineId: String): Player? =
        client
            .preparedQuery("SELECT ID, NAME, ONLINE_ID FROM PLAYERS WHERE ONLINE_ID = $1")
            .execute(Tuple.of(onlineId))
            .onItem()
            .transform { row -> row.firstOrNull()?.let { rowMapper(it) } }
            .await()
            .indefinitely()

    fun insert(player: Player): Player {
        client
            .preparedQuery("INSERT INTO PLAYERS (ID, NAME, ONLINE_ID) VALUES ($1, $2, $3)")
            .execute(Tuple.of(player.uuid.toJavaUuid(), player.name, player.onlineId))
            .await()
            .indefinitely()

        return player
    }

    fun update(player: Player) {
        client
            .preparedQuery("UPDATE PLAYERS SET NAME = $1 WHERE ID = $2")
            .execute(Tuple.of(player.name, player.uuid.toJavaUuid()))
            .await()
            .indefinitely()
    }

    companion object {
        val COLUMNS = " P.ID AS ID, P.NAME AS NAME, P.ONLINE_ID AS ONLINE_ID"
        val COLUMNS_DETAIL =
            "P.ID AS ID, " +
                "P.NAME AS NAME, " +
                "P.ONLINE_ID AS ONLINE_ID, " +
                " COUNT(*) AS MATCHES, " +
                "SUM(TP.GOALS) AS GOALS, " +
                "SUM(TP.SHOTS) AS SHOTS, " +
                "SUM(TP.ASSISTS) AS ASSISTS, " +
                "SUM(TP.SAVES) AS SAVES, " +
                "SUM(TP.DEMOS) AS DEMOS"

        val TABLE = "PLAYERS P"
        val TABLE_DETAIL = "PLAYERS P LEFT JOIN TEAM_PLAYERS TP ON TP.PLAYER_ID = P.ID"

        fun rowMapper(row: Row): Player =
            Player(
                row.get(UUID::class.java, "id").toKotlinUuid(),
                row.get(String::class.java, "online_id"),
                row.get(String::class.java, "name"),
            )

        fun rowDetailMapper(row: Row): PlayerDetail =
            PlayerDetail(
                player = rowMapper(row),
                matches = row.getInteger("matches"),
                goals = row.getInteger("goals"),
                shots = row.getInteger("shots"),
                assists = row.getInteger("assists"),
                saves = row.getInteger("saves"),
                demos = row.getInteger("demos"),
            )
    }
}
