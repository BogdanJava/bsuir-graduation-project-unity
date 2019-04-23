package by.bogdan.bsuir.bsuirgraduationbackend.security

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role

/**
 * @see RoleAccessAspect
 * Note: if function is annotated with @RoleSensitive, the second parameter must be
 * an authorization header (bearer) of type String
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RoleSensitive(vararg val requiredRoles: Role)