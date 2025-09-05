package util

import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
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

fun KSClassDeclaration.getAllFunctionsList(): List<KSFunctionDeclaration> {
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

fun KSType.isInlineClass(): Boolean {
    // Get the class declaration associated with this type
    val classDeclaration = this.declaration as? KSClassDeclaration ?: return false

    // Check if the class is inline
    if (classDeclaration.modifiers.contains(Modifier.INLINE)) return true

    return classDeclaration.annotations.any { annotation ->
        annotation.shortName.asString() == "JvmInline" &&
            annotation.annotationType.resolve().declaration.qualifiedName?.asString() == "kotlin.jvm.JvmInline"
    }
}

fun TypeName.isCollectionType(): Boolean {
    return when (this) {
        is ParameterizedTypeName -> {
            when (rawType.toString()) {
                "utils.collections.ClearableList",
                "kotlin.collections.List",
                "kotlin.collections.Set",
                "kotlin.collections.Map" -> true
                else -> false // If it's not a known collection, keep it as is
            }

        }
        else -> false // Non-parameterized types are returned as-is
    }
}

fun TypeName.isMutableCollectionType(): Boolean {
    return when (this) {
        is ParameterizedTypeName -> {
            when (rawType.toString()) {
                "utils.collections.MutableClearableList",
                "kotlin.collections.MutableList",
                "kotlin.collections.MutableSet",
                "kotlin.collections.MutableMap" -> true
                else -> false // If it's not a known collection, keep it as is
            }

        }
        else -> false // Non-parameterized types are returned as-is
    }
}

fun TypeName.toMutableCollectionType(): TypeName {
    return when (this) {
        is ParameterizedTypeName -> {
            // Check the raw type and convert it
            val mutableRawType = when (rawType.toString()) {
                "utils.collections.ClearableList" -> ClassName("utils.collections", "MutableClearableList")
                "kotlin.collections.List" -> ClassName("kotlin.collections", "MutableList")
                "kotlin.collections.Set" -> ClassName("kotlin.collections", "MutableSet")
                "kotlin.collections.Map" -> ClassName("kotlin.collections", "MutableMap")
                else -> rawType // If it's not a known collection, keep it as is
            }
            // Reapply the type arguments to the new raw type
            mutableRawType.parameterizedBy(typeArguments)
        }
        else -> this // Non-parameterized types are returned as-is
    }
}

fun Resolver.getKSTypeByName(fqName: String, generics: List<KSTypeArgument> = emptyList()): KSType? {
    val classDeclaration = getClassDeclarationByName(getKSNameFromString(fqName))
    return classDeclaration?.asType(generics)
}

fun TypeName.getEmptyInitializer(): String {
    return when (this.toString().split("<")[0]) {
        "utils.collections.ClearableList" -> "utils.collections.mutableClearableListOf()"
        "kotlin.collections.List" -> "mutableListOf()"
        "kotlin.collections.Set" -> "mutableSetOf()"
        "kotlin.collections.Map" -> "mutableMapOf()"
        "kotlin.collections.MutableList" -> "mutableListOf()"
        "kotlin.collections.MutableSet" -> "mutableSetOf()"
        "kotlin.collections.MutableMap" -> "mutableMapOf()"
        "utils.collections.MutableClearableList" -> "utils.collections.mutableClearableListOf()"
        else -> "TODO()" // Fallback for non-collection types
    }
}