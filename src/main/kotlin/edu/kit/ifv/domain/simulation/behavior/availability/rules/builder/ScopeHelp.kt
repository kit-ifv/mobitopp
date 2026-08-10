package edu.kit.ifv.domain.simulation.behavior.availability.rules.builder

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

/**
 * Prints the available properties and functions of a scope to the console.
 *
 * This function uses reflection to inspect the given [type] and print its
 * declared properties and functions (excluding common ones like `toString`).
 * It is used to provide runtime help for the availability rules DSL.
 *
 * @param type the class to inspect
 * @return this builder instance for chaining
 */
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

/**
 * Prints the available properties and functions of the [StaticRuleScope] to the console.
 * This can be used during DSL configuration to discover available fields like `person` or `mode`.
 */
fun StaticAvailabilityRuleBuilder.help() = help(StaticRuleScope::class)

/**
 * Prints the available properties and functions of the [ProviderRuleScope] to the console.
 * This can be used during DSL configuration to discover available helper functions
 * like `checkOperatingHours`, `stations`, or `homeBasedVehicleRule`.
 */
fun ProviderAvailabilityRuleBuilder.help() = help(ProviderRuleScope::class)

/**
 * Prints the available properties and functions of the [ResourceRuleScope] to the console.
 * This can be used during DSL configuration to discover available resource selection functions
 * like `selectStationByMinDistance` or `selectRideOfferByMinDuration`.
 */
fun ResourceAvailabilityRuleBuilder.help() = help(ResourceRuleScope::class)
