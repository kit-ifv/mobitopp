package modeling.steps

import Builder
import Identifiable

interface ModelStep {
    //TODO rename to ModelStep
    val name: String
    fun execute()
    fun validate(): Boolean
}


open class PrepareResourceStep<B, E>(
    override val name: String,
    protected val resource: Resource<B>,
    protected val repository: BuilderRepository<B, E>,
) : ModelStep where E:Identifiable<E>, B: Builder<E> {

    override fun execute() {
        repository.prepare(resource)
    }

    override fun validate() = validatePrepareResourceStep(repository, resource, this)

}

open class PrepareCsvStep<B, E>(
    name: String,
    protected val csv: CsvResource<B>,
    repository: BuilderRepository<B, E>,
): PrepareResourceStep<B, E>(name, csv, repository) where E:Identifiable<E>, B: Builder<E>{

    override fun validate() = validatePrepareCsvStep(repository, csv, this)
}

open class InitializeResourceStep<E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val repository: LateInitRepository<E>
) : ModelStep where E: Identifiable<E> {

    override fun execute() {
        repository.initialize(resource)
    }

    override fun validate() = validateInitializeResourceStep(repository, resource, this)
}

class InitializeCsvStep<E>(
    name: String,
    protected val csv: CsvResource<E>,
    repository: LateInitRepository<E>
): InitializeResourceStep<E>(name, csv, repository) where E: Identifiable<E> {

    override fun validate() = validateInitializeCsvStep(repository, csv, this)
}

class FilterStep<B, E>(
    override val name: String,
    val repository: BuilderRepository<B, E>,
    val predicate: (B) -> Boolean,
) : ModelStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.reduce(name, predicate)
    }

    override fun validate() = validateFilterStep(repository, this)

}

class UpdateStep<B, E>(
    override val name: String,
    protected val repository: BuilderRepository<B, E>,
    protected val transformation: (B) -> B?,
) : ModelStep where B: Builder<E>, E: Identifiable<E> {

    override fun execute() {
        repository.update(name, transformation)
    }

    override fun validate() = validateUpdateStep(repository, this)

}

class BuildStep<B, E> (
    override val name: String,
    val repository: BuilderRepository<B, E>,
) : ModelStep where B: Builder<E>, E: Identifiable<E>{

    override fun execute() {
        repository.build()
    }

    override fun validate() = validateBuildStep(repository, this)
}

open class MultiStep(
    override val name: String,
    vararg steps: ModelStep,
): ModelStep {
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

//TODO add merge and mergeBuilders step

