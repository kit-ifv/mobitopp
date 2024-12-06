import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import processor.builder.BuilderProcessor

class ProcessorProvider : SymbolProcessorProvider {
    /**
     * Called by Kotlin Symbol Processing to create the processor.
     */
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ProcessorDispatcher(environment.codeGenerator, environment.logger, environment.options)
        //return BuilderProcessor(environment.codeGenerator, environment.logger, environment.options)
    }
}