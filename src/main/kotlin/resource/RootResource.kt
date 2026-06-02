package com.janoz.rl.statgatherer.resource

import io.quarkus.qute.Template
import io.quarkus.qute.TemplateInstance
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType

@Path("/")
class RootResource {
    @Inject
    private lateinit var index: Template

    @GET
    @Produces(MediaType.TEXT_HTML)
    fun index(): TemplateInstance? = index.instance()
}
