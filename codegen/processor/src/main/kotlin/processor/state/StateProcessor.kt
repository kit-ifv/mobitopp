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
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
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
            val paramType = parameterTypeOf(param)

            val defaultValue = scopeDeclaration?.let {
                inferDefaultValue(paramName, paramType, it.toClassName(), getReceiverProperties(it))
            }

            ParameterSpec.builder(paramName, paramType).apply {
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

    private fun parameterTypeOf(param: KSValueParameter): TypeName {
        val typeRef = param.type
        val paramType = try {
            val resolved = typeRef.resolve()
            val declaredType = resolved.declaration
            val isTypeParam = declaredType is KSTypeParameter

            if (isTypeParam) {
                // Use the declared type variable name as a TypeVariableName
                TypeVariableName(declaredType.name.asString() + TARGET_SUFFIX)
            } else {
                resolved.toTypeName()
            }
        } catch (e: Exception) {
            // Fallback to TypeVariable if it's a raw type parameter
            val simpleName = typeRef.element.toString() + TARGET_SUFFIX
            TypeVariableName(simpleName)
        }
        return paramType
    }

    fun inferDefaultValue(
        paramName: String,
        paramType: TypeName,
        receiverType: TypeName,
        receiverProps: Map<String, TypeName>
    ): CodeBlock? {
        return when {
            // Case 1: param type equals receiver type → use `this`
            paramType == receiverType -> {
                CodeBlock.of("this")
            }

            // Case 2: param name + type match a receiver property → use `this.<prop>`
            receiverProps[paramName]?.let { receiverPropType ->
                receiverPropType == paramType
            } == true -> {
                CodeBlock.of("this.%L", paramName)
            }

            else -> null
        }
    }

    private fun getReceiverProperties(scopeDeclaration: KSClassDeclaration?) = scopeDeclaration?.getAllProperties()
        ?.associate {
            val name = it.simpleName.asString()
            val typeName = try {
                val resolved = it.type.resolve()
                resolved.toTypeName()
            } catch (e: Exception) {
                // fallback to TypeVariable
                TypeVariableName( "${it.type.element?.toString()}$SCOPE_SUFFIX")
            }
            name to typeName
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

//
//class StateProcessor(
//    override val logger: KSPLogger,
//    private val codeGenerator: CodeGenerator,
//
//): GroupingProcessor<StateCalled, AnnotationData, KSFile> {
//
//    override val annotationType: KClass<StateCalled>
//        get() = StateCalled::class
//
//
//    override fun castAnnotations(annotation: KSAnnotation) =
//        AnnotationData(
//            name = annotation.parameter("name"),
//            scope = annotation.parameter<KSType>("scope").declaration.toClassName()
//        )
//
//    override fun groupKey(
//        symbol: KSAnnotated,
//        annotations: List<AnnotationData>
//    ): KSFile? = symbol.containingFile
//
//    override fun processAnnotatedSymbolGroup(
//        groupKey: KSFile,
//        symbolGroup: List<KSAnnotated>,
//        annotations: Map<KSAnnotated, List<AnnotationData>>,
//        resolver: Resolver
//    ): List<KSAnnotated> {
//
//        val filePackage = groupKey.packageName.asString()
//        val originalFileName = groupKey.fileName.removeSuffix(".kt")
//        val generatedFileName = "${originalFileName}GeneratedStates"
//        val fileSpecBuilder = FileSpec.builder(filePackage, generatedFileName)
//
//        logger.warn("states of file $originalFileName in $filePackage")
//
//        for (symbol in symbolGroup) {
//            val classDecl = symbol as? KSClassDeclaration ?: continue
//            val state = annotations[symbol]?.firstOrNull() ?: continue
//
//            logger.warn("process ${symbol.qualifiedName?.asString()}")
//            logger.warn("process state $state")
//
//
//            fileSpecBuilder.addProperty(
//            createStateAliasProperty(classDecl, state)
//            )
//
//            // --- Handle type parameters of message class ---
//            val messageTypeParams = classDecl.typeParameters.map {
//                val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
//                val name = "${it.name.asString()}_state"
//                if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
//            }
//
//            // --- Handle type parameters of scope class (if any) ---
//            val scopeKClass = state.scope.takeIf { it != ANY }
//            val scopeDeclaration = scopeKClass?.let { scopeClassName ->
//                resolver.getClassDeclarationByName(resolver.getKSNameFromString(scopeClassName.canonicalName))
//            }
//            val scopeTypeParams = scopeDeclaration?.also {
//                fileSpecBuilder.addImport(it.packageName.asString(), it.simpleName.asString())
//            }?.typeParameters?.map {
//                val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
//                val name = "${it.name.asString()}_scope"
//                if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
//            } ?: emptyList()
//
//            val scopeProps = scopeDeclaration?.getAllProperties()?.associateBy { it.simpleName.asString() }.orEmpty()
//
//
//            val functionName = state.name.replaceFirstChar { it.lowercase() }
//            val className = classDecl.toClassName()
//            val returnType = if (messageTypeParams.isEmpty()) {
//                className
//            } else {
//                className.parameterizedBy(messageTypeParams)
//            }
//
//            for (constructor in classDecl.getConstructors()) {
//                val funBuilder = createInitializeFunction(
//                    constructor,
//                    messageTypeParams,
//                    className,
//                    functionName,
//                    returnType,
//                    scopeDeclaration,
//                    scopeTypeParams,
//                    scopeProps
//                )
//
//                fileSpecBuilder.addFunction(funBuilder.build())
//
//            }
//
//
//        }
//
//        codeGenerator.createNewFile(
//            dependencies = Dependencies(false, groupKey),
//            packageName = filePackage,
//            fileName = generatedFileName
//        ).bufferedWriter().use { writer ->
//            fileSpecBuilder.build().writeTo(writer)
//        }
//
//        return emptyList()
//    }
//
//    private fun createInitializeFunction(
//        constructor: KSFunctionDeclaration,
//        stateTypeParams: List<TypeVariableName>,
//        className: ClassName,
//        functionName: String,
//        returnType: TypeName,
//        scopeDeclaration: KSClassDeclaration?,
//        scopeTypeParams: List<TypeVariableName>,
//        scopeProps: Map<String, KSPropertyDeclaration>
//    ): FunSpec.Builder {
//        val allTypeParams = scopeTypeParams + stateTypeParams
//
//        val parameters = constructor.parameters.mapIndexed { index, param ->
//            val name = param.name?.asString() ?: "param$index"
//            val type = param.type.resolve()
//            val typeName = type.toTypeName()
//
//            val defaultValue = getDefaultValue(type, scopeDeclaration, scopeProps, name)
//
//            ParameterSpec.builder(name, typeName).apply {
//                defaultValue?.let { defaultValue(it) }
//            }.build()
//        }
//
//
//        val constructorCall = if (stateTypeParams.isNotEmpty()) {
//            val typeArgs = stateTypeParams.joinToString(", ") { it.name }
//            "$className<$typeArgs>(${parameters.joinToString { it.name }})"
//        } else {
//            "$className(${parameters.joinToString { it.name }})"
//        }
//
//        val funBuilder = FunSpec.builder(functionName)
//            .addParameters(parameters)
//            .returns(returnType)
//            .addStatement("return $constructorCall")
//
//        if (allTypeParams.isNotEmpty()) {
//            funBuilder.addTypeVariables(allTypeParams)
//        }
//
//        scopeDeclaration?.let { declaration ->
//            val receiver = if (scopeTypeParams.isNotEmpty())
//                scopeDeclaration.toClassName().parameterizedBy(scopeTypeParams)
//            else
//                scopeDeclaration.toClassName()
//
//            funBuilder.receiver(receiver)
//        }
//
//        return funBuilder
//    }
//
//    private fun getDefaultValue(
//        type: KSType,
//        scopeDeclaration: KSClassDeclaration?,
//        scopeProps: Map<String, KSPropertyDeclaration>,
//        name: String
//    ): CodeBlock? = when {
//        // Case 1: same type as receiver
//        type == scopeDeclaration?.asStarProjectedType() -> CodeBlock.of("this")
//        // Case 2: matches property in scope
//        scopeProps[name]?.type?.resolve()?.isAssignableFrom(type) == true ->
//            CodeBlock.of("this.$name")
//
//        else -> null
//    }
//
//    private fun createStateAliasProperty(
//        classDecl: KSClassDeclaration,
//        stateCalled: AnnotationData,
//    ): PropertySpec {
//        val className = classDecl.toClassName()
//        val messageName = stateCalled.name.replaceFirstChar { it.uppercase() }
//
//        // Add: val <name> = <Class>::class
//        val type = classDecl.toStarParameterizedKClassType()
//        val classValSpec = PropertySpec.builder(messageName, type)
//            .initializer("$className::class")
//            .build()
//
//        return classValSpec
//    }
//
//
//
//    private fun KSClassDeclaration.toStarParameterizedKClassType(): TypeName {
//        val classType = this.toClassName()
//        val typeArguments = this.typeParameters.map { STAR }
//        return KClass::class.asClassName().parameterizedBy(
//            if (typeArguments.isEmpty()) classType else classType.parameterizedBy(typeArguments)
//        )
//    }
//
//
//}
