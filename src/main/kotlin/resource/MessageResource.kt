package com.janoz.rl.statgatherer.resource

import com.janoz.rl.statgatherer.domain.entities.Match
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path

@Path("/messages")
class MessageResource {
    @GET
    suspend fun get(): List<Match> = emptyList()
}
