package com.janoz.rl.statgatherer.repository

import assertk.all
import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.hasSize
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import assertk.assertions.matchesPredicate
import assertk.assertions.message
import assertk.assertions.prop
import com.janoz.rl.statgatherer.domain.Fixtures.Companion.createMatch
import com.janoz.rl.statgatherer.domain.entities.Link
import com.janoz.rl.statgatherer.domain.entities.Match
import com.janoz.rl.statgatherer.domain.entities.enums.UrlType
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
@QuarkusTest
class MatchRepositoryTest {
    @Inject
    private lateinit var testSupport: TestSupport

    @Inject
    private lateinit var cut: MatchRepository

    @BeforeEach
    fun setup() {
        testSupport.clear()
    }

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
    fun `insert match with same id fails`() {
        val match = createMatch(id(4))
        cut.insert(match)

        assertFailure { cut.insert(match) }.message().isNotNull().matchesPredicate {
            it.contains("unique constraint")
            it.contains("_pkey\"")
        }
    }

    @Test
    fun `find all matches`() {
        val match1 = createMatch(id(1), Instant.parse("2016-02-15T12:00:00Z"))
        cut.insert(match1)
        val match2 = createMatch(id(2), Instant.parse("2016-02-15T13:00:00Z"))
        cut.insert(match2)

        val actual = cut.findAll()

        assertThat(actual).containsExactly(match2, match1)
    }

    @Test
    fun `find match with all links`() {
        val match1 = createMatch(id(1), Instant.parse("2016-02-15T12:00:00Z"))
        cut.insert(match1)
        val match2 = createMatch(id(2), Instant.parse("2016-02-15T13:00:00Z"))
        cut.insert(match2)

        cut.insertLink(match2.uuid, Link(UrlType.YOUTUBE, "http://youtube.com/"))
        cut.insertLink(match2.uuid, Link(UrlType.REPLAY, "blaat.replay"))
        cut.insertLink(match2.uuid, Link(UrlType.OTHER, "something"))

        val actual = cut.findAll()

        assertThat(actual.map { it.uuid }).containsExactly(id(2), id(1))
        assertThat(actual[1]).isEqualTo(match1)
        assertThat(actual[1].links).isNotNull().isEmpty()
        assertThat(actual[0].links).isNotNull().hasSize(3)
    }

    private fun id(Id: Long) =
        Uuid.fromLongs(
            Id,
            0,
        )
}
