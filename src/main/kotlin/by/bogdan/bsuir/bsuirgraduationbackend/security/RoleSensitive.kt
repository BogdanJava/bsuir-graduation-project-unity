package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role

/**
 * @see RoleAccessAspect
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RoleSensitive(vararg val requiredRoles: Role)