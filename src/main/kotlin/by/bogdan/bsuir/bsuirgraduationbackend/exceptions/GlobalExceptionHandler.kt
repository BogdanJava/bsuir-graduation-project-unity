package by.bogdan.bsuir.bsuirgraduationbackend.exceptions

import by.bogdan.bsuir.bsuirgraduationbackend.Application
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice(basePackageClasses = [(Application::class)])
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException) = message(ex)

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException::class)
    fun handleUnauthorized(ex: AuthenticationException): Map<String, String?> {
        log.info("Wrong credentials: [${ex.username}, ${ex.password}]")
        return message(ex)
    }

    fun message(ex: Throwable) = mapOf(Pair("message", ex.message))

    companion object {
        val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)!!
    }
}

// System exceptions

data class AuthenticationException(val msg: String,
                                   val username: String,
                                   val password: String) : RuntimeException(msg) {
    constructor(ex: Throwable) : this(ex.message!!, "", "")
    constructor(message: String) : this(message, "", "")
}

data class ResourceNotFoundException(val msg: String) : RuntimeException(msg)
