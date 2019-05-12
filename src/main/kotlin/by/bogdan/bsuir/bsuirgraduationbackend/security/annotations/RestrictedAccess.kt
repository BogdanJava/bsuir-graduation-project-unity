package by.bogdan.bsuir.bsuirgraduationbackend.security.annotations

import by.bogdan.bsuir.bsuirgraduationbackend.datamodel.Role

/**
 * @see RoleAccessAspect
 * Note: if function is annotated with @RestrictedAccess, the first parameter must be
 * an authorization header (bearer) of type String
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class RestrictedAccess(vararg val requiredRoles: Role)