package by.bogdan.bsuir.bsuirgraduationbackend.websocket

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEvent
import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.TimelineEventType
import by.bogdan.bsuir.bsuirgraduationbackend.events.NotificationEvent
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.utils.CustomReflectionUtils
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.CloseStatus
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Mono
import java.util.*
import kotlin.collections.HashMap

@Component
class NotificationsWebSocketHandler(val objectMapper: ObjectMapper,
                                    val authenticationService: AuthenticationService,
                                    val reflectionUtils: CustomReflectionUtils) : CustomWebSocketHandler<NotificationEvent>() {
    override fun getMapping() = "/notifications"

    override fun handle(session: WebSocketSession): Mono<Void> {
        try {
            if (!authenticationService.isTokenValid(parseParams(session)["accessToken"])) {
                return session.close(CloseStatus.BAD_DATA)
            }
            return session.send(Flux.create { sink: FluxSink<NotificationEvent> ->
                publish.subscribe { event ->
                    if (toSend(event, session)) {
                        sink.next(event)
                    }
                }
            }.share().map { event ->
                val json = objectMapper.writeValueAsString(event)
                session.textMessage(json)
            })
        } catch (ex: Throwable) {
            log.error(ex.message, ex)
            return session.send(Mono.just(session.textMessage(ex.message ?: "Internal server error")))
        }
    }

    private fun toSend(event: NotificationEvent, session: WebSocketSession): Boolean {
        val payload: TimelineEvent = event.payload
        val currentUserId = UUID.fromString(getUserIdFromSession(session))
        return when (payload.className) {
            "TimeRequest", "WorktimeRequest" -> {
                val userId = reflectionUtils.readField("userId", payload.obj) as UUID
                val approverId = reflectionUtils.readField("approverId", payload.obj) as UUID
                if (payload.type == TimelineEventType.APPROVE || payload.type == TimelineEventType.DECLINE) {
                    currentUserId == userId
                } else if (payload.type == TimelineEventType.CREATE) {
                    currentUserId == approverId
                } else false
            }
            else -> {
                val idField = reflectionUtils.findIdField(payload.obj)!!
                val idValue = reflectionUtils.readField(idField, payload.obj)
                idValue == currentUserId
            }
        }
    }

    private fun getUserIdFromSession(session: WebSocketSession): String {
        val accessToken = parseParams(session)["accessToken"]!!
        return authenticationService.getClaimsFromToken(accessToken)["id"] as String
    }

    private fun parseParams(session: WebSocketSession): Map<String, String> {
        val uri = session.handshakeInfo.uri
        val query = uri.query
        val map = HashMap<String, String>()
        query.split('&').forEach { keyValueString ->
            val chunks = keyValueString.split('=')
            map.put(chunks[0], chunks[1])
        }
        return map
    }

    companion object {
        val log = LoggerFactory.getLogger(NotificationsWebSocketHandler::class.java)!!
    }
}