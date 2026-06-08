package com.janoz.rl.statgatherer.domain

import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.entities.Player
import com.janoz.rl.statgatherer.domain.entities.Team
import com.janoz.rl.statgatherer.domain.json.JsonRocketLeagueMessage
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import io.vertx.core.json.Json
import java.awt.Color
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class Fixtures {
    @OptIn(ExperimentalUuidApi::class)
    companion object {
        val statMsg =
            this::class.java
                .getResourceAsStream("/updateState.txt")
                ?.bufferedReader()
                ?.readLine()!!

        val updateStateData: JsonUpdateStateData = extractData(statMsg, JsonUpdateStateData::class.java)

        val matchGuid = Uuid.parse(updateStateData.matchGuid)

        val aPlayerId = updateStateData.players.first().botSaveId()

        val invalidStatMsg: String = rePack(updateStateData.copy(game = updateStateData.game.copy(teams = emptyList())))

        val players =
            mapOf(
                "Jasper" to Player(name = "Jasper", onlineId = "steam|1|0"),
                "Yasmine" to Player(name = "Yasmine", onlineId = "steam|1|1"),
                "Xavier" to Player(name = "Xavier", onlineId = "steam|1|2"),
                "Stijn" to Player(name = "Stijn", onlineId = "steam|1|3"),
                "Twan" to Player(name = "Twan", onlineId = "steam|1|4"),
                "Isa" to Player(name = "Isa", onlineId = "steam|1|5"),
            )

        fun createTeam(name: String) =
            Team(
                name = name,
                score = 2,
                primaryColor = Color.red,
                secondaryColor = Color.blue,
            )

        fun createMatch(
            matchGuid: Uuid = Uuid.random(),
            firstSeen: Instant = Instant.fromEpochSeconds(Clock.System.now().epochSeconds),
        ) = Match(
            uuid = matchGuid,
            homeTeam = createTeam("Home"),
            awayTeam = createTeam("Away"),
            isFinished = true,
            firstSeen = firstSeen,
        )

        private fun <T> extractData(
            payload: String,
            clazz: Class<T>,
        ): T =
            Json.decodeValue(
                Json.decodeValue(payload, JsonRocketLeagueMessage::class.java).data,
                clazz,
            )

        private fun rePack(date: JsonUpdateStateData) =
            Json.encode(JsonRocketLeagueMessage(event = "UpdateState", data = Json.encode(date)))
    }
}
