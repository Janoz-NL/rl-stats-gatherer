package com.janoz.rl.statgatherer.messaging

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.janoz.rl.statgatherer.domain.Fixtures
import com.janoz.rl.statgatherer.domain.json.JsonUpdateStateData
import com.janoz.rl.statgatherer.service.MatchService
import com.janoz.rl.statgatherer.service.TimeService
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import kotlin.time.Instant

@QuarkusTest
class MessageReceiverTest {
    @Inject
    private lateinit var cut: MessageReceiver

    @InjectMock
    private lateinit var matchService: MatchService

    @InjectMock
    private lateinit var timeService: TimeService

    @BeforeEach
    fun setup() {
        cut.reset()
    }

    @Test
    fun `receive one unfinished match`() {
        val unfinishedMatxh = Fixtures.statMsg.replace("\\\"bHasWinner\\\":true", "\\\"bHasWinner\\\":false")
        val now = Instant.parse("2016-02-15T12:00:00Z")
        whenever(timeService.now()).thenReturn(now)

        cut.receiveUpdateState(unfinishedMatxh)

        verify(timeService).now()
        verifyNoInteractions(matchService)
        verifyNoMoreInteractions(timeService)
    }

    @Test
    fun `receive two unfinished matches doesnt change time`() {
        val unfinishedMatxh = Fixtures.statMsg.replace("\\\"bHasWinner\\\":true", "\\\"bHasWinner\\\":false")
        val now = Instant.parse("2016-02-15T12:00:00Z")
        whenever(timeService.now()).thenReturn(now)

        cut.receiveUpdateState(unfinishedMatxh)

        verify(timeService).now()

        cut.receiveUpdateState(unfinishedMatxh)

        verifyNoInteractions(matchService)
        verifyNoMoreInteractions(timeService)
    }

    @Test
    fun `receive finished match`() {
        val finishedMatch = Fixtures.statMsg
        val now = Instant.parse("2016-02-15T12:00:00Z")
        whenever(timeService.now()).thenReturn(now)
        val captor = argumentCaptor<JsonUpdateStateData>()
        doNothing().whenever(matchService).create(captor.capture(), any())

        cut.receiveUpdateState(finishedMatch)

        verify(timeService).now()
        verify(matchService).create(any(), eq(now))
        verifyNoMoreInteractions(matchService, timeService)
        assertThat(captor.firstValue.matchGuid).isEqualTo("4366BADC95F480E34366BADC95F480E3")
    }

    @Test
    fun `receive invalid match`() {
        val invalidMatch = Fixtures.invalidStatMsg
        cut.receiveUpdateState(invalidMatch)
        verifyNoMoreInteractions(matchService, timeService)
    }
}
