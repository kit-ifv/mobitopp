package processor.mutable

import Mutable
import SuccessiveAnnotationProcessor
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.*
import util.resolveGenerics
import kotlin.reflect.KClass

class MutableProcessor(
    override val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
): SuccessiveAnnotationProcessor<Mutable> {

    override val annotationType: KClass<Mutable>
        get() = Mutable::class

    @OptIn(KspExperimental::class)
    override fun castAnnotations(symbol: KSAnnotated): List<Mutable> =
        symbol.getAnnotationsByType(annotationType).toList()

    override fun processAnnotatedSymbol(
        symbol: KSAnnotated,
        annotations: List<Mutable>,
        resolver: Resolver
    ): List<KSAnnotated> {
        if (symbol !is KSClassDeclaration) {
            return emptyList()
        }

        val packageName = symbol.packageName.asString()
        val className = symbol.simpleName.asString()
        val mutableClassName = "Mutable${className}"

        if (!symbol.isSubClassable()) {
            logger.warn(
                "Cannot create a Mutable subclass '$mutableClassName' of '$className' ('${packageName}.${className}')"
            )
            return emptyList()
        }

        // Retrieve constructor parameters
        val constructorParams = symbol.primaryConstructor?.parameters ?: emptyList()
        val props = symbol.getInheritedProperties(resolver).filter { it.isOverridable() }

        generateMutableClass(symbol, packageName, className, mutableClassName, constructorParams, props.toList())

        return emptyList()
    }

    private fun generateMutableClass(
        symbol: KSClassDeclaration,
        packageName: String,
        className: String,
        mutableClassName: String,
        constructorParams: List<KSValueParameter>,
        props: List<KSPropertyDeclaration>,
    ) {
        logger.warn("Generate mutable class $packageName.$mutableClassName: $className")
        val mutClassBuilder = TypeSpec.classBuilder(mutableClassName)
                                        .addModifiers(KModifier.PUBLIC)
        if (symbol.classKind != ClassKind.INTERFACE) {
            mutClassBuilder.superclass(ClassName(packageName, className))
        } else {
            mutClassBuilder.addSuperinterface(ClassName(packageName, className))
        }


        val constructorBuilder = generateConstructor(mutClassBuilder, constructorParams)
        if (constructorBuilder.parameters.isNotEmpty()) {
            mutClassBuilder.primaryConstructor(constructorBuilder.build())
        }

        logger.warn("Generate mutable properties: ${props.map { it.simpleName.getShortName() + ": " + it.type.resolve().toString() }}")
        props.forEach {
            generateMutableProperty(mutClassBuilder, constructorBuilder, it)
        }

        // Write the class to a file
        val fileSpec = FileSpec.builder(packageName, mutableClassName)
            .addType(mutClassBuilder.build())
            .build()

        val outputStream = codeGenerator.createNewFile(
            Dependencies(true, symbol.containingFile!!),
            packageName,
            mutableClassName
        )
        outputStream.writer().use { writer ->
            fileSpec.writeTo(writer)
        }

    }

    private fun generateConstructor(
        mutClassBuilder: TypeSpec.Builder,
        constructorParams: List<KSValueParameter>
    ): FunSpec.Builder {

        val constructorSpec = FunSpec.constructorBuilder()

        if (constructorParams.isNotEmpty()) {
            logger.warn(
                "Generate (super)constructor params: ${constructorParams.map { it.name.toString() + ": " + it.type.resolve().toString() }}"
            )

            val superConstructorCall = CodeBlock.builder()

            constructorParams.forEach { param ->
                val paramName = param.name?.asString() ?: "arg${constructorParams.indexOf(param)}"
                val paramType = param.type.resolve().resolveGenerics()

                // Add parameter to the constructor
                constructorSpec.addParameter(paramName, paramType)

                // Add parameter to the super constructor call
                superConstructorCall.add(paramName)
                if (constructorParams.indexOf(param) != constructorParams.size - 1) {
                    superConstructorCall.add(", ")
                }
            }

            mutClassBuilder.addSuperclassConstructorParameter(superConstructorCall.build())
        }

        return constructorSpec
    }

    private fun generateMutableProperty(
        mutClassBuilder: TypeSpec.Builder,
        constructorBuilder: FunSpec.Builder,
        property: KSPropertyDeclaration
    ) {
        val propertyName = property.simpleName.asString()
        val propertyType = property.type.resolve().resolveGenerics()

        if (property.canBeVar()) {
            val propertySpec = PropertySpec.builder(propertyName, propertyType, KModifier.OVERRIDE)
                .mutable(true)

                if (property.canBeLateinitInSubclass()) {
                    propertySpec.initializer("lateinit var $propertyName")
                } else {
                    propertySpec.initializer("TODO()")
                }

            mutClassBuilder.addProperty(propertySpec.build())

        } else if (propertyName !in constructorBuilder.parameters.map { it.name }) { //if property not yet in constructor, add it
            constructorBuilder.addParameter(propertyName, propertyType)
        }

    }



}

