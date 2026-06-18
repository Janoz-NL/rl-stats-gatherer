package com.janoz.rl.statgatherer.repository

import com.janoz.rl.statgatherer.domain.entities.Link
import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.entities.enums.Order
import com.janoz.rl.statgatherer.domain.entities.enums.SortColumnMatch
import com.janoz.rl.statgatherer.domain.entities.enums.UrlType
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toColor
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toHex
import io.vertx.mutiny.sqlclient.Pool
import io.vertx.mutiny.sqlclient.Row
import io.vertx.mutiny.sqlclient.RowSet
import io.vertx.mutiny.sqlclient.Tuple
import jakarta.enterprise.context.ApplicationScoped
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.UUID
import kotlin.time.toJavaInstant
import kotlin.time.toKotlinInstant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

@ApplicationScoped
@OptIn(ExperimentalUuidApi::class)
class MatchRepository(
    private val client: Pool,
) {
    fun findAll(): List<Match> = findAll(SortColumnMatch.FIRST_SEEN, Order.DESC)

    fun findAll(
        sortBy: SortColumnMatch,
        order: Order,
    ) = findAll(
        " ORDER BY ${sortBy.name} $order, MU.TYPE",
    )

    private fun findAll(order: String): List<Match> =
        client
            .query("SELECT $COLUMNS FROM $TABLE $order")
            .execute()
            .onItem()
            .transform { rows -> rowsMapper(rows) }
            .await()
            .indefinitely()

    fun findById(matchGuid: Uuid): Match? =
        client
            .preparedQuery(
                "SELECT $COLUMNS FROM $TABLE WHERE M.ID = $1",
            ).execute(Tuple.of(matchGuid.toJavaUuid()))
            .onItem()
            .transform { row -> row.firstOrNull()?.let { rowMapper(it) } }
            .await()
            .indefinitely()

    fun insert(match: Match): Match {
        client
            .preparedQuery("INSERT INTO TEAMS (ID,NAME,PRIMARY_COLOR,SECONDARY_COLOR,SCORE) VALUES ($1, $2, $3, $4, $5)")
            .execute(
                Tuple.of(
                    match.homeTeam.uuid.toJavaUuid(),
                    match.homeTeam.name,
                    match.homeTeam.primaryColor.toHex(),
                    match.homeTeam.secondaryColor.toHex(),
                    match.homeTeam.score,
                ),
            ).flatMap {
                client
                    .preparedQuery("INSERT INTO TEAMS (ID,NAME,PRIMARY_COLOR,SECONDARY_COLOR,SCORE) VALUES ($1, $2, $3, $4, $5)")
                    .execute(
                        Tuple.of(
                            match.awayTeam.uuid.toJavaUuid(),
                            match.awayTeam.name,
                            match.awayTeam.primaryColor.toHex(),
                            match.awayTeam.secondaryColor.toHex(),
                            match.awayTeam.score,
                        ),
                    )
            }.flatMap {
                client
                    .preparedQuery(
                        "INSERT INTO MATCHES (ID, HOME_TEAM_ID, AWAY_TEAM_ID, FIRST_SEEN, LAST_SEEN) VALUES ($1, $2, $3, $4, $5)",
                    ).execute(
                        Tuple.of(
                            match.uuid.toJavaUuid(),
                            match.homeTeam.uuid.toJavaUuid(),
                            match.awayTeam.uuid.toJavaUuid(),
                            match.firstSeen
                                .toJavaInstant()
                                .atOffset(ZoneOffset.ofHours(0))
                                .toLocalDateTime(),
                            match.lastSeen
                                .toJavaInstant()
                                .atOffset(ZoneOffset.ofHours(0))
                                .toLocalDateTime(),
                        ),
                    )
            }.await()
            .indefinitely()
        return match
    }

    fun insertLink(
        matchId: Uuid,
        link: Link,
    ) {
        client
            .preparedQuery(
                "INSERT INTO MATCH_URLS (ID, MATCH_ID, TYPE, URL) VALUES ($1, $2, $3, $4)",
            ).execute(
                Tuple.of(
                    UUID.randomUUID(),
                    matchId.toJavaUuid(),
                    link.type.name,
                    link.url,
                ),
            ).await()
            .indefinitely()
    }

    companion object {
        private val COLUMNS =
            "M.ID AS ID, " +
                "M.FIRST_SEEN AS FIRST_SEEN, " +
                "M.LAST_SEEN AS LAST_SEEN, " +
                "HT.ID AS HOME_TEAM_ID, " +
                "HT.NAME AS HOME_TEAM_NAME, " +
                "HT.PRIMARY_COLOR AS HOME_TEAM_PRIMARY_COLOR, " +
                "HT.SECONDARY_COLOR AS HOME_TEAM_SECONDARY_COLOR, " +
                "HT.SCORE AS HOME_TEAM_SCORE, " +
                "AT.ID AS AWAY_TEAM_ID, " +
                "AT.NAME AS AWAY_TEAM_NAME, " +
                "AT.PRIMARY_COLOR AS AWAY_TEAM_PRIMARY_COLOR, " +
                "AT.SECONDARY_COLOR AS AWAY_TEAM_SECONDARY_COLOR, " +
                "AT.SCORE AS AWAY_TEAM_SCORE, " +
                "MU.TYPE AS URL_TYPE, " +
                "MU.URL AS URL_URL "
        private val TABLE =
            "MATCHES M " +
                "JOIN TEAMS HT ON HT.ID = M.HOME_TEAM_ID " +
                "JOIN TEAMS AT ON AT.ID = M.AWAY_TEAM_ID " +
                "LEFT JOIN MATCH_URLS MU ON MU.MATCH_ID = M.ID "

        private fun rowsMapper(rows: RowSet<Row>): List<Match> {
            val result = mutableListOf<Match>()
            val map = HashMap<Uuid, Match>()
            rows.forEach { row ->
                val match =
                    map.computeIfAbsent(row.get(UUID::class.java, "id").toKotlinUuid()) {
                        rowMapper(row).also { result.add(it) }
                    }
                if (row.getString("url_type") != null) {
                    match.links.add(
                        Link(
                            UrlType.of(row.getString("url_type")),
                            row.getString("url_url"),
                        ),
                    )
                }
            }
            return result
        }

        private fun rowMapper(row: Row): Match =
            Match(
                uuid = row.get(UUID::class.java, "id").toKotlinUuid(),
                firstSeen = row.get(LocalDateTime::class.java, "first_seen").toInstant(ZoneOffset.UTC).toKotlinInstant(),
                lastSeen = row.get(LocalDateTime::class.java, "last_seen").toInstant(ZoneOffset.UTC).toKotlinInstant(),
                isFinished = true,
                homeTeam =
                    Team(
                        uuid = row.get(UUID::class.java, "home_team_id").toKotlinUuid(),
                        name = row.get(String::class.java, "home_team_name"),
                        primaryColor = row.get(String::class.java, "home_team_primary_color").toColor(),
                        secondaryColor = row.get(String::class.java, "home_team_secondary_color").toColor(),
                        score = row.getInteger("home_team_score"),
                    ),
                awayTeam =
                    Team(
                        uuid = row.get(UUID::class.java, "away_team_id").toKotlinUuid(),
                        name = row.get(String::class.java, "away_team_name"),
                        primaryColor = row.get(String::class.java, "away_team_primary_color").toColor(),
                        secondaryColor = row.get(String::class.java, "away_team_secondary_color").toColor(),
                        score = row.getInteger("away_team_score"),
                    ),
            )
    }
}
