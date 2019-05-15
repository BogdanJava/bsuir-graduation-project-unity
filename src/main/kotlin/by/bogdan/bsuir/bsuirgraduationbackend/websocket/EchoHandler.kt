package by.bogdan.bsuir.bsuirgraduationbackend.websocket

import by.bogdan.bsuir.bsuirgraduationbackend.events.EchoEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
final class EchoHandler : CustomWebSocketHandler<EchoEvent>() {
    override fun getMapping() = "/echo"

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(publish.map { event -> event.source }
                .map { src -> session.textMessage(src.toString()) })
    }
}
