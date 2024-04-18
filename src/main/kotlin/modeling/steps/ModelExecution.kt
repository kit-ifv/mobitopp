package modeling.steps

import utils.Builder
import utils.Identifiable

interface ModelStep {
    // TODO rename to ModelStep
    val name: String
    fun execute()
    fun validate(): Boolean
}

open class PrepareResourceStep<B, E, I>(
    override val name: String,
    protected val resource: Resource<B>,
    protected val repository: RepositoryBuilder<B, E, I>,
) : ModelStep where E : Identifiable<I>, B : Builder<E> {

    override fun execute() {
        repository.prepare(resource)
    }

    override fun validate() = validatePrepareResourceStep(repository, resource, this)
}

open class PrepareCsvStep<B, E, I>(
    name: String,
    protected val csv: CsvResource<B>,
    repository: RepositoryBuilder<B, E, I>,
) : PrepareResourceStep<B, E, I>(
    name = name,
    resource = csv,
    repository = repository
) where E : Identifiable<I>, B : Builder<E> {

    override fun validate() = validatePrepareCsvStep(repository, csv, this)
}

open class FilterStep<B, E, I>(
    override val name: String,
    val repository: RepositoryBuilder<B, E, I>,
    protected val predicate: (B) -> Boolean,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.reduce(name, predicate)
    }

    override fun validate() = validateFilterStep(repository, this)
}

open class UpdateStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
    protected val transformation: (B) -> B?,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.update(name, transformation)
    }

    override fun validate() = validateUpdateStep(repository, this)
}

open class UpdateAllStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
    protected val transformation: (Sequence<B>) -> Sequence<B>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.updateAll(name, transformation)
    }

    override fun validate() = validateUpdateStep(repository, this)
}

open class MergeStep<B, E, I>(
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
    protected val resource: Resource<B>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.mergeBuilders(resource)
    }

    override fun validate() = validateMergeStep(repository, resource, this)
}

open class BuildStep<B, E, I> (
    override val name: String,
    protected val repository: RepositoryBuilder<B, E, I>,
) : ModelStep where B : Builder<E>, E : Identifiable<I> {

    override fun execute() {
        repository.build()
        println("Built ${repository.name} repo: ${repository.size} elements")
    }

    override fun validate() = validateBuildStep(repository, this)
}

open class MultiStep(
    override val name: String,
    vararg steps: ModelStep,
) : ModelStep {
    private val steps = steps.toMutableList()

    fun addStep(step: ModelStep) {
        steps.add(step)
    }

    override fun execute() = steps.forEach { it.execute() }

    override fun validate() = steps.all { step ->
        step.validate().also {
            println("Step ${step.name} is " + (if (it) "valid" else "invalid") + "!")
        }
    }
}

class ModelExecution<C>(
    val context: C
) : MultiStep(context.scenarioName) where C : Context

fun <C> C.synthesis(lambda: ModelExecution<C>.() -> Unit): C where C : Context {
    println("Validate before run!")
//    val dummy = ModelExecution(this)
//    dummy.lambda()
    val isValid = true //dummy.validate()

    if (isValid) {
        println("Execute")
        this.reset()
        val synth = ModelExecution(this)
        synth.lambda()
        synth.execute()
        return this
    } else {
        error("validation failed")
    }
}
