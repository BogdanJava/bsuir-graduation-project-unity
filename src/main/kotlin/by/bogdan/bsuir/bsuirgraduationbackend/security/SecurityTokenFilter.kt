package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import javax.annotation.PostConstruct

@Component
class SecurityTokenFilter(val authenticationService: AuthenticationService) : WebFilter {
    private lateinit var protectedResources: MutableSet<String>

    @PostConstruct
    fun init() {
        this.protectedResources = SetProtectedRoutesApplicationInitializer.protectedResources
    }

    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
        return if (isProtectedUrl(serverWebExchange.request)) {
            val bearer: String? = serverWebExchange.request.headers.getFirst("Authorization")
            if (bearer != null) {
                val token = bearer.substring(7)
                if (authenticationService.isTokenValid(token)) {
                    webFilterChain.filter(serverWebExchange)
                } else throw AuthenticationException(msg = "Token is expired or invalid", username = "", password = "")
            } else {
                throw AuthenticationException(msg = "Token is missing", username = "", password = "")
            }
        } else {
            webFilterChain.filter(serverWebExchange)
        }
    }

    private fun isProtectedUrl(request: HttpRequest) =
            this.protectedResources.contains(request.uri.path)
                    && request.method != HttpMethod.OPTIONS
}