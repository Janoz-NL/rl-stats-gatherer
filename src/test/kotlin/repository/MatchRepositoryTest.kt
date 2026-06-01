package com.janoz.rl.statgatherer.repository

import assertk.all
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import assertk.assertions.message
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createMatch
import com.janoz.rl.statgatherer.domain.entities.Match
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Test
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class MatchRepositoryTest {
    @Inject
    private lateinit var cut: MatchRepository

    @Test
    fun `find non existing match`() {
        assertThat(cut.findById(id(1))).isNull()
    }

    @Test
    fun `insert match and finding it by id`() {
        val match = createMatch(id(2))

        cut.insert(match)

        val actual = cut.findById(id(2))
        assertThat(cut.findById(id(3))).isNull()

        assertThat(actual).isEqualTo(match)
        assertThat(actual).isNotNull().all {
            prop(Match::homeTeam).isEqualTo(match.homeTeam)
            prop(Match::awayTeam).isEqualTo(match.awayTeam)
        }
    }

    @Test
    fun `insert player with same id fails`() {
        val match = createMatch(id(4))
        cut.insert(match)

        assertFailure { cut.insert(match) }.message().isNotNull().matchesPredicate {
            it.contains("unique constraint")
            it.contains("_pkey\"")
        }
    }

    private fun id(Id: Long) =
        Uuid.fromLongs(
            Id,
            0,
        )
}
