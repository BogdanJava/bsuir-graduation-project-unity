package by.bogdan.bsuir.bsuirgraduationbackend.websocket

import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.web.reactive.socket.WebSocketHandler
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.function.Consumer

abstract class CustomWebSocketHandler<T : ApplicationEvent> :
        WebSocketHandler,
        ApplicationListener<T>,
        Consumer<FluxSink<T>> {
    private val eventsQueue: BlockingQueue<T> = LinkedBlockingQueue<T>();
    private val executor = Executors.newCachedThreadPool()

    override fun accept(sink: FluxSink<T>) {
        executor.execute {
            while (true) {
                val echoEvent = eventsQueue.poll(2, TimeUnit.SECONDS)
                sink.next(if (echoEvent != null) echoEvent else noEvent())
            }
        }
    }

    protected fun getPublish() = Flux.create(this).share()

    protected abstract fun noEvent(): T

    override fun onApplicationEvent(event: T) {
        eventsQueue.offer(event)
    }

    abstract fun getMapping(): String
}