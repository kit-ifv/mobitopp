package edu.kit.ifv

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.validate
import edu.kit.ifv.processor.builder.BuilderProcessor
import edu.kit.ifv.processor.mutable.MutableProcessor
import edu.kit.ifv.processor.state.TestMessageProcessor
import edu.kit.ifv.processor.state.TestStateProcessor
import edu.kit.ifv.utils.groupByIgnoringNullKey
import kotlin.reflect.KClass

private var roundCounter: Int = 0

class ProcessorDispatcher(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    options: Map<String, String>,
    private val environment: SymbolProcessorEnvironment
) : SymbolProcessor {

    init {
        logger.info("edu.kit.ifv.Processor options: $options")
        logger.info("Api version: ${environment.apiVersion}")
        logger.info("Kotlin version: ${environment.kotlinVersion}")
        logger.info("Compiler version: ${environment.compilerVersion}")
    }

    private val processors = listOf<Processor<*, *>>(
        BuilderProcessor(codeGenerator, logger, options),
        MutableProcessor(logger, codeGenerator),
        TestStateProcessor(logger, codeGenerator),
        TestMessageProcessor(logger, codeGenerator),
    )


    override fun process(resolver: Resolver): List<KSAnnotated> {
        roundCounter++
        logger.info("Processing round: $roundCounter")

        val deferred = mutableListOf<KSAnnotated>()

        processors.forEach { processor ->
            val symbols = resolver.getSymbolsWithAnnotation(processor.annotationName, inDepth = true).toList()
            logger.info(" - Found ${symbols.size} symbols annotated by @${processor.annotationName}: $symbols")

            if (symbols.isEmpty()) {
                deferred += emptyList()
            }

            var newDeferred = processor.processAnnotatedSymbolList(symbols, resolver)
            if (newDeferred.isNotEmpty()) {
                logger.info(" - RETRY processor: ${processor::class.simpleName}")
                newDeferred = processor.processAnnotatedSymbolList(newDeferred, resolver)
            }
            deferred += newDeferred
        }

        return deferred
    }

}

interface Processor<A, D> where A: Annotation {

    val logger: KSPLogger

    val annotationType: KClass<A>
    val annotationName: String
        get() = annotationType.simpleName!!

    fun castAnnotations(symbol: KSAnnotated): List<D> = symbol.annotations.mapNotNull { castAnnotations(it) }.toList()
    fun castAnnotations(annotation: KSAnnotation): D? =
        throw UnsupportedOperationException("Cast annotation is not provided!")

    fun processAnnotatedSymbolList(symbols: List<KSAnnotated>, resolver: Resolver): List<KSAnnotated>

}

interface SuccessiveProcessor<A,D>: Processor<A,D> where  A: Annotation {

    override fun processAnnotatedSymbolList(symbols: List<KSAnnotated>, resolver: Resolver): List<KSAnnotated> {
        val deferred = mutableListOf<KSAnnotated>()

        symbols.forEach { symbol ->

            val annotations = mutableListOf<D>()
            try {
                if(!symbol.validate()) {
                    deferred.add(symbol)
                    logger.info(" - Deferred $symbol as it is not valid !!")
                } else {
                    annotations += castAnnotations(symbol)
                    deferred += processAnnotatedSymbol(symbol, annotations, resolver)
                    logger.info(
                        "Processed $symbol, ${symbol.annotations.toList()}"
                    )
                }

            } catch (exception: Exception) {
                logger.warn(
                    "Error while processing $symbol with annotations: $annotations: \n${exception.message}"
                )
            }

        }

        return deferred
    }

    fun processAnnotatedSymbol(symbol: KSAnnotated, annotations: List<D>, resolver: Resolver) : List<KSAnnotated>

}

interface GroupingProcessor<A,D,G>: Processor<A,D> where  A: Annotation {

    fun groupKey(symbol: KSAnnotated, annotations: List<D>): G?

    override fun processAnnotatedSymbolList(symbols: List<KSAnnotated>, resolver: Resolver): List<KSAnnotated> {
        val result = mutableListOf<KSAnnotated>()

        val annotationsBySymbol = symbols.associateWith { castAnnotations(it) }

        symbols.groupByIgnoringNullKey {
            groupKey(it, annotationsBySymbol[it]!!)
        }.forEach { (key, groupedSymbols) ->

            val annotationsOfGroup = groupedSymbols.associateWith { annotationsBySymbol[it]!! }
            try {
                result += processAnnotatedSymbolGroup(key, groupedSymbols, annotationsOfGroup, resolver)
                logger.info(
                    " - Processed group ${key.toString()}:\n  $groupedSymbols\nwith annotations: \n  $annotationsOfGroup"
                )

            } catch (exception: Exception) {
                logger.warn(
                    "Error while processing group $key:\n" +
                            "  $groupedSymbols\n" +
                            "with annotations:\n" +
                            "  $annotationsOfGroup\n" +
                            "Message: ${exception.message}"
                )
            }

        }

        return result
    }

    fun processAnnotatedSymbolGroup(
        groupKey: G,
        symbolGroup: List<KSAnnotated>,
        annotations: Map<KSAnnotated, List<D>>,
        resolver: Resolver
    ) : List<KSAnnotated>

}

interface AnnotationProcessor<A: Annotation>: Processor<A, A>

interface SuccessiveAnnotationProcessor<A: Annotation>: SuccessiveProcessor<A, A>

interface GroupingAnnotationProcessor<A: Annotation, G>: GroupingProcessor<A, A, G>

