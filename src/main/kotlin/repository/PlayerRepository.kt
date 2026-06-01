package com.janoz.rl.statgatherer.repository

import com.janoz.rl.statgatherer.domain.entities.Player
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.util.UUID
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class PlayerRepository(
    private val client: Pool,
) {
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

    private fun rowMapper(row: Row): Player =
        Player(
            row.get(UUID::class.java, "id").toKotlinUuid(),
            row.get(String::class.java, "online_id"),
            row.get(String::class.java, "name"),
        )

    companion object {
        const val TABLE_NAME = "PLAYERS"
        const val ONLINE_ID_COLUMN = "ONLINE_ID"
        const val NAME_COLUMN = "NAME"
    }
}
