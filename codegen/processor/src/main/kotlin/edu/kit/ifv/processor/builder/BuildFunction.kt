package edu.kit.ifv.processor.builder

import edu.kit.ifv.Buildable
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.symbol.KSClassDeclaration
import edu.kit.ifv.utils.Parameter
import edu.kit.ifv.utils.TextBuilder
import edu.kit.ifv.utils.allProperties
import edu.kit.ifv.utils.builderNameWithResolvedGenerics
import edu.kit.ifv.utils.builderWithGenerics
import edu.kit.ifv.utils.defaultableParameters
import edu.kit.ifv.utils.invoke
import edu.kit.ifv.utils.mimic
import edu.kit.ifv.utils.name
import edu.kit.ifv.utils.nameWithGenerics
import edu.kit.ifv.utils.nonDefaultableParameters
import edu.kit.ifv.utils.parameters
import edu.kit.ifv.utils.resolvedGenerics
import edu.kit.ifv.utils.simpleGenerics

/**
 * This file contains the code to generate a builder class for an [Buildable]-Annotated target class. There may be
 * unavoidable ambiguity in the documentation, as both the target code and meta code generation have significant content
 * overlap (Read: A meta builder software building classes that satisfy the builder pattern for target classes)
 */

/**
 * The standard BuildFunction required to generate a builder from a target [KSClassDeclaration].
 */
abstract class BuildFunction(val classDeclaration: KSClassDeclaration) {
    /**
     * A list of [Parameter] which contain a default parameter. Example:
     * Buildable
     * class X(val a: Int = 0)
     * "a" would be a defaultable Parameter
     *
     */
    open val defaultableParameters = classDeclaration.defaultableParameters()

    /**
     * A list of [Parameter] which do not have a default parameter. Example:
     * Buildable
     * class X(val b: List<String>)
     * "b" would be a non defaultable Parameter
     *
     */
    open val nonDefaultableParameters = classDeclaration.nonDefaultableParameters()

    /**
     * The union of all parameters defaultable and non-defaultable
     */
    open val allParameters = classDeclaration.parameters()

    /**
     * All parameters that need to be specified in a constructor call, either because they are required "nondefaultable"
     * or an overwritten default parameter
     */
    val specifiedParameters get() = overwrittenDefaultables + nonDefaultableParameters

    val overwrittenDefaultables get() = defaultableParameters.filter { it.hasExternalDefault }
    val requiredDefaultables get() = defaultableParameters.filter { !it.hasExternalDefault }

    /**
     * The signature of the class Declaration of the Builder.
     */
    open fun classDeclaration(): String =
        "class ${classDeclaration.builderNameWithResolvedGenerics}() : Builder<${classDeclaration.nameWithGenerics}>"

    /**
     * Additional builder extension on the class since Jelle had something like this in his code, and I will not touch
     * his code. I think this extension is rather useless, as the Builder<X> interface provides nothing of convenience
     */
    fun asWeakBuilderExtension(): String {
        val floatingParameters = allParameters.filter {it.isFreeFloating}.map{it.nameWithOverrideableType(false)}
        val floatingParameterNames = allParameters.filter { it.isFreeFloating }.map {it.name}
        return "\nfun${classDeclaration.resolvedGenerics} ${classDeclaration.nameWithGenerics}.weakBuilder(${floatingParameters.joinToString(", ") { it }}):" +
                " Builder<${classDeclaration.nameWithGenerics}> = this.asBuilder(${floatingParameterNames.joinToString(", ") { it }})\n\n"

    }

    /**
     * Convenience asBuilder call, actually returning the builder instance, so the attributes can be changed.
     */
    fun asBuilderExtension(): String {
        val floatingParameters = allParameters.filter {it.isFreeFloating}.map{it.nameWithOverrideableType(false)}
        return "\nfun${classDeclaration.resolvedGenerics} ${classDeclaration.nameWithGenerics}.asBuilder(${floatingParameters.joinToString(", ") { it }}) " +
                "= ${classDeclaration.builderWithGenerics}().also"{
                    +allParameters.joinToString(separator = "\n") { "it.${it.name} = ${it.name + it.state.reverseInstantiate()}" }
                } +"\n\n"


    }

