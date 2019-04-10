package by.bogdan.bsuir.bsuirgraduationbackend.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
class GlobalConfiguration(@Value("\${services.web.url}") val webUrl: String) : WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedOrigins(webUrl)
                .allowedMethods("*")
                .maxAge(3600)
    }
}