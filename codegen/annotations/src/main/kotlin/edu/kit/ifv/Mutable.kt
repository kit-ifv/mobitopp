package edu.kit.ifv

@Target(AnnotationTarget.CLASS)
annotation class Buildable(
    val defaults: String = ""
)
