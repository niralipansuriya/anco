package com.app.ancoturf.di.scope

import javax.inject.Scope

/**
 * The ServiceScoped custom scoping annotation specifies that the lifespan of a dependency be
 * the same as that of a Service. This is used to annotate dependencies that behave like a
 * singleton within the lifespan of a Service.
 */
@Scope
@Retention
@Target(AnnotationTarget.TYPE, AnnotationTarget.FUNCTION)
annotation class ServiceScoped