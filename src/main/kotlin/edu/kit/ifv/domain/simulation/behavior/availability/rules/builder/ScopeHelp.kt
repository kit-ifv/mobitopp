package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

private fun <T> T.help(type: KClass<*>): T {
    println("Scope: ${type.simpleName}")

    println("\nProperties:")
    type.declaredMemberProperties
        .filterNot { it.name == "help" }
        .forEach { property ->
            println("- ${property.name}: ${property.returnType}")
        }

    println("\nFunctions:")
    type.declaredMemberFunctions
        .filterNot { it.name in setOf("help", "equals", "hashCode", "toString") }
        .forEach { function ->
            val parameters = function.parameters
                .drop(1) // Skip the instance receiver
                .joinToString { parameter ->
                    "${parameter.name}: ${parameter.type}"
                }

            println("- ${function.name}($parameters): ${function.returnType}")
        }

    return this
}

fun StaticAvailabilityRuleBuilder.help() = help(StaticRuleScope::class)
fun ProviderAvailabilityRuleBuilder.help() = help(ProviderRuleScope::class)
fun ResourceAvailabilityRuleBuilder.help() = help(ResourceRuleScope::class)