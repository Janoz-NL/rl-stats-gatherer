package com.janoz.rl.statgatherer.domain.entities.enums

import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
enum class Order {
    ASC,
    DESC,
    ;

    fun reverse(): Order = if (this == ASC) DESC else ASC

    companion object {
        fun of(event: String?): Order? = entries.find { it.name == event }
    }
}

@RegisterForReflection
enum class SortColumnPlayer {
    NAME,
    MATCHES,
    GOALS,
    SHOTS,
    ASSISTS,
    SAVES,
    DEMOS,
    ;

    companion object {
        fun of(event: String?): SortColumnPlayer? = entries.find { it.name == event }
    }
}

@RegisterForReflection
enum class SortColumnMatch {
    HOME_TEAM_NAME,
    AWAY_TEAM_NAME,
    FIRST_SEEN,
    ;

    companion object {
        fun of(event: String?): SortColumnMatch? = entries.find { it.name == event }
    }
}
