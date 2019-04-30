package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@ProtectedResource
@RestController
@RequestMapping("/api/messages")
class MessageController {

    @GetMapping("/count")
    fun undeadMessagesCount(@RequestParam username: String,
                            @RequestParam read: Boolean): Mono<Int> {
        return Mono.just(0)
    }
}