package com.janoz.rl.statgatherer.messaging

import io.smallrye.reactive.messaging.mqtt.MqttMessage
import jakarta.enterprise.context.ApplicationScoped
import org.eclipse.microprofile.reactive.messaging.Incoming
import java.util.concurrent.CompletionStage

@ApplicationScoped
class MessageReceiver {

    @Incoming("rlapi")
    fun receiveUpdateState(msg: MqttMessage<ByteArray>): CompletionStage<Void?> {
        println("Received on ${msg.topic}: ${msg.payload.decodeToString()}")
        return msg.ack()
    }
}