    /**
     * A function to create a text block generating all parameters that will be present in the Builder class. Should
     * output valid Kotlin code. Note that the parameters of the builder can be slightly different from the target parameter
     * Main differences include nullability or mutability. A detailed explanation can be found in [Parameter].
     * Example:
     * """ XBuilder: Builder<X> {
     *         var a: Int? = null   //Note that "a" of X originally is of type "Int" and "val".
     *         val b: MutableList<String> = mutableListOf()
     *    }"""
     */

    open fun builderParameters(): String = allParameters.joinToString(separator = "\n") { it.toAttribute() }

    /**
     * A function to create a text block representing the call to instantiate the target Class. Should output valid
     * Kotlin code.
     * Example:
     * """override fun build(): X{
     *        return X(a, b)
     *    }"""
     */
    abstract fun createBuildFunction(): String

    /**
     * A function to create a text block reseting the builder to the initial configuration. Should output valid Kotlin
     * code.
     * """override fun reset():{
     *        a = null
     *        b.clear()
     *    }"""
     */
    fun resetFunction(): String = allParameters.joinToString(separator = "\n") { it.reset() }

    /**
     * A standard implementation of the build Function generation. Produces Kotlin code that verifies that all necessary
     * builder parameters are configured and returns an object of X. Note that this code snippet generates code that may
     * cause name clashes when an attribute in class X is "autoGenMap" or "autoGenControl" so avoid that please.
     * @param [constructorInvocation] allows to change the constructor invocation.
     * @param [parameterEvaluation] allows to change how the parameters should be called in the constructor invocation
     */
    protected fun buildWithoutDefaults(
        constructorInvocation: String = "${classDeclaration.name}${classDeclaration.simpleGenerics}",
        parameterEvaluation: Parameter.() -> String = Parameter::evaluate
    ): String {
        return TextBuilder().apply {
            +"// Created by build Without Defaults"
            +requiredParameterGuard()
            +"return $constructorInvocation(${
                specifiedParameters.joinToString(separator = ", ") {
                    it.parameterEvaluation()
                }
            })"
        }.makeText()
    }

    /**
     * Adds a text clause to the builder verifying that all attributes that need to be set are set.
     */
    protected fun requiredParameterGuard(): String {
        return TextBuilder().apply {
            val nullableParameters = nonDefaultableParameters.filter { it.isNullable() && !it.originalIsNullable }
            if (nullableParameters.isNotEmpty()) {
                val map = nullableParameters.joinToString(
                    prefix = "listOf(",
                    postfix = ").toMap()"
                ) { "\"${it.name}\" to (${it.name} != null)" }
                +"val autoGenMap = $map"
                +"val autoGenControl = autoGenMap.filter{it.value == false}.keys"
                +"require(autoGenControl.isEmpty())" {
                    +$$"\"The following attributes need to be set ${autoGenControl}\""
                }
            }
        }.makeText()

    }

    companion object {
        /**
         * Enum to represent potential states of a class
         */
        enum class ClassStates {
            CLASS,
            ABSTRACT_CLASS,
            INTERFACE;

            companion object {
                fun parse(classDeclaration: KSClassDeclaration): ClassStates {
                    if (classDeclaration.isAbstract()) {
                        return if (classDeclaration.primaryConstructor == null) INTERFACE else ABSTRACT_CLASS
                    }
                    return CLASS
                }
            }
        }

        /**
         * Get the fitting class state from a declaration
         */
        fun fromDeclaration(classDeclaration: KSClassDeclaration): BuildFunction {
            return when (ClassStates.parse(classDeclaration)) {
                ClassStates.INTERFACE -> InterfaceBuilder(classDeclaration)
                ClassStates.CLASS -> ClassBuilder(classDeclaration)
                ClassStates.ABSTRACT_CLASS -> AbstractClassBuilder(classDeclaration)
            }
        }
    }
}

/**
 * This is the standard builder for a concrete class annotated with [Buildable].
 */
open class ClassBuilder(classDeclaration: KSClassDeclaration) : BuildFunction(classDeclaration) {
    /**
     * Since instantiations of concrete classes can have a relatively arbitrary number of defaultable parameters, we
     * need a strategy to build around the fact that we cannot trivially extract default values via KSP. The build
     * function switches the creation strategy depending on the number of defaultable parameters.
     */
    override fun createBuildFunction(): String {
        return "override fun build(): ${classDeclaration.name}${classDeclaration.simpleGenerics}" {
            // If either no defaultable parameter exists, or all are overwritten via external values, the standard construction should be used
            if (defaultableParameters.all { it.hasExternalDefault }) {
                // If no parameters have either intrinsic default values or default values specified by the annotation, we can simply generate a standard builder pattern.
                +buildWithoutDefaults()
            } else {
                // The list construction causes exponential growth by the number of defaultable parameters, the reflection construction is very slow but does not grow.
                if (defaultableParameters.size > 5) +buildWithReflection() else +buildWithListConstruction()
            }
        }
    }

