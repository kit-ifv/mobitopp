package modeling.synthesis

import Builder
import Identifiable
import utils.ErrorHandling

interface ValidateStep {

    val delegate: SynthesisStep

    fun validateStep(onError: () -> Unit): Boolean =
        try {
            ErrorHandling.WARN_COLLECT.handle(runnable = {
                delegate.execute()
            }) {"${delegate.javaClass.simpleName}-Step ${delegate.name} is invalid!"}
            true

        } catch (e: Exception) {
            println("   ${e.message}")
            onError()
            false
        }

    fun validate(): Boolean

}

interface ValidateResource: ValidateStep {

    fun getMetadata(resource: Resource<*>): Pair<String, String> {
        val name = ErrorHandling.WARNING.handle(runnable = {resource.name})
            {"Could not obtain name of resource in step ${delegate.name}!"}
            ?: "${this.javaClass.simpleName}-Dummy"

        val source = ErrorHandling.WARNING.handle(runnable = {resource.source})
            {"Could not obtain source of resource in step ${delegate.name}!"}
            ?: "${this.javaClass.simpleName}.validate()"

        return name to source
    }

}

class ValidateNewResource<B,E>(
    override val delegate: NewResource<B,E>
): ValidateStep, ValidateResource where B: Builder<E>, E: Identifiable<E> {
    override fun validate(): Boolean {
        //TODO validate resource

        return validateStep {
            if (delegate.repository.state == RepositoryState.UNINITIALIZED) {

                val metadata = getMetadata(delegate.resource)


                delegate.repository.initializeBuilders(
                    SequenceResource(metadata.first, metadata.second, emptySequence())
                )
            }
        }
    }

}

class ValidateFinalResource<E>(
    override val delegate: FinalResource<E>
): ValidateStep, ValidateResource where E: Identifiable<E> {
    override fun validate(): Boolean {
        //TODO validate resource

        return validateStep {
            if (delegate.repository.state == RepositoryState.UNINITIALIZED) {

                val metadata = getMetadata(delegate.resource)

                delegate.repository.initialize(
                    SequenceResource(metadata.first, metadata.second, emptySequence())
                )

            }
        }

    }

}

class SimpleValidate(
    override val delegate: SynthesisStep
): ValidateStep {
    override fun validate() = validateStep {  }

}

class ValidateBuild<B,E>(
    override val delegate: BuildRepository<B,E>
): ValidateStep, ValidateResource where B: Builder<E>, E: Identifiable<E> {
    override fun validate(): Boolean {

        return validateStep {
            // in case
            if (delegate.repository.state == RepositoryState.UNINITIALIZED) {

                delegate.repository.initialize(
                    SequenceResource("ValidateBuild-Dummy", "ValidateBuild.validate()", emptySequence())
                )
            }
        }
    }

}