package com.janoz.rl.statgatherer.resource

import com.janoz.rl.statgatherer.domain.entities.views.PlayerDetail
import com.janoz.rl.statgatherer.service.PlayerService
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Path("/players")
class PlayerResource(
    val playerService: PlayerService,
) {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun list(): TemplateInstance? = Templates.list(playerService.findAll())

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun player(id: String): TemplateInstance? = Templates.player(playerService.find(Uuid.parse(id)))

    @CheckedTemplate(basePath = "player")
    private object Templates {
        @JvmStatic
        external fun list(players: List<PlayerDetail>): TemplateInstance

        @JvmStatic
        external fun player(player: PlayerDetail?): TemplateInstance
    }
}
