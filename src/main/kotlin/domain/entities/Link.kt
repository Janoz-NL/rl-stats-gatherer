package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.entities.enums.UrlType
import io.quarkus.qute.TemplateData

@TemplateData
class Link(
    val type: UrlType,
    val url: String,
)
