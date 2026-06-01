package com.janoz.rl.statgatherer.messaging

import com.janoz.rl.statgatherer.domain.json.JsonRocketLeagueMessage
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import com.janoz.rl.statgatherer.service.MatchService
import com.janoz.rl.statgatherer.service.TimeService
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.Json
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@ApplicationScoped
class MessageReceiver(
    private val timeService: TimeService,
    private val matchService: MatchService,
) {
    private val log = LoggerFactory.getLogger(MessageReceiver::class.java)

    private val matchesFirstSeen: MutableMap<Uuid, Instant> = HashMap()

    @Incoming("rlapi")
    fun receiveUpdateState(payload: String) {
        val stateData = extractData(payload)
        log.info("Received matchdata whith GUID: ${stateData.matchGuid}")
        if (stateData.game.isValid()) {
            val matchGuid = Uuid.parse(stateData.matchGuid)
            if (stateData.game.isDone) {
                val firstSeen = matchesFirstSeen[matchGuid] ?: timeService.now()
                matchService.create(stateData, firstSeen)
                matchesFirstSeen.remove(matchGuid)
            } else {
                matchesFirstSeen.computeIfAbsent(matchGuid) { timeService.now() }
            }
        }
    }

    private fun extractData(payload: String): JsonUpdateStateData =
        Json.decodeValue(
            Json.decodeValue(payload, JsonRocketLeagueMessage::class.java).data,
            JsonUpdateStateData::class.java,
        )

    fun reset() {
        matchesFirstSeen.clear()
    }
}
