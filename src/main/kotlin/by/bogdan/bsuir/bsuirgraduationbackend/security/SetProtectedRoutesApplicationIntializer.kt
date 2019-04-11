package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.Application
import org.reflections.Reflections
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Modifier

class SetProtectedRoutesApplicationInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(context: ConfigurableApplicationContext) {
        val rootPackage = Application::class.java.`package`!!
        val reflections = Reflections(rootPackage.name)
        reflections.getTypesAnnotatedWith(RestController::class.java).forEach { controllerType ->
            val mappingAnnotation = controllerType.getAnnotation(RequestMapping::class.java)
            val paths = mappingAnnotation.path
            if (controllerType.isAnnotationPresent(ProtectedResource::class.java)) {
                controllerType.methods
                        .filter { m -> Modifier.isPublic(m.modifiers) }
                        .forEach { m ->
                            if (!m.isAnnotationPresent(PublicResource::class.java)) {
                                TODO()
                            }
                        }
            } else {
                controllerType.methods
                        .filter { m -> Modifier.isPublic(m.modifiers) }
                        .forEach { m ->
                            if (m.isAnnotationPresent(ProtectedResource::class.java)) {
                                TODO()
                            }
                        }
            }
        }
    }
}