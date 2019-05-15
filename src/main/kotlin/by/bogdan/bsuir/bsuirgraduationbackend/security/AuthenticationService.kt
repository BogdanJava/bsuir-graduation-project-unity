package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

@Service
class AuthenticationService(private val userRepository: UserRepository,
                            private val passwordEncoder: BCryptPasswordEncoder,
                            @Value("\${jwt.secret}") secretKey: String) {
    private val secretEncoded = Base64.getUrlEncoder().encode(secretKey.toByteArray())
    private val parser = Jwts.parser().setSigningKey(secretEncoded)

    private fun passwordsMatch(rawPassword: String, encodedPassword: String) =
            passwordEncoder.matches(rawPassword, encodedPassword)

    fun encode(rawPassword: String) = passwordEncoder.encode(rawPassword)!!

    fun checkPasswordForUser(userId: UUID, password: String): Mono<Boolean> {
        return userRepository.findById(userId).map { user: UserDocument ->
            val encodedPassword = user.password
            passwordsMatch(password, encodedPassword)
        }
    }

    fun getClaimsFromAuthorizationHeader(authorization: String): Claims {
        val token = authorization.substring(7)
        if (parser.isSigned(token)) {
            return parser.parseClaimsJws(token).body
        } else {
            throw AuthenticationException("Invalid token", "", "")
        }
    }

    fun authenticate(username: String, password: String): Mono<AuthToken> {
        return userRepository.findByUsername(username).map { user: UserDocument ->
            val encodedPassword = user.password
            if (passwordsMatch(password, encodedPassword)) {
                val expirationSeconds = LocalDateTime.now().plusHours(12).toEpochSecond(ZoneOffset.UTC)
                val accessToken = Jwts.builder()
                        .setSubject(username)
                        .setId(UUID.randomUUID().toString())
                        .setExpiration(Date(expirationSeconds * 1000)) // milliseconds
                        .claim("id", user.id)
                        .signWith(SignatureAlgorithm.HS256, secretEncoded)
                        .compact()
                AuthToken(accessToken, expirationSeconds)
            } else throw AuthenticationException("Wrong credentials", username, password)
        }
    }

    fun isTokenValid(rawToken: String?): Boolean {
        return try {
            if (rawToken == null) {
                throw RuntimeException("Token is null")
            }
            if (parser.isSigned(rawToken)) {
                val body = parser.parseClaimsJws(rawToken).body
                body.expiration.after(Date(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC) * 1000))
            } else false
        } catch (ex: Throwable) {
            log.error(ex.message, ex)
            false
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String, token: String): Mono<Boolean> {
        return try {
            if (parser.isSigned(token)) {
                val body = parser.parseClaimsJws(token).body
                val id = UUID.fromString(body["id", String::class.java])
                userRepository.findById(id).flatMap { user ->
                    if (passwordsMatch(oldPassword, user.password)) {
                        user.password = encode(newPassword)
                        userRepository.save(user).map { _ -> true }
                    } else {
                        throw AuthenticationException("Passwords don't match", user.username, oldPassword)
                    }
                }
            } else {
                throw AuthenticationException("Token is unsigned", "", "")
            }
        } catch (ex: Throwable) {
            log.error(ex.message, ex)
            throw AuthenticationException(ex)
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(AuthenticationService::class.java)!!
    }
}

data class AuthToken(val accessToken: String,
                     val expiresAt: Long)