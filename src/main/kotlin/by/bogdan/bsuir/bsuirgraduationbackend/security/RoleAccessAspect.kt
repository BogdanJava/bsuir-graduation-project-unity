package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.service.UserService
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.*

@Aspect
@Component
class RoleAccessAspect(private val authenticationService: AuthenticationService,
                       private val userService: UserService) {

    @Around("@annotation(by.bogdan.bsuir.bsuirgraduationbackend.security.RoleSensitive)")
    fun checkUserPrivileges(joinPoint: ProceedingJoinPoint): Mono<Any> {
        val method = (joinPoint.signature as MethodSignature).method
        val roleSensitive = method.getDeclaredAnnotation(RoleSensitive::class.java)
        val bearer = joinPoint.args[1] as String
        val claims = authenticationService.getClaimsFromAuthorizationHeader(bearer)
        return userService.findById(UUID.fromString(claims["id"] as String)).flatMap { currentUser ->
            roleSensitive.requiredRoles.forEach { role ->
                if (!currentUser.roles.contains(role)) {
                    val message = "User does not have enough privileges to do this action: " +
                            "action: ${method.name}"
                    log.warn(message);
                    throw AuthenticationException(message,
                            username = currentUser.username, password = "")
                }
            }
            joinPoint.proceed() as Mono<*>
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(RoleAccessAspect::class.java)!!
    }
}