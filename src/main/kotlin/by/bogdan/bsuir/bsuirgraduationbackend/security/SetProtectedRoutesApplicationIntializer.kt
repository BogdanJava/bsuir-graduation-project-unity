package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.Application
import org.reflections.Reflections
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import kotlin.reflect.full.findAnnotation

class SetProtectedRoutesApplicationInitializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
    companion object {
        var protectedResources: MutableSet<String> = HashSet()
    }

    override fun initialize(context: ConfigurableApplicationContext) {
        val rootPackage = Application::class.java.`package`!!
        val reflections = Reflections(rootPackage.name)
        reflections.getTypesAnnotatedWith(RestController::class.java).forEach { controllerType ->
            val mappingAnnotation = controllerType.getAnnotation(RequestMapping::class.java)
            val paths = mappingAnnotation.value
            if (controllerType.isAnnotationPresent(ProtectedResource::class.java)) {
                controllerType.declaredMethods
                        .filter { m -> Modifier.isPublic(m.modifiers) }
                        .forEach { m ->
                            if (!m.isAnnotationPresent(PublicResource::class.java)) {
                                val mappingsForMethod = getMappingsForMethod(m, paths)
                                protectedResources.addAll(mappingsForMethod)
                            }
                        }
            } else {
                controllerType.methods
                        .filter { m -> Modifier.isPublic(m.modifiers) }
                        .forEach { m ->
                            if (m.isAnnotationPresent(ProtectedResource::class.java)) {
                                val mappingsForMethod = getMappingsForMethod(m, paths)
                                protectedResources.addAll(mappingsForMethod)
                            }
                        }
            }
        }
        protectedResources = protectedResources.map { path ->
            path.replace("\\^(\\{.+\\})", "*")
        }.toMutableSet()
        println(protectedResources)
        throw RuntimeException()
    }

    private fun getMappingsForMethod(method: Method, controllerPaths: Array<String>): List<String> {
        val paths: MutableList<String> = ArrayList()
        controllerPaths.forEach { path ->
            val methodMappingAnnotation = getMappingAnnotation(method)
            if (methodMappingAnnotation != null) {
                val mappingPaths = getMappingAnnotationPaths(methodMappingAnnotation)
                if (mappingPaths.isNotEmpty()) {
                    mappingPaths.forEach { mappingPath ->
                        paths.add("$path$mappingPath")
                    }
                } else {
                    paths.add(path)
                }
            }
        }
        return paths
    }

    private fun getMappingAnnotation(method: Method) =
            method.declaredAnnotations.filter { annotation ->
                annotation.annotationClass.findAnnotation<RequestMapping>() != null
            }.getOrNull(0)

    @SuppressWarnings("unchecked")
    private fun getMappingAnnotationPaths(annotation: Annotation): List<*> =
            (annotation.javaClass.getDeclaredMethod("value").invoke(annotation) as Array<*>).asList()

}