package edu.kit.ifv.core

import edu.kit.ifv.mobitopp.discretechoice.structure.EnumeratedStructureBuilder
import edu.kit.ifv.mobitopp.discretechoice.structure.RuleBasedStructureBuilder

fun <A, C, P> RuleBasedStructureBuilder<A, C, P>.ruleForAllOf(utilityFunction: context(A, C) P.() -> Double) {
    ruleForAll { a, c ->
        context(a, c) { utilityFunction() }
    }
}

fun <A, C, P> RuleBasedStructureBuilder<A, C, P>.ruleOf(
    rule: (A) -> Boolean,
    utilityFunction: context(A, C) P.() -> Double,
) {
    rule(rule) { a, c ->
        context(a, c) { utilityFunction() }
    }
}

fun <A, C, P> EnumeratedStructureBuilder<A, C, P>.optionOf(option: A, utilityFunction: context(A, C) P.() -> Double) {
    option(option) { a, c ->
        context(a, c) { utilityFunction() }
    }
}
