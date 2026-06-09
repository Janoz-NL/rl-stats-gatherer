package com.janoz.rl.statgatherer.domain.entities.enums

enum class Order {
    ASC,
    DESC,
    ;

    fun label(): String = name

    fun reverse(): Order = if (this == ASC) DESC else ASC

    companion object {
        fun of(event: String?): Order? = entries.find { it.name == event }
    }
}

enum class SortColumnPlayer {
    NAME,
    MATCHES,
    GOALS,
    SHOTS,
    ASSISTS,
    SAVES,
    DEMOS,
    ;

    fun label(): String = name

    companion object {
        fun of(event: String?): SortColumnPlayer? = entries.find { it.name == event }
    }
}

enum class SortColumnMatch {
    HOME_TEAM_NAME,
    AWAY_TEAM_NAME,
    FIRST_SEEN,
    ;

    fun label(): String = name

    companion object {
        fun of(event: String?): SortColumnMatch? = entries.find { it.name == event }
    }
}