    /**
     * Creates a build function call using kotlin reflection. This is relatively slow and may hamper performance when
     * used as hot code. However, the generated text grows in O(1) for the number of default parameters.
     */
    private fun buildWithReflection(): String {
        return TextBuilder().apply {
            +"val autoGenTargetConstructor = ${classDeclaration.name}::class.primaryConstructor ?: throw NoSuchElementException(\"A primary constructor is required for the Buildable to work\")"
            +"val autoGenRequiredParameters = autoGenTargetConstructor.parameters.filter{!it.isOptional}"
            +"val autoGenDefaultables : Map<String, () -> Any?> = ${
                allParameters.joinToString(
                    prefix = "mapOf(",
                    postfix = ")",
                    separator = ","
                ) { "\"${it.name}\" to {${it.name}}" }
            }"

            +"require(autoGenRequiredParameters.all { autoGenDefaultables[it.name]?.invoke() != null })" {
                +$$"\"The following attributes are not set and required ${autoGenRequiredParameters.filter { autoGenDefaultables[it.name]?.invoke() == null }.map{it.name}}\""
            }
            +"val autoGenParamMap = autoGenTargetConstructor.parameters.map { it to autoGenDefaultables[it.name]?.invoke() }.filter { it.second != null }.toMap()"
            +"return autoGenTargetConstructor.callBy(autoGenParamMap)"
        }.makeText()
    }

    /**
     * Generates a list switch to determine the proper call to the class construction by checking which builder
     * parameters are null and calling the constructor with only the set of parameters which are actually set in the
     * builder
     */
    private fun buildWithListConstruction(): String {
        return TextBuilder().apply {
            +"val autoGenTargetParameters = listOf(${
                requiredDefaultables.joinToString(separator = ", ") { "${it.name} != null" }
            })"
            +requiredParameterGuard()
            +"return when(autoGenTargetParameters)" {
                for (i in 0..<2.pow(requiredDefaultables.size)) {
                    val zip = i.toBinaryRepresentation(requiredDefaultables.size).zip(requiredDefaultables)
                    val targets = zip.filter { it.first }.map { it.second }
                    val output =
                        targets.joinToString(separator = ", ") { it.evaluate() }
                    val remainingParameters =
                        specifiedParameters.joinToString(separator = ", ") {
                            it.evaluate()
                        }
                    val parameterCollector = mutableListOf<String>()
                    if (targets.isNotEmpty()) parameterCollector.add(output)
                    if (specifiedParameters.isNotEmpty()) parameterCollector.add(remainingParameters)
                    +"listOf(${zip.map { it.first }.joinToString(", ")}) -> ${classDeclaration.name}(${
                        parameterCollector.joinToString(
                            separator = ", "
                        )
                    })"
                }
                +"else -> throw IllegalArgumentException()"
            }
        }.makeText()
    }
}

/**
 * Abstract Classes can be annotated with [Buildable] as well. In this case we need a dummy implementation of a class that
 * concretely implements the target interface/abstract class. Also, we need to adapt certain
 */
abstract class AbstractBuilder(classDeclaration: KSClassDeclaration) : ClassBuilder(classDeclaration) {
    /**
     * The name of the new dummy class. Note that there may be name clashes if the class exists already somewhere else.
     * So attempt to avoid building Dummy-X classes when using the [Buildable] annotation on X
     */
    val defaultClassName = "Dummy" + classDeclaration.nameWithGenerics

    /**
     * Determines how the dummy class is constructed. Separated because Interface instantiations do not use a constructor
     * whereas an abstract class does.
     */
    abstract val defaultClassInstantiation: String

    /**
     * The instantiation of an abstract class remains similar for interfaces & abstract classes. Only the instantiation
     * of the dummy class changes.
     */
    open fun generateDummyClass(): String {
        return "class $defaultClassInstantiation" {
            +classDeclaration.getAllFunctions().filter { it.isAbstract }.joinToString(separator = "\n") {
                "override ${it.mimic}" {
                    +"throw NotImplementedError()"
                }
            }
        }
    }

