package application.synthesis

import Builder
import Identifiable

interface SynthesisStep<C> {
    val name: String
    fun execute(context: C)

}

class InitResourceStep<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, MutableRepository<E>) -> Unit
) : SynthesisStep<C> {
    override fun execute(context: C) {
        setter(context, MutableRepository.from(resource))
    }

}

class InitIdResourceStep<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, MutableIdRepository<E>) -> Unit
) : SynthesisStep<C> where E: Identifiable {
    override fun execute(context: C) {
        setter(context, MutableIdRepository.from(resource))
    }

}

class FinalResourceStep<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, Repository<E>) -> Unit
) : SynthesisStep<C> {
    override fun execute(context: C) {
        setter(context, Repository.from(resource))
    }

}

class FinalIdResourceStep<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, IdRepository<E>) -> Unit
) : SynthesisStep<C> where E: Identifiable {
    override fun execute(context: C) {
        setter(context, IdRepository.from(resource))
    }

}


class UpdateStep<C, E>(
    override val name: String,
    protected val transformation: Transformation<E>,
    protected val getter: (C) -> MutableRepository<E>
) : SynthesisStep<C> {
    override fun execute(context: C) {
        getter(context).apply(transformation)
    }

}

class FinishStep<C, B, E> (
    override val name: String,
    protected val getter: (C) -> MutableRepository<B>,
    protected val setter: (C, Repository<E>) -> Unit
) : SynthesisStep<C> where B: Builder<E> {
    override fun execute(context: C) {
        val finished = getter(context).finish()
        setter(context, finished)
    }

}



class Synthesis<C> {
    private val steps: MutableList<SynthesisStep<C>> = mutableListOf()

    fun execute(context: C) {
        steps.forEach{ s -> s.execute(context)}
    }

    fun <E> addResource(
        name: String,
        resource: Resource<E>,
        setter: (C, MutableRepository<E>) -> Unit
    ): Synthesis<C> {
        steps.add(InitResourceStep(name, resource, setter))
        return this
    }

    fun <E> addFinalResource(
        name: String,
        resource: Resource<E>,
        setter: (C, Repository<E>) -> Unit
    ): Synthesis<C> {
        steps.add(FinalResourceStep(name, resource, setter))
        return this
    }

    fun <E> addIdResource(
        name: String,
        resource: Resource<E>,
        setter: (C, MutableIdRepository<E>) -> Unit
    ): Synthesis<C> where E: Identifiable {
        steps.add(InitIdResourceStep(name, resource, setter))
        return this
    }

    fun <E> addFinalIdResource(
        name: String,
        resource: Resource<E>,
        setter: (C, IdRepository<E>) -> Unit
    ): Synthesis<C> where E: Identifiable {
        steps.add(FinalIdResourceStep(name, resource, setter))
        return this
    }

    fun <E> addUpdate(
        name: String,
        transformation: Transformation<E>,
        getter:  (C) -> MutableRepository<E>?
    ): Synthesis<C> {
        steps.add(UpdateStep(name, transformation, wrapGetter(name, getter)))
        return this
    }

    fun <B, E> addFinishStep(
        name: String,
        getter: (C) -> MutableRepository<B>?,
        setter: (C, Repository<E>) -> Unit
    ): Synthesis<C> where B: Builder<E> {
        steps.add(FinishStep(name, wrapGetter(name, getter), setter))
        return this
    }

    private fun <R> wrapGetter(
        step: String,
        getter: (C) -> R?
    ): (C) -> R = {
        context ->
        requireNotNull(getter(context)) {
            "Update step $step is not applicable since the required resource has not been initialized."
        }
    }

}

class ExampleContext {
    var strings: MutableRepository<String>? = null
    var ints: MutableRepository<Int>? = null
}

fun main() {
    val ctxt = ExampleContext()

    Synthesis<ExampleContext>()
        .addResource("load strings", resource = { sequenceOf("hello", "world") }) {c,r -> c.strings=r}
        .addUpdate("_", transformation = { s -> s+"_" }) {c -> c.strings}
        //.addUpdate("illegal", transformation = {i -> i+1}){ c -> c.ints}
        .execute(ctxt)

    println(ctxt.strings?.getAll())
    println(ctxt.ints?.getAll())
}


