package com.janoz.rl.statgatherer.messaging

import com.janoz.rl.statgatherer.domain.Mapper
import com.janoz.rl.statgatherer.domain.json.JsonRocketLeagueMessage
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import io.vertx.core.impl.logging.LoggerFactory
import io.vertx.core.json.Json
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming

@ApplicationScoped
class MessageReceiver {
    private val log = LoggerFactory.getLogger(MessageReceiver::class.java)

    @Incoming("rlapi")
    fun receiveUpdateState(payload: String) {
        val stateData = extractData(payload)
        val match = Mapper().map(stateData)

        log.info("Received matchdata whith GUID: ${stateData.matchGuid}")
    }

    private fun extractData(payload: String): JsonUpdateStateData =
        Json.decodeValue(
            Json.decodeValue(payload, JsonRocketLeagueMessage::class.java).data,
            JsonUpdateStateData::class.java,
        )
}
