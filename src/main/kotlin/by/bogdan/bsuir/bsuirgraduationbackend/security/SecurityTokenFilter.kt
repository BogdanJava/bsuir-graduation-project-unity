package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*
import javax.annotation.PostConstruct

@Component
class SecurityTokenFilter(val authenticationService: AuthenticationService,
                          val userService: UserService) : WebFilter {
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
                    executeInUserContext(webFilterChain.filter(serverWebExchange), bearer)
                } else throw AuthenticationException(msg = "Token is expired or invalid", username = "", password = "")
            } else {
                throw AuthenticationException(msg = "Token is missing", username = "", password = "")
            }
        } else {
            webFilterChain.filter(serverWebExchange)
        }
    }

    private fun executeInUserContext(filterMono: Mono<Void>, bearer: String): Mono<Void> {
        val claims = authenticationService.getClaimsFromAuthorizationHeader(bearer)
        return filterMono.subscriberContext { ctx -> ctx.put("userId", UUID.fromString(claims["id"] as String)) }
    }

    private fun isProtectedUrl(request: HttpRequest) =
            this.protectedResources.contains(request.uri.path)
                    && request.method != HttpMethod.OPTIONS // cors handshake

    companion object {
        val log = LoggerFactory.getLogger(SecurityTokenFilter::class.java)!!
    }
}