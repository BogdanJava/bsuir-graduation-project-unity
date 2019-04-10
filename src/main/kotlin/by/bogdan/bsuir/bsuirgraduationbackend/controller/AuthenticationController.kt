package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(val authService: AuthenticationService) {

    @PostMapping("/token")
    fun generateToken(@RequestBody body: Map<String, String>): Mono<String> {
        val username = body["username"]!!
        val password = body["password"]!!
        return authService.authenticate(username, password)
    }
}