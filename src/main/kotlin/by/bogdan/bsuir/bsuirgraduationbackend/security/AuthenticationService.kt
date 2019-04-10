package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.UserDocument
import by.bogdan.bsuir.bsuirgraduationbackend.exceptions.AuthenticationException
import by.bogdan.bsuir.bsuirgraduationbackend.repository.UserRepository
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.util.*

@Service
class AuthenticationService(private val userRepository: UserRepository,
                            private val passwordEncoder: BCryptPasswordEncoder,
                            @Value("\${jwt.secret}") secretKey: String) {
    private val secretEncoded = Base64.getUrlEncoder().encode(secretKey.toByteArray())

    private fun passwordsMatch(rawPassword: String, encodedPassword: String) =
            passwordEncoder.matches(rawPassword, encodedPassword)

    fun encode(rawPassword: String) = passwordEncoder.encode(rawPassword)!!

    fun authenticate(username: String, password: String): Mono<String> {
        return userRepository.findByUsername(username).map { user: UserDocument ->
            val encodedPassword = user.password
            if (passwordsMatch(password, encodedPassword)) {
                Jwts.builder().setSubject(username).signWith(SignatureAlgorithm.HS256, secretEncoded).compact()
            } else throw AuthenticationException("Wrong credentials", username, password)
        }
    }
}