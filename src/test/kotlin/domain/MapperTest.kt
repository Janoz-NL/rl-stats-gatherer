package com.janoz.rl.statgatherer.domain

import com.janoz.rl.statgatherer.domain.json.JsonRocketLeagueMessage
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import io.quarkus.test.TestTransaction
import io.quarkus.test.junit.QuarkusIntegrationTest
import io.vertx.core.json.Json
import org.junit.jupiter.api.Test

@QuarkusIntegrationTest
class MapperTest {
    @Test
    @TestTransaction
    fun testMapper() {
        val mapper = Mapper()
        val jsonGameState = extractData(lines?.get(0)!!)

        mapper.map(jsonGameState)
        println("klaar")
    }

    val lines =
        this::class.java
            .getResourceAsStream("/messages.txt")
            ?.bufferedReader()
            ?.readLines()

    private fun extractData(payload: String): JsonUpdateStateData =
        Json.decodeValue(
            Json.decodeValue(payload, JsonRocketLeagueMessage::class.java).data,
            JsonUpdateStateData::class.java,
        )
}