    /**
     * Adds the dummy class definition before the parameters of the builder.
     */
    override fun builderParameters(): String {
        return TextBuilder().apply {
            +generateDummyClass()
            +super.builderParameters()
        }.makeText()
    }
}

/**
 * Implements the Builder for an abstract class Annotated with the [Buildable] annotation
 */
class AbstractClassBuilder(classDeclaration: KSClassDeclaration) : AbstractBuilder(classDeclaration) {
    /**
     * The instantiation of an Abstract class X would be like Class(attr: Int, next: Double): X(attr, next)
     * where the attributes are taken from the primary constructor of the abstract class. Note that the parameters
     * do only need to be referenced by name, and since the type of the parameters from [Parameter.toReferenceInstantiation]
     * should accurately mimic the types of the original constructor it should be possible to build the dummy template
     * by name reference alone.
     */
    override val defaultClassInstantiation: String =
        "$defaultClassName(${allParameters.joinToString(",") { it.toReferenceInstantiation() }}) : ${classDeclaration.nameWithGenerics}(${
            allParameters.joinToString(
                ", "
            ) { it.name }
        })"

    /**
     * The build function for an abstract class must override the constructor invocation of the build function to [defaultClassName]
     * as there is no option to instantiate the abstract class. (Note that this reason is why we need to build the dummy
     * class implementation in the first place.)
     */
    override fun createBuildFunction(): String {
        return "override fun build(): ${classDeclaration.name}${classDeclaration.simpleGenerics}" {
            +buildWithoutDefaults(constructorInvocation = defaultClassName)
        }
    }

}

/**
 * Implements the builder for interfaces annotated with [Buildable]. Unlike the other builders the interface is augmented
 * by a constructor parameter "builder" (<params>) -> Target-Interface. So the end user can specify a correct interface
 * instantiation rather than the default implementation.
 */
class InterfaceBuilder(classDeclaration: KSClassDeclaration) : AbstractBuilder(classDeclaration) {
    /**
     * As an interface has no primary constructor, the list of parameters must be extracted elsewhere. Instead, the
     * defined properties in the interface can be extracted, as these must be instantiated by the dummy class to be a
     * valid implementation of the target interface.
     */
    override val defaultableParameters: List<Parameter> = emptyList()
    override val nonDefaultableParameters: List<Parameter> = classDeclaration.allProperties()
    override val allParameters: List<Parameter> = classDeclaration.allProperties()

    /**
     * The class instantiation would be along the lines of Default-X(override val param1: P1) : X
     */
    override val defaultClassInstantiation: String =
        "$defaultClassName(${allParameters.joinToString(", ") { it.toOverrideInstantiation(false) }}) : ${classDeclaration.nameWithGenerics}"

    /**
     * The instantiation of the buildable target object is evalated with the "builder" call. Per default this maps to
     * the dummy implementation, but can be specified by the end user.
     */
    override fun createBuildFunction(): String {
        return "override fun build(): ${classDeclaration.name}${classDeclaration.simpleGenerics}" {
            +buildWithoutDefaults(constructorInvocation = "builder", parameterEvaluation = Parameter::evaluateSimple)
        }
    }

    /**
     * The class declaration definition is augmented by the "builder" parameter.
     */
    override fun classDeclaration(): String {
        val helperVars = allParameters.indices.joinToString(separator = ", ") { "p$it" }
        return TextBuilder().apply {
            +"class ${classDeclaration.builderNameWithResolvedGenerics}(val builder: (${
                allParameters.joinToString(
                    separator = ", "
                ) { it.overrideableTypeWithGenerics(false) }
            }) -> ${classDeclaration.nameWithGenerics} = {$helperVars -> $defaultClassName($helperVars)}) : Builder<${classDeclaration.nameWithGenerics}>"
        }.makeText()
    }
}

/**
 * Helper function to map an int to its corresponding binary representation.
 */
private fun Int.toBinaryRepresentation(size: Int): List<Boolean> {
    return Integer.toBinaryString(this).padStart(size, '0').map { it == '1' }
}

/** Slow but no conversion errors. In the future someone may want to turn this into a reasonably useful function
 *
 */
private fun Int.pow(exponent: Int): Int {
    var result = 1
    repeat(exponent) {
        result *= this
    }
    return result
}