/**
 * Extension function to check if a property can be overridden
 */
private fun KSPropertyDeclaration.isOverridable(): Boolean {
    // A property is overridable if:
    // 1. It is not private
    // 2. It is either open or abstract
    val modifiers = this.modifiers
    return !modifiers.contains(Modifier.PRIVATE) && !modifiers.contains(Modifier.FINAL)
}

private fun KSClassDeclaration.isSubClassable(): Boolean {
    return this.classKind == ClassKind.INTERFACE || // Interfaces are inherently subclassable
            modifiers.contains(Modifier.OPEN) ||
            modifiers.contains(Modifier.ABSTRACT)
}

private fun KSPropertyDeclaration.canBeVar(): Boolean =
    ((this.parentDeclaration as? KSClassDeclaration)?.classKind == ClassKind.INTERFACE)
            || this.modifiers.contains(Modifier.OPEN) || this.modifiers.contains(Modifier.ABSTRACT)


fun KSPropertyDeclaration.canBeLateinitInSubclass(): Boolean {
    // 1. Check if the property is mutable (`var`) or could become `var` in a subclass
    val canBeVar = this.isMutable || Modifier.VALUE in this.modifiers

    // If it can't be mutable, it can't be `lateinit`
    if (!canBeVar) return false

    // 2. Resolve the property type
    val type: KSType = this.type.resolve()

    // 3. Check if it is nullable
    if (type.isMarkedNullable) return false

    // 4. Check if it is a primitive type
    if (type.isPrimitive()) return false

//    // 5. Ensure it has no initializer
//    if (this.hasBackingField) return false

    // If all checks pass, it can be `lateinit` in a subclass
    return true
}

// Extension function to check if a type is primitive
fun KSType.isPrimitive(): Boolean {
    val primitiveTypes = setOf(
        "kotlin.Int", "kotlin.Float", "kotlin.Double", "kotlin.Short",
        "kotlin.Long", "kotlin.Boolean", "kotlin.Char", "kotlin.Byte"
    )
    return this.declaration.qualifiedName?.asString() in primitiveTypes
}

fun KSClassDeclaration.getInheritedProperties(resolver: Resolver): List<KSPropertyDeclaration> {
    val properties = mutableListOf<KSPropertyDeclaration>()

    // Add properties declared in this class/interface
    properties += this.getAllProperties()

    // Recursively add properties from superclasses and interfaces
    this.superTypes.forEach { superType ->
        val superTypeDeclaration = superType.resolve().declaration
        if (superTypeDeclaration is KSClassDeclaration) {
            properties += superTypeDeclaration.getInheritedProperties(resolver)
        }
    }

    // Return the unique properties (remove overridden duplicates)
    return properties.distinctBy { it.simpleName.asString() }
}
