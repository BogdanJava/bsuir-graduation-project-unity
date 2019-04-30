package by.bogdan.bsuir.bsuirgraduationbackend.controller

import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthToken
import by.bogdan.bsuir.bsuirgraduationbackend.security.AuthenticationService
import by.bogdan.bsuir.bsuirgraduationbackend.security.annotations.ProtectedResource
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.util.*

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(val authService: AuthenticationService) {

    @PostMapping("/token")
    fun generateToken(@RequestBody body: Map<String, String>): Mono<AuthToken> {
        val username = body["username"]!!
        val password = body["password"]!!
        return authService.authenticate(username, password)
    }

    @ProtectedResource
    @PostMapping("/check-password")
    fun checkPassword(@RequestBody body: Map<String, String>,
                      @RequestHeader("Authorization") authHeader: String): Mono<Boolean> {
        val password = body["password"]!!
        val claims = authService.getClaimsFromAuthorizationHeader(authHeader)
        return authService.checkPasswordForUser(UUID.fromString(claims["id", String::class.java]), password)

    }

    @ProtectedResource
    @PostMapping("/update-password")
    fun updatePassword(@RequestBody body: Map<String, String>,
                       @RequestHeader("Authorization") authHeader: String): Mono<Boolean> {
        val oldPassword = body["old"]!!
        val newPassword = body["new"]!!
        val token = authHeader.substring(7)
        return authService.updatePassword(oldPassword, newPassword, token)
    }
}