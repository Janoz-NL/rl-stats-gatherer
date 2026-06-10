package com.janoz.rl.statgatherer.messaging

import com.janoz.rl.statgatherer.domain.json.JsonRocketLeagueMessage
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import com.janoz.rl.statgatherer.service.MatchService
import com.janoz.rl.statgatherer.service.TimeService
import io.vertx.core.impl.logging.LoggerFactory
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
        receiveUpdateState(payload, timeService.now())
    }

    fun receiveUpdateState(
        payload: String,
        timestamp: Instant,
    ) {
        val stateData = extractData(payload)
        log.info("Received matchdata whith GUID: ${stateData.matchGuid}")
        if (stateData.game.isValid()) {
            val matchGuid = Uuid.parse(stateData.matchGuid)
            if (stateData.game.isDone) {
                val firstSeen = matchesFirstSeen[matchGuid]
                if (firstSeen != null) {
                    // Only store finished matched states and only store when we have seen unfinished
                    // states before. Workaround for a bug in Rocket League stats API where the last
                    // messages are sent with the UUID of the next message
                    matchService.create(stateData, firstSeen, timestamp)
                    matchesFirstSeen.remove(matchGuid)
                }
            } else {
                matchesFirstSeen.computeIfAbsent(matchGuid) { timestamp }
            }
        }
    }

    private fun extractData(payload: String): JsonUpdateStateData = JsonUpdateStateData.parse(JsonRocketLeagueMessage.parse(payload).data)

    fun reset() {
        matchesFirstSeen.clear()
    }
}
