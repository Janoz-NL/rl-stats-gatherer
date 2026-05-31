package com.janoz.rl.statgatherer.messaging

import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test

class MessageReceiverTest {
    val cut = MessageReceiver()

    @Test
    fun testMessage() {
        val lines =
            this::class.java
                .getResourceAsStream("/messages.txt")
                ?.bufferedReader()
                ?.readLines()
        if (lines != null) {
            for (line in lines) {
                cut.receiveUpdateState(line)
            }
        } else {
            fail("No messages found")
        }
    }
}
