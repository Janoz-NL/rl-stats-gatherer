package com.janoz.rl.statgatherer.resource

import com.janoz.rl.statgatherer.domain.entities.enums.Order
import com.janoz.rl.statgatherer.domain.entities.enums.SortColumnPlayer
import com.janoz.rl.statgatherer.domain.entities.views.PlayerDetail
import com.janoz.rl.statgatherer.service.PlayerService
import io.quarkus.qute.CheckedTemplate
import io.quarkus.qute.TemplateInstance
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import org.jboss.resteasy.reactive.RestQuery
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@Path("/players")
class PlayerResource(
    val playerService: PlayerService,
) {
    @GET
    @Produces(MediaType.TEXT_HTML)
    fun list(
        @RestQuery orderBy: String?,
        @RestQuery order: String?,
    ): TemplateInstance? {
        val eSortBy = SortColumnPlayer.of(orderBy) ?: SortColumnPlayer.NAME
        val eOrder = Order.of(order) ?: Order.ASC
        return Templates.list(
            playerService.findAll(eSortBy, eOrder),
            eSortBy,
            eOrder,
        )
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.TEXT_HTML)
    fun player(id: String): TemplateInstance? = Templates.player(playerService.find(Uuid.parse(id)))

    @CheckedTemplate(basePath = "player")
    private object Templates {
        @JvmStatic
        external fun list(
            players: List<PlayerDetail>,
            sortBy: SortColumnPlayer,
            order: Order,
            reverseOrder: Order = order.reverse(),
        ): TemplateInstance

        @JvmStatic
        external fun player(player: PlayerDetail?): TemplateInstance
    }
}
