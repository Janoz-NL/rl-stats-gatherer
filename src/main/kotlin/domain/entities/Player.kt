package com.janoz.rl.statgatherer.domain.entities

import com.janoz.rl.statgatherer.domain.json.JsonPlayer
import io.quarkus.hibernate.orm.panache.kotlin.PanacheCompanion
import io.quarkus.hibernate.orm.panache.kotlin.PanacheEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity

@Entity
class Player : PanacheEntity() {
    @Column(name = ONLINE_ID_COLUMN)
    lateinit var onlineId: String

    @Column(name = NAME_COLUMN)
    lateinit var name: String

    companion object : PanacheCompanion<Player> {
        const val TABLE_NAME = "PLAYERS"
        const val ONLINE_ID_COLUMN = "ONLINE_ID"
        const val NAME_COLUMN = "NAME"

        fun findByOnlineId(onlineId: String): Player? = find(ONLINE_ID_COLUMN, onlineId).firstResult()

        fun findOrMake(jsonPLayer: JsonPlayer): Player =
            findByOnlineId(jsonPLayer.botSaveId()) ?: Player()
                .apply {
                    this.onlineId = jsonPLayer.botSaveId()
                    this.name = jsonPLayer.name
                }.apply { persist() }
    }
}
