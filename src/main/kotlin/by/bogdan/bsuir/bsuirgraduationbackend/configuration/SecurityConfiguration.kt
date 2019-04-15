package by.bogdan.bsuir.bsuirgraduationbackend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class SecurityConfiguration {
    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()
}