package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.Application
import org.reflections.Reflections
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.regex.Pattern
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
            val paths = if (mappingAnnotation != null) mappingAnnotation.value else arrayOf("")
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
            val pattern = Pattern.compile("\\{[a-z]+}")
            val matcher = pattern.matcher(path)
            matcher.replaceAll("*")
        }.toMutableSet()
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

    private fun getMappingAnnotationPaths(annotation: Annotation) =
            (annotation.javaClass.getDeclaredMethod("value")
                    .invoke(annotation) as Array<*>).asList()

}