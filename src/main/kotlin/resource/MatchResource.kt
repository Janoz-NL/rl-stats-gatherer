package com.janoz.rl.statgatherer.resource

import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.service.MatchService
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Path("/matches")
class MatchResource(
    val matchService: MatchService,
) {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun list(): TemplateInstance? = Templates.list(matchService.findAll())

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun player(id: String): TemplateInstance? = Templates.match(matchService.find(Uuid.parse(id)))

    @CheckedTemplate(basePath = "match")
    private object Templates {
        @JvmStatic
        external fun list(matches: List<Match>): TemplateInstance

        @JvmStatic
        external fun match(match: Match?): TemplateInstance
    }
}
