package edu.kit.ifv

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class StateCalled(val name: String, vararg val scopes: KClass<*> = [Any::class])
