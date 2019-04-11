package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.net.URI

@Component
class SecurityTokenFilter(val authenticationService: AuthenticationService) : WebFilter {
    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
        if (isProtectedUrl(serverWebExchange.request.uri)) {
            val bearer: String? = serverWebExchange.request.headers.getFirst("Authorization")
            if (bearer != null) {
                val token = bearer.substring(7)
                if (authenticationService.isTokenValid(token)) {
                    return webFilterChain.filter(serverWebExchange)
                } else throw AuthenticationException(msg = "Token is expired or invalid", username = "", password = "")
            } else {
                throw AuthenticationException(msg = "Token is missing", username = "", password = "")
            }
        } else {
            return webFilterChain.filter(serverWebExchange)
        }
    }

    private fun isProtectedUrl(uri: URI): Boolean {
        return false;
    }
}