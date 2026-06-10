package com.janoz.rl.statgatherer.messaging

import com.janoz.rl.statgatherer.domain.Fixtures
import com.janoz.rl.statgatherer.service.MatchService
import com.janoz.rl.statgatherer.service.TimeService
import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.times
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
        val unfinishedMatch = Fixtures.statMsg.replace("\\\"bHasWinner\\\":true", "\\\"bHasWinner\\\":false")
        val finishedMatch = Fixtures.statMsg
        val earlier = Instant.parse("2016-02-15T11:00:00Z")
        val now = Instant.parse("2016-02-15T12:00:00Z")
        val later = Instant.parse("2016-02-15T13:00:00Z")
        whenever(timeService.now()).thenReturn(earlier, now, later)

        cut.receiveUpdateState(unfinishedMatch)

        cut.receiveUpdateState(unfinishedMatch)

        cut.receiveUpdateState(finishedMatch)

        verify(timeService, times(3)).now()
        verify(matchService).create(any(), eq(earlier), eq(later))
        verifyNoMoreInteractions(matchService)
        verifyNoMoreInteractions(timeService)
    }

    @Test
    fun `receive single finished match should do nothing`() {
        val finishedMatch = Fixtures.statMsg
        val now = Instant.parse("2016-02-15T12:00:00Z")
        whenever(timeService.now()).thenReturn(now)

        cut.receiveUpdateState(finishedMatch)

        verify(timeService).now()
        verifyNoMoreInteractions(matchService, timeService)
    }

    @Test
    fun `receive invalid match`() {
        val invalidMatch = Fixtures.invalidStatMsg
        val now = Instant.parse("2016-02-15T12:00:00Z")
        whenever(timeService.now()).thenReturn(now)

        cut.receiveUpdateState(invalidMatch)

        verify(timeService).now()
        verifyNoMoreInteractions(matchService, timeService)
    }
}
