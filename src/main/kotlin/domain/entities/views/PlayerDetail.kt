package com.janoz.rl.statgatherer.domain.entities.views

import com.janoz.rl.statgatherer.domain.entities.Player

data class PlayerDetail(
    val player: Player,
    val matches: Int = 0,
    val goals: Int = 0,
    val shots: Int = 0,
    val assists: Int = 0,
    val saves: Int = 0,
    val demos: Int = 0,
)
