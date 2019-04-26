package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.CustomException
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.HttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*
import java.util.regex.Pattern
import javax.annotation.PostConstruct
import kotlin.collections.HashMap

@Component
class SecurityTokenFilter(val authenticationService: AuthenticationService,
                          val objectMapper: ObjectMapper) : WebFilter {
    private lateinit var protectedResources: MutableSet<String>
    private var resourcePatternMap: Map<String, Pattern> = HashMap()

    @PostConstruct
    fun init() {
        this.protectedResources = SetProtectedRoutesApplicationInitializer.protectedResources
        this.protectedResources.forEach { r ->
            this.resourcePatternMap += Pair(r, Pattern.compile(r))
        }
    }

    override fun filter(serverWebExchange: ServerWebExchange, webFilterChain: WebFilterChain): Mono<Void> {
        try {
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
        } catch (ex: CustomException) {
            serverWebExchange.response.setStatusCode(ex.getResponseStatus())
            val responseContent = objectMapper.writeValueAsBytes(Pair("message", ex.message))
            val buffer = serverWebExchange.response.bufferFactory().wrap(responseContent)
            return serverWebExchange.response.writeWith(Mono.just(buffer))
        }
    }

    private fun executeInUserContext(filterMono: Mono<Void>, bearer: String): Mono<Void> {
        val claims = authenticationService.getClaimsFromAuthorizationHeader(bearer)
        return filterMono.subscriberContext { ctx -> ctx.put("userId", UUID.fromString(claims["id"] as String)) }
    }

    private fun isProtectedUrl(request: HttpRequest) =
            (this.protectedResources.contains(request.uri.path) || this.matchesPattern(request.uri.path))
                    && request.method != HttpMethod.OPTIONS // cors handshake

    private fun matchesPattern(path: String): Boolean {
        for (resourcePatternPair in this.resourcePatternMap) {
            if (resourcePatternPair.value.matcher(path).matches()) {
                return true;
            }
        }
        return false
    }

    companion object {
        val log = LoggerFactory.getLogger(SecurityTokenFilter::class.java)!!
    }
}