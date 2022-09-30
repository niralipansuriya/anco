package com.app.ancoturf.di.scope

import javax.inject.Scope

/**
 * By default, if no scope annotation is present, the injector creates an instance , uses the
 * instance for one injection, and then forgets it. If a scope annotation is present, the
 * injector may retain the instance for possible reuse in a later injection.
 * (from: https://docs.oracle.com/javaee/6/api/javax/inject/Scope.html)
 * <p>
 * Scopes TL;DR:
 * No scope = new instance created every time
 * [@Singleton] = only one instance
 * [@CustomScope] = instance reused depending on the component’s lifecycle
 * <p>
 * In Dagger, an unscoped component cannot depend on a scoped component. As
 * AppComponent is a scoped component ({@code @Singleton}, we create a custom
 * scope to be used by all fragment components. Additionally, a component with a specific scope
 * cannot have a sub component with the same scope.
 * <p>
 * The ActivityScoped scoping annotation specifies that the lifespan of a dependency be the same
 * as that of an Activity. This is used to annotate dependencies that behave like a singleton
 * within the lifespan of an Activity.
 * <p>
 * [@Singleton] is used to specify that the lifespan of a dependency be the same as that of the
 * Application.
 */
@MustBeDocumented
@Scope
@Retention
annotation class ActivityScoped