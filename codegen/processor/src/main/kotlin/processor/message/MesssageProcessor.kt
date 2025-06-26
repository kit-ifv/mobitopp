package processor.message
//
//import GroupingAnnotationProcessor
//import GroupingProcessor
//import MessageCalled
//import com.google.devtools.ksp.KspExperimental
//import com.google.devtools.ksp.containingFile
//import com.google.devtools.ksp.getAnnotationsByType
//import com.google.devtools.ksp.getConstructors
//import com.google.devtools.ksp.processing.CodeGenerator
//import com.google.devtools.ksp.processing.Dependencies
//import com.google.devtools.ksp.processing.KSPLogger
//import com.google.devtools.ksp.processing.Resolver
//import com.google.devtools.ksp.symbol.KSAnnotated
//import com.google.devtools.ksp.symbol.KSAnnotation
//import com.google.devtools.ksp.symbol.KSClassDeclaration
//import com.google.devtools.ksp.symbol.KSFile
//import com.google.devtools.ksp.symbol.KSFunctionDeclaration
//import com.google.devtools.ksp.symbol.KSType
//import com.google.devtools.ksp.symbol.KSTypeParameter
//import com.squareup.kotlinpoet.ClassName
//import com.squareup.kotlinpoet.FileSpec
//import com.squareup.kotlinpoet.FunSpec
//import com.squareup.kotlinpoet.ParameterSpec
//import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
//import com.squareup.kotlinpoet.PropertySpec
//import com.squareup.kotlinpoet.STAR
//import com.squareup.kotlinpoet.TypeName
//import com.squareup.kotlinpoet.TypeVariableName
//import com.squareup.kotlinpoet.asClassName
//import com.squareup.kotlinpoet.ksp.toTypeName
//import util.parameter
//import util.toClassName
//import kotlin.reflect.KClass
//
//data class MessageAnnotationData(val name: String, val scope: ClassName)
//private val ANY = ClassName("kotlin", "Any")
//
//class MessageProcessor(
//    override val logger: KSPLogger,
//    private val codeGenerator: CodeGenerator,
//
//): GroupingProcessor<MessageCalled, MessageAnnotationData, KSFile> {
//
//    override val annotationType: KClass<MessageCalled>
//        get() = MessageCalled::class
//
//
//    override fun castAnnotations(annotation: KSAnnotation) =
//        MessageAnnotationData(
//            name = annotation.parameter("name"),
//            scope = annotation.parameter<KSType>("scope").declaration.toClassName()
//        )
//
//    override fun groupKey(
//        symbol: KSAnnotated,
//        annotations: List<MessageAnnotationData>
//    ): KSFile? = symbol.containingFile
//
//    override fun processAnnotatedSymbolGroup(
//        groupKey: KSFile,
//        symbolGroup: List<KSAnnotated>,
//        annotations: Map<KSAnnotated, List<MessageAnnotationData>>,
//        resolver: Resolver
//    ): List<KSAnnotated> {
//
//        val filePackage = groupKey.packageName.asString()
//        val originalFileName = groupKey.fileName.removeSuffix(".kt")
//        val generatedFileName = "${originalFileName}GeneratedMessages"
//        val fileSpecBuilder = FileSpec.builder(filePackage, generatedFileName)
//
//        logger.warn("messages of file $originalFileName in $filePackage")
//
//        for (symbol in symbolGroup) {
//            try {
//                val classDecl = symbol as? KSClassDeclaration ?: continue
//                val message = annotations[symbol]?.firstOrNull() ?: continue
//
//                logger.warn("  process ${symbol.qualifiedName?.asString()}")
//                logger.warn("  process message $message")
//
//
//                fileSpecBuilder.addProperty(
//                    createMessageAliasProperty(classDecl, message)
//                )
//
//                logger.warn("    resolve message type params ${classDecl.typeParameters}")
//
//                // --- Handle type parameters of message class ---
//                val messageTypeParams = classDecl.typeParameters.map {
//                    val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
//                    val name = "${it.name.asString()}_msg"
//                    if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
//                }
//                logger.warn("      > result $messageTypeParams")
//
//
//                // --- Handle type parameters of scope class (if any) ---
//                val scopeKClass = message.scope.takeIf { it != ANY }
//                val scopeDeclaration = scopeKClass?.let { scopeClassName ->
//                    resolver.getClassDeclarationByName(resolver.getKSNameFromString(scopeClassName.canonicalName))
//                }
//
//                logger.warn("    resolve scope type params ${scopeDeclaration?.typeParameters}")
//
//                val scopeTypeParams = scopeDeclaration?.also {
//                    fileSpecBuilder.addImport(it.packageName.asString(), it.simpleName.asString())
//                }?.typeParameters?.map {
//                    val bounds = it.bounds.map { b -> b.resolve().toTypeName() }.toList()
//                    val name = "${it.name.asString()}_scope"
//                    if (bounds.isNotEmpty()) TypeVariableName(name, bounds) else TypeVariableName(name)
//                } ?: emptyList()
//
//                logger.warn("      > result $scopeTypeParams")
//
//
//                val functionName = message.name.replaceFirstChar { it.lowercase() }
//                val className = classDecl.toClassName()
//                val returnType = if (messageTypeParams.isEmpty()) {
//                    className
//                } else {
//                    className.parameterizedBy(messageTypeParams)
//                }
//
//                logger.warn("    return type $returnType")
//
//                for (constructor in classDecl.getConstructors()) {
//                    val funBuilder = createInitializeFunction(
//                        constructor,
//                        messageTypeParams,
//                        className,
//                        functionName,
//                        returnType,
//                        scopeDeclaration,
//                        scopeTypeParams
//                    )
//
//                    logger.warn("    add function ${funBuilder.build().name}: ${funBuilder.build()}")
//
//                    fileSpecBuilder.addFunction(funBuilder.build())
//
//                }
//            } catch (e: Exception) {
//                logger.warn(e.message ?: "error while processing message $symbol")
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
//        messageTypeParams: List<TypeVariableName>,
//        className: ClassName,
//        functionName: String,
//        returnType: TypeName,
//        scopeDeclaration: KSClassDeclaration?,
//        scopeTypeParams: List<TypeVariableName>
//    ): FunSpec.Builder {
//        val allTypeParams = scopeTypeParams + messageTypeParams
//
//        logger.warn("    resolve parameters ${constructor.parameters}")
//
//        val parameters = constructor.parameters.mapIndexed { index, param ->
//            val paramName = param.name?.asString() ?: "param$index"
//
//            val typeRef = param.type
//            val paramType = try {
//                val resolved = typeRef.resolve()
//                val declaredType = resolved.declaration
//                val isTypeParam = declaredType is KSTypeParameter
//
//                if (isTypeParam) {
//                    // Use the declared type variable name as a TypeVariableName
//                    TypeVariableName(declaredType.name.asString() + "_msg")
//                } else {
//                    resolved.toTypeName()
//                }
//            } catch (e: Exception) {
//                // Fallback to TypeVariable if it's a raw type parameter
//                val simpleName = typeRef.element.toString()+"_msg"
//                TypeVariableName(simpleName)
//            }
//
//            ParameterSpec.builder(paramName, paramType).build()
//        }
//
//        logger.warn("      > resulting parameters $parameters")
//
//
//        val constructorCall = if (messageTypeParams.isNotEmpty()) {
//            val typeArgs = messageTypeParams.joinToString(", ") { it.name }
//            "$className<$typeArgs>(${parameters.joinToString { it.name }})"
//        } else {
//            "$className(${parameters.joinToString { it.name }})"
//        }
//
//        logger.warn("    constructor call $constructorCall")
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
//        logger.warn("    add receiver $scopeDeclaration")
//        scopeDeclaration?.let { declaration ->
//            val receiver = if (scopeTypeParams.isNotEmpty()) {
//                scopeDeclaration.toClassName().parameterizedBy(scopeTypeParams)
//            } else {
//                scopeDeclaration.toClassName()
//            }
//
//            logger.warn("      > resulting receiver $receiver")
//
//            funBuilder.receiver(receiver)
//        }
//
//        return funBuilder
//    }
//
//    private fun createMessageAliasProperty(
//        classDecl: KSClassDeclaration,
//        messageCalled: MessageAnnotationData,
//    ): PropertySpec {
//        val className = classDecl.toClassName()
//        val messageName = messageCalled.name.replaceFirstChar { it.uppercase() }
//
//        // Add: val <name> = <Class>::class
//        val type = classDecl.toStarParameterizedKClassType()
//        val classValSpec = PropertySpec.builder(messageName, type)
//            .initializer("%T::class", className)
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
