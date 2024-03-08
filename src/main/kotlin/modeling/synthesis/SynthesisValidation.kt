package modeling.synthesis

import Builder
import Identifiable
import utils.ErrorHandling
import utils.csv.CsvReader
import utils.csv.DefaultCsvReader
import utils.csv.Row

interface ValidateStep {

    val delegate: SynthesisStep

    @Suppress("TooGenericExceptionCaught")
    fun validateStep(onError: () -> Unit): Boolean =
        try {
            ErrorHandling.WARN_COLLECT.handle(runnable = {
                delegate.execute()
            }) {"${delegate.javaClass.simpleName} '${delegate.name}' is invalid!"}
            true

        } catch (e: Exception) {
            println("   ${e.message}")
            onError()
            false
        }

    fun validate() = validateStep {  }



}

abstract class ValidateResourceStep(
    protected val resource: Resource<*>
): ValidateStep {
    protected fun getMetadata(resource: Resource<*>): Pair<String, String> {
        val name = ErrorHandling.WARNING.handle(runnable = { resource.name })
        { "Could not obtain name of resource in step ${delegate.name}!" }
            ?: "${this.javaClass.simpleName}-Dummy"

        val source = ErrorHandling.WARNING.handle(runnable = { resource.source })
        { "Could not obtain source of resource in step ${delegate.name}!" }
            ?: "${this.javaClass.simpleName}.validate()"

        return name to source
    }
}

open class ValidateRepositoryInitialization<E>(
    override val delegate: SynthesisStep,
    protected val repository: LateInitRepository<E>,
    resource: Resource<*> = repository
): ValidateResourceStep(resource) where E: Identifiable<E> {

    private fun repairInitState() = when(repository.state) {
        RepositoryState.UNINITIALIZED ->  {
            val metadata = getMetadata(resource)

            repository.initialize(
                SequenceResource(metadata.first, metadata.second, emptySequence())
            )
        }
        RepositoryState.PREPARING -> (repository as BuilderRepository<*,*>).build()
        RepositoryState.FINISHED -> {
             /* State is already INITIALIZED. */
        }
    }

    override fun validate(): Boolean = validateStep { repairInitState() }

}

open class ValidateRepositoryPreparation<B, E>(
    override val delegate: SynthesisStep,
    protected val repository: BuilderRepository<B, E>,
    resource: Resource<*> = repository
): ValidateResourceStep(resource) where B: Builder<E>, E: Identifiable<E> {

    override fun validate() = validateStep { repairBuildState() }

    private fun repairBuildState() = when(repository.state) {
        RepositoryState.UNINITIALIZED -> {
            val metadata = getMetadata(resource)

            repository.prepare(
                SequenceResource(metadata.first, metadata.second, emptySequence())
            )
        }
        RepositoryState.PREPARING -> { /* State is already BUILDING */}
        RepositoryState.FINISHED -> {
            println("WARNING: Could not repair state of repository '${repository.name}'" +
                    " after step '${delegate.name}': expected PREPARED but is FINISHED!")
            println("    Subsequent validation errors concerning repository '${repository.name}' " +
                    "might be resolved by fixing this step.")
        }
    }

}

open class ValidateCsvPrepare<B,E>(
    override val delegate: PrepareCsvStep<B,E>,
    repository: BuilderRepository<B,E>,
    protected val csv: CsvResource<*>,
): ValidateRepositoryPreparation<B, E>(delegate, repository, csv) where B: Builder<E>, E: Identifiable<E> {
    override fun validate(): Boolean {
        return super.validate() && ValidateCsvMetadata(delegate, csv).validate()
    }
}


open class ValidateCsvInitialize<E>(
    override val delegate: InitializeCsvStep<E>,
    repository: LateInitRepository<E>,
    protected val csv: CsvResource<*>,
): ValidateRepositoryInitialization<E>(delegate, repository, csv) where E: Identifiable<E> {
    override fun validate(): Boolean {
        return super.validate() && ValidateCsvMetadata(delegate, csv).validate()
    }
}

class ValidateCsvMetadata<E>(
    private val step: SynthesisStep,
    private val csv: CsvResource<E>
): Row, CsvReader {
    companion object {
        private val testStrings = listOf("1", "1u", "1.0", "1.0f", "true", "")
    }
    private lateinit var reader: CsvReader
    private var isValid = true


    //Row Attributes
    override val index = 0

    // CsvReader Attributes
    override val columns
        get() = reader.columns
    override val rowCount = 1
    override val source
        get() = "ValidationRow for " + reader.source
    override fun toString() = "${source}[1]:${columns}"
    override fun hasColumn(column: String) = true
    //Assume all columns exist external module try to access them to trigger error report in case of missing column

    fun validate(): Boolean {
        if (!csv.file.exists()) {
            println("Error: Csv resource file '${csv.file}' used in step '${step.name}' does not exist!")
            isValid = false
        } else if (!csv.file.canRead()) {
            println("Error: Cannot read '${csv.file}' used in step '${step.name}': missing read permission!")
            isValid = false
        }

        reader = DefaultCsvReader(csv.file)

        ErrorHandling.ERROR.handle {
            ErrorHandling.mute()
            csv.parser.parse(this).toList()
            ErrorHandling.unmute()
        }


        return isValid
    }


    override fun rows(): Sequence<Row> {
        return sequenceOf(this)
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    override operator fun <T> invoke(column: String, converter: (String) -> T): T {
        validateColumnExists(column)

        for (test in testStrings) {
            try {
                return converter(test)
            } catch (e: Exception) { /**/ }
        }

        isValid = false
        println("WARNING: Value of column '$column' of $csv could not be mocked " +
                "for parsing! Validation of columns in step '${step.name}' may be incomplete!")
        error("Could not mock column '$column'!")

    }

    private fun validateColumnExists(column: String) {
        if (!reader.columns.contains(column)) {
            println("ERROR: Invalid column '$column' accessed in step '${step.name}' " +
                    "does not exist in the source csv file: ${reader.source}!")
            isValid = false
        }
    }


}
