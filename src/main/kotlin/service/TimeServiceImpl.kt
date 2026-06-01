package com.janoz.rl.statgatherer.service

import jakarta.enterprise.context.ApplicationScoped
import kotlin.time.Clock

@ApplicationScoped
class TimeServiceImpl : TimeService {
    override fun now() = Clock.System.now()
}
