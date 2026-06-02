package com.janoz.rl.statgatherer.repository

import com.janoz.rl.statgatherer.domain.Fixtures
import com.janoz.rl.statgatherer.service.MatchService
import io.vertx.mutiny.sqlclient.Pool
import jakarta.enterprise.context.ApplicationScoped
import kotlin.time.Clock

@ApplicationScoped
class TestSupport(
    private val client: Pool,
    private val matchService: MatchService,
) {
    fun insertMatch() {
        matchService.create(Fixtures.updateStateData, Clock.System.now())
    }

    fun clear() {
        client
            .query("DELETE FROM TEAM_PLAYERS")
            .execute()
            .flatMap {
                client.query("DELETE FROM MATCHES").execute()
            }.flatMap {
                client.query("DELETE FROM PLAYERS").execute()
            }.flatMap {
                client.query("DELETE FROM TEAMS").execute()
            }.await()
            .indefinitely()
    }
}
