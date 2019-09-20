package by.bogdan.bsuir.bsuirgraduationbackend.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ServerWebExchange

@RestControllerAdvice(basePackages = ["by.bogdan"])
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable::class)
    fun handleOther(ex: Throwable, serverWebExchange: ServerWebExchange): Map<*, *> {
        log.error(ex.message, ex)
        log.error("request: ${serverWebExchange.request}")
        return mapOf(
                Pair("message", ex.message),
                Pair("request", serverWebExchange.request))
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: ResourceNotFoundException) = message(ex)

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadPayloadException::class)
    fun handleBadPayloadException(ex: BadPayloadException) = message(ex)

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
                                   val password: String) : CustomException(msg) {
    override fun getResponseStatus() = HttpStatus.UNAUTHORIZED

    constructor(ex: Throwable) : this(ex.message!!, "", "")
    constructor(message: String) : this(message, "", "")
}

data class ResourceNotFoundException(val msg: String) : CustomException(msg) {
    override fun getResponseStatus() = HttpStatus.NOT_FOUND
}

data class BadPayloadException(val msg: String) : CustomException(msg) {
    override fun getResponseStatus(): HttpStatus = HttpStatus.BAD_REQUEST
}

abstract class CustomException(message: String) : RuntimeException(message) {
    abstract fun getResponseStatus(): HttpStatus
}
