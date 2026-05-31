package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonTeam
import com.janoz.rl.statgatherer.util.ColorUtils.Companion.toColor
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import java.awt.Color

@Entity
class Team : PanacheEntity() {
    @OneToMany(mappedBy = "playerMatch")
    lateinit var playerMatches: MutableList<TeamPlayer>

    @Column(name = NAME_COLUMN)
    lateinit var name: String

    @Column(name = SCORE_COLUMN)
    var score: Int = 0

    @Column(name = PRIMARY_COLOR_COLUMN)
    lateinit var primaryColor: Color

    @Column(name = SECONDARY_COLOR_COLUMN)
    lateinit var secondaryColor: Color

    companion object : PanacheCompanion<Team> {
        const val TABLE_NAME = "TEAMS"
        const val NAME_COLUMN = "NAME"
        const val SCORE_COLUMN = "SCORE"
        const val PRIMARY_COLOR_COLUMN = "PRIMARY_COLOR"
        const val SECONDARY_COLOR_COLUMN = "SECONDARY_COLOR"

        fun make(jsonTeam: JsonTeam?): Team =
            Team()
                .apply {
                    if (jsonTeam == null) {
                        this.name = "Unknown"
                        this.score = 0
                        this.primaryColor = Color.GRAY
                        this.secondaryColor = Color.DARK_GRAY
                    } else {
                        apply(jsonTeam)
                    }
                }.apply { persist() }
    }

    fun apply(jsonTeam: JsonTeam) {
        this.name = jsonTeam.name
        this.score = jsonTeam.score
        this.primaryColor = jsonTeam.colorPrimary.toColor()
        this.secondaryColor = jsonTeam.colorSecondary.toColor()
    }
}
