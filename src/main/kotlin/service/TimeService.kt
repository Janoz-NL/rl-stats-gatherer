package com.janoz.rl.statgatherer.service

import kotlin.time.Instant

interface TimeService {
    fun now(): Instant
}
