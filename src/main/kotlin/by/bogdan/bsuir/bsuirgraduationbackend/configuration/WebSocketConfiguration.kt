package by.bogdan.bsuir.bsuirgraduationbackend.configuration

import by.bogdan.bsuir.bsuirgraduationbackend.websocket.CustomWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered.HIGHEST_PRECEDENCE
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Configuration
class WebSocketConfiguration {

    @Bean
    fun handlerMapping(handlers: List<CustomWebSocketHandler<*>>): HandlerMapping {
        val handlerMap = HashMap<String, WebSocketHandler>()
        handlers.forEach { handler -> handlerMap.put(handler.getMapping(), handler) }
        val mapping = SimpleUrlHandlerMapping()
        mapping.urlMap = handlerMap
        mapping.order = HIGHEST_PRECEDENCE
        return mapping
    }

    @Bean
    fun handlerAdapter(webSocketService: WebSocketService): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter(webSocketService)
    }

    @Bean
    fun webSocketService(): WebSocketService {
        return HandshakeWebSocketService(ReactorNettyRequestUpgradeStrategy())
    }

    @Bean
    fun executor(): Executor = Executors.newSingleThreadExecutor()
}