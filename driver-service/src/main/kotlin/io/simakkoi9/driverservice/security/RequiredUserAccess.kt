package io.simakkoi9.driverservice.security

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class RequiredUserAccess(
    val accessType: AccessType = AccessType.USER
) 