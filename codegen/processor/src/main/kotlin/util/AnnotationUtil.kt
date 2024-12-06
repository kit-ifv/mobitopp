package util

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName

inline fun <reified T> KSAnnotation.parameter(parameter: String): T =
    requireNotNull(arguments.find { it.name?.asString() == parameter }){
        "Parameter '$parameter' is missing from the annotation @${this.shortName.asString()}:\n" +
            "arguments: ${arguments.map { it.name?.asString() }}\n" +
            "file: ${this.containingFile}\n" +
            "parent: ${this.parent}\n"
    }.value.let {

       it as? T ?: error(
            "Cannot cast @${this.shortName.asString()} parameter $parameter [$it] to: ${T::class.qualifiedName}\n" +
            "file: ${this.containingFile}\n" +
            "parent: ${this.parent}\n"
       )
    }

fun KSDeclaration.toClassName() = ClassName(packageName.asString(), simpleName.asString())

fun KSDeclaration.toTypeNameWithGenerics() : TypeName{
    val className = ClassName(packageName.asString(), simpleName.asString())
    val typeArgs = typeParameters.map { it.toTypeVariableName() }

    return if (typeArgs.isNotEmpty()) {
        // Return the ClassName as a ParameterizedTypeName if there are generics
        className.parameterizedBy(typeArgs)
    } else {
        // Return ClassName if there are no generics
        className
    }

}

fun KSTypeParameter.toTypeVariableName(): TypeVariableName {
    // Create a TypeVariableName from the generic parameter name
    val name = this.name.asString()

    // Handle bounds (if any)
    val bounds = this.bounds.map { it.toTypeName() }.toList()

    return if (bounds.isNotEmpty()) {
        // If bounds exist, create TypeVariableName with bounds
        TypeVariableName.invoke(name, bounds)
    } else {
        // If no bounds, just create a simple TypeVariableName
        TypeVariableName.invoke(name)
    }
}

fun KSTypeReference?.toTypeName(): TypeName {
    return this?.resolve()?.declaration?.let {
        ClassName(it.packageName.asString(), it.simpleName.asString())
    } ?: TypeVariableName("Any")
}

fun KSType.resolveGenerics(): TypeName {

    val baseType = this.declaration.toClassName()

    if (arguments.isEmpty()) return baseType

    return baseType.parameterizedBy(
        arguments.mapNotNull{ it.type?.resolve()?.resolveGenerics() }
    )

}

fun KSClassDeclaration.getAllFunctions(): List<KSFunctionDeclaration> {
    // Get functions declared in this class
    val ownFunctions = this.getDeclaredFunctions().toList()

    // Get functions from superclasses and interfaces
    val inheritedFunctions = getSuperTypes().map {
        it.declaration as? KSClassDeclaration
    }.filterNotNull().flatMap {
        it.getAllFunctions()
    }.toList()

    // Combine own functions and inherited functions
    return ownFunctions + inheritedFunctions
}

fun KSClassDeclaration.getSuperTypes(): Sequence<KSType> {
    // Get the super types (interfaces and superclass)
    return this.superTypes
        .map { it.resolve() }
        .filter { it.declaration is KSClassDeclaration } // Filter only class types
}

fun KSNode?.asClass() = this as? KSClassDeclaration ?:
    error(symbolErrorMessage("KSClassDeclaration"))

fun KSNode?.asFunction() = this as? KSFunctionDeclaration ?:
    error(symbolErrorMessage("KSFunctionDeclaration"))

private fun KSNode?.symbolErrorMessage(expectedClass: String) =
    "Expected symbol to be a $expectedClass but was ${this?.let { it::class.simpleName }}\n   Symbol: $this"
