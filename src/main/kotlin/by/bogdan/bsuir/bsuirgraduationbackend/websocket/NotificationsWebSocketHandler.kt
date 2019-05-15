package by.bogdan.bsuir.bsuirgraduationbackend.websocket

import by.bogdan.bsuir.bsuirgraduationbackend.events.NotificationEvent
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono

@Component
class NotificationsWebSocketHandler(val objectMapper: ObjectMapper,
                                    val authenticationService: AuthenticationService) : CustomWebSocketHandler<NotificationEvent>() {
    override fun getMapping() = "/notifications"
    private val sessionIdParamsMap = HashMap<String, Map<String, String>>()

    override fun handle(session: WebSocketSession): Mono<Void> {
        if (!sessionIdParamsMap.containsKey(session.id)) {
            sessionIdParamsMap[session.id] = parseParams(session)
        }

        return session.send(publish.map { event -> objectMapper.writeValueAsString(event) }
                .map { json ->
                    val accessToken = sessionIdParamsMap[session.id]!!["accessToken"]
                    if (authenticationService.isTokenValid(accessToken)) {
                        // todo send message rules based on class name, type and pther params
                        session.textMessage(json as String)
                    } else {
                        throw AuthenticationException("Token is invalid")
                    }
                })
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
}