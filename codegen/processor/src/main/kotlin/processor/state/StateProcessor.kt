package processor.state

import GroupingProcessor
import MessageCalled
import StateCalled
import com.google.devtools.ksp.containingFile
import com.google.devtools.ksp.getConstructors
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeParameter
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import util.parameter
import util.toClassName
import kotlin.reflect.KClass

data class AnnotationData(val name: String, val scopes: List<ClassName>)
private val ANY = ClassName("kotlin", "Any")


private const val TARGET_SUFFIX = "_trgt"
private const val SCOPE_SUFFIX = "_scope"

abstract class TypeReferenceAndScopedConstructorProcessor<A: Annotation>(
    override val logger: KSPLogger,
    private val codeGenerator: CodeGenerator,
): GroupingProcessor<A, AnnotationData, KSFile> {

    abstract val fileNameSuffix: String

    override fun castAnnotations(annotation: KSAnnotation) =
        AnnotationData(
            name = annotation.parameter("name"),
            scopes = annotation.arguments
                .first { it.name?.asString() == "scopes" }
                .value.let { value ->
                    (value as? List<*>)?.mapNotNull { entry ->
                        (entry as? KSType)?.declaration?.toClassName()
                    } ?: emptyList()
                }
        )

    override fun groupKey(
        symbol: KSAnnotated,
        annotations: List<AnnotationData>
    ): KSFile? = symbol.containingFile

    override fun processAnnotatedSymbolGroup(
        groupKey: KSFile,
        symbolGroup: List<KSAnnotated>,
        annotations: Map<KSAnnotated, List<AnnotationData>>,
        resolver: Resolver
    ): List<KSAnnotated> {

        val filePackage = groupKey.packageName.asString()
        val originalFileName = groupKey.fileName.removeSuffix(".kt")
        val generatedFileName = "${originalFileName}$fileNameSuffix"
        val fileSpecBuilder = FileSpec.builder(filePackage, generatedFileName)


        logger.warn("  states of file $originalFileName in $filePackage")

        for (symbol in symbolGroup) {
            try {
                processSingleClass(symbol, annotations, fileSpecBuilder, resolver)
                continue

            } catch (e: Exception) {
                logger.warn("Error while processing annotated symbol: $symbol")
                logger.warn("  - ${e.message}")
            }


        }

        codeGenerator.createNewFile(
            dependencies = Dependencies(false, groupKey),
            packageName = filePackage,
            fileName = generatedFileName
        ).bufferedWriter().use { writer ->
            fileSpecBuilder.build().writeTo(writer)
        }

        return emptyList()
    }

    private fun processSingleClass(
        symbol: KSAnnotated,
        annotations: Map<KSAnnotated, List<AnnotationData>>,
        fileSpecBuilder: FileSpec.Builder,
        resolver: Resolver
    ) {
        val classDecl = symbol as? KSClassDeclaration ?: return
        val data = annotations[symbol]?.firstOrNull() ?: return

        logger.warn("  process ${symbol.qualifiedName?.asString()}")
        logger.warn("  process symbol $data")

        fileSpecBuilder.addProperty(
            createKClassAliasProperty(classDecl, data)
        )

        logger.warn("    resolve message type params ${classDecl.typeParameters}")
        val targetTypeParams = getTargetTypeParameters(classDecl)
        logger.warn("      > result $targetTypeParams")


        for (scope in data.scopes) {
            val scopeDeclaration = getScopeDeclaration(scope, resolver)
            logger.warn("    resolve scope type params ${scopeDeclaration?.typeParameters}")
            val scopeTypeParams = getScopeTypeParameters(scopeDeclaration, fileSpecBuilder)
            logger.warn("      > result $scopeTypeParams")

            val functionName = data.name.replaceFirstChar { it.lowercase() }
            val className = classDecl.toClassName()
            val returnType = if (targetTypeParams.isEmpty()) {
                className
            } else {
                className.parameterizedBy(targetTypeParams)
            }
            logger.warn("    return type $returnType")

            for (constructor in classDecl.getConstructors()) {
                val funBuilder = createInitializeFunction(
                    constructor,
                    targetTypeParams,
                    className,
                    functionName,
                    returnType,
                    scopeDeclaration,
                    scopeTypeParams
                )

                logger.warn("    add function ${funBuilder.build().name}: ${funBuilder.build()}")

                fileSpecBuilder.addFunction(funBuilder.build())
            }
        }

    }

    private fun getScopeTypeParameters(
        scopeDeclaration: KSClassDeclaration?,
        fileSpecBuilder: FileSpec.Builder
    ): List<TypeVariableName> = scopeDeclaration?.also {
        fileSpecBuilder.addImport(it.packageName.asString(), it.simpleName.asString())
    }?.typeParameters?.map {
        val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
        val name = "${it.name.asString()}$SCOPE_SUFFIX"
        if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
    } ?: emptyList()

    private fun getScopeDeclaration(
        scope: ClassName,
        resolver: Resolver
    ): KSClassDeclaration? {
        val scopeKClass = scope.takeIf { it != ANY }
        return scopeKClass?.let { scopeClassName ->
            resolver.getClassDeclarationByName(resolver.getKSNameFromString(scopeClassName.canonicalName))
        }
    }

    private fun getTargetTypeParameters(classDecl: KSClassDeclaration): List<TypeVariableName> = classDecl.typeParameters.map {
        val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
        val name = "${it.name.asString()}$TARGET_SUFFIX"
        if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
    }

    private fun createKClassAliasProperty(
        classDecl: KSClassDeclaration,
        messageCalled: AnnotationData,
    ): PropertySpec {
        val className = classDecl.toClassName()
        val messageName = messageCalled.name.replaceFirstChar { it.uppercase() }

        // Add: val <name> = <Class>::class
        val type = classDecl.toStarParameterizedKClassType()
        val classValSpec = PropertySpec.builder(messageName, type)
            .initializer("$className::class")
            .build()

        return classValSpec
    }

    private fun createInitializeFunction(
        constructor: KSFunctionDeclaration,
        targetTypeParameters: List<TypeVariableName>,
        className: ClassName,
        functionName: String,
        returnType: TypeName,
        scopeDeclaration: KSClassDeclaration?,
        scopeTypeParams: List<TypeVariableName>
    ): FunSpec.Builder {
        val allTypeParams = scopeTypeParams + targetTypeParameters

        logger.warn("    resolve parameters ${constructor.parameters}")

        val parameters = constructor.parameters.mapIndexed { index, param ->
            val paramName = param.name?.asString() ?: "param$index"
            val (paramKSType, paramTypeName) = parameterTypeOf(param)
            val receiverType = scopeDeclaration?.asStarProjectedType()

            val defaultValue = scopeDeclaration?.let {
                inferDefaultValue(paramName, paramKSType, paramTypeName, receiverType, getReceiverProperties(it))
            }

            ParameterSpec.builder(paramName, paramTypeName).apply {
                defaultValue?.let { defaultValue(it) }
            }.build()
        }

        logger.warn("      > resulting parameters $parameters")


        val constructorCall = if (targetTypeParameters.isNotEmpty()) {
            val typeArgs = targetTypeParameters.joinToString(", ") { it.name }
            "$className<$typeArgs>(${parameters.joinToString { it.name }})"
        } else {
            "$className(${parameters.joinToString { it.name }})"
        }

        logger.warn("    constructor call $constructorCall")

        val funBuilder = FunSpec.builder(functionName)
            .addParameters(parameters)
            .returns(returnType)
            .addStatement("return $constructorCall")

        if (allTypeParams.isNotEmpty()) {
            funBuilder.addTypeVariables(allTypeParams)
        }

        logger.warn("    add receiver $scopeDeclaration")
        scopeDeclaration?.let { declaration ->
            val receiver = if (scopeTypeParams.isNotEmpty()) {
                scopeDeclaration.toClassName().parameterizedBy(scopeTypeParams)
            } else {
                scopeDeclaration.toClassName()
            }

            logger.warn("      > resulting receiver $receiver")

            funBuilder.receiver(receiver)
        }

        return funBuilder
    }

    private fun parameterTypeOf(param: KSValueParameter): Pair<KSType?, TypeName> {
        val typeRef = param.type

        val resolvedKSType = try {
            typeRef.resolve()
        } catch (_: Exception) {
            null
        }

        val typeName = try {
            val declaredType = resolvedKSType?.declaration
            if (declaredType is KSTypeParameter) {
                TypeVariableName(declaredType.name.asString() + TARGET_SUFFIX)
            } else {
                resolvedKSType?.toTypeName()
                    ?: TypeVariableName(typeRef.element?.toString() + TARGET_SUFFIX)
            }
        } catch (_: Exception) {
            TypeVariableName(typeRef.element?.toString() + TARGET_SUFFIX)
        }

        return resolvedKSType to typeName

    }

    fun inferDefaultValue(
        paramName: String,
        paramKSType: KSType?,
        paramTypeName: TypeName,
        receiverKSType: KSType?,
        receiverProps: Map<String, Pair<KSType?, TypeName>>
    ): CodeBlock? {
        return when {
            // Case 1: receiver is assignable to param
            paramKSType != null && receiverKSType != null &&
                    paramKSType.isAssignableFrom(receiverKSType) -> {
                CodeBlock.of("this")
            }

            // Case 2: receiver property is assignable to param
            receiverProps[paramName]?.first?.let { receiverPropType ->
                paramKSType != null && paramKSType.isAssignableFrom(receiverPropType)
            } == true -> {
                CodeBlock.of("this.%L", paramName)
            }

            else -> null
        }
    }

    private fun getReceiverProperties(scopeDeclaration: KSClassDeclaration?) = scopeDeclaration?.getAllProperties()
        ?.associate { prop ->
            val name = prop.simpleName.asString()

            val ksType = try {
                prop.type.resolve()
            } catch (_: Exception) {
                null
            }

            val typeName = try {
                ksType?.toTypeName()
                    ?: TypeVariableName("${prop.type.element?.toString()}$SCOPE_SUFFIX")
            } catch (_: Exception) {
                TypeVariableName("${prop.type.element?.toString()}$SCOPE_SUFFIX")
            }

            name to (ksType to typeName)
        } ?: emptyMap()

    private fun KSClassDeclaration.toStarParameterizedKClassType(): TypeName {
        val classType = this.toClassName()
        val typeArguments = this.typeParameters.map { STAR }
        return KClass::class.asClassName().parameterizedBy(
            if (typeArguments.isEmpty()) classType else classType.parameterizedBy(typeArguments)
        )
    }

}

class TestStateProcessor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator,
): TypeReferenceAndScopedConstructorProcessor<StateCalled>(logger, codeGenerator) {

    override val fileNameSuffix: String
        get() = "GeneratedStateTypes"

    override val annotationType: KClass<StateCalled>
        get() = StateCalled::class
}

class TestMessageProcessor(
    logger: KSPLogger,
    codeGenerator: CodeGenerator,
): TypeReferenceAndScopedConstructorProcessor<MessageCalled>(logger, codeGenerator) {

    override val fileNameSuffix: String
        get() = "GeneratedMessageTypes"

    override val annotationType: KClass<MessageCalled>
        get() = MessageCalled::class
}
