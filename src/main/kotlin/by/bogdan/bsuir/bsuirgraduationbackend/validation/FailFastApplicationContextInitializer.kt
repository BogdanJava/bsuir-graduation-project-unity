package by.bogdan.bsuir.bsuirgraduationbackend.validation

import by.bogdan.bsuir.bsuirgraduationbackend.log
import lombok.extern.slf4j.Slf4j
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext

@Slf4j
class FailFastApplicationContextInitializer :
        ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        log.warn("Not implemented yet")
    }
}