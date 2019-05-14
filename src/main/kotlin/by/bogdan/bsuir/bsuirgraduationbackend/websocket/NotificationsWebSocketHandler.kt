package by.bogdan.bsuir.bsuirgraduationbackend.websocket

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEventType
import by.bogdan.bsuir.bsuirgraduationbackend.events.NotificationEvent
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class NotificationsWebSocketHandler : CustomWebSocketHandler<NotificationEvent>() {
    override fun noEvent(): NotificationEvent {
        val timelineEvent = TimelineEvent()
        timelineEvent.description = "No event"
        timelineEvent.type = TimelineEventType.HANDSHAKE
        timelineEvent.className = TimelineEvent::class.java.name
        return NotificationEvent(timelineEvent)
    }

    override fun getMapping() = "/notifications"

    override fun handle(session: WebSocketSession): Mono<Void> {
        return session.send(getPublish().map { event -> event.source }
                .map { src -> session.textMessage((src as TimelineEvent).toString()) })
    }
}