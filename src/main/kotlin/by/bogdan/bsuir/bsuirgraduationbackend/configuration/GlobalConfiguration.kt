package by.bogdan.bsuir.bsuirgraduationbackend.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import java.text.DateFormat
import java.text.SimpleDateFormat


@Configuration
class GlobalConfiguration(@Value("\${services.web.url}") val webUrl: String) :
        WebFluxConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
                .allowedOrigins(webUrl)
                .allowedMethods("*")
                .maxAge(3600)
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper().apply {
            registerModule(KotlinModule())
        }
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        return mapper
    }

    @Qualifier("isoDateFormat")
    @Bean
    fun isoDateFormat(): DateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    }

}