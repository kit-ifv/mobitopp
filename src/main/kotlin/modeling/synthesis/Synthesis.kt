package modeling.synthesis

import Builder
import Identifiable
import domain.data.CarData
import domain.data.HouseholdData
import domain.data.OpportunityData
import domain.data.PersonData
import domain.data.ZoneData
import usecases.loadZoneCsv
import usecases.zoneParser
import utils.units.DistanceUnit
import java.io.File
interface SynthesisStep<C> {
    val name: String
    fun execute(context: C)

}

class TestResource<C, E, M>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val cons: (Resource<E>) -> M,
    protected val setter: (C, M) -> Unit,
) : SynthesisStep<C> {
    override fun execute(context: C) {
        setter(context, cons(resource))
    }

} //TODO check if this approach could be used



/**
 * Init resource step adds a resource to the context
 *
 * @param C generic type of the context object
 * @param E generic type of the elements provided by the resource
 * @constructor Create empty Init resource step
 * @property name
 * @property resource
 * @property setter
 */
class InitResource<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, MutableRepository<E>) -> Unit,
) : SynthesisStep<C> {
    override fun execute(context: C) {
        setter(context, MutableRepository.from(resource))
    }

}

class InitIdResource<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, MutableIdRepository<E>) -> Unit
) : SynthesisStep<C> where E: Identifiable<E> {
    override fun execute(context: C) {
        setter(context, MutableIdRepository.from(resource))
    }

}

class FinalResource<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, Repository<E>) -> Unit
) : SynthesisStep<C> {
    override fun execute(context: C) {
        setter(context, Repository.from(resource))
    }

}

class FinalIdResource<C, E>(
    override val name: String,
    protected val resource: Resource<E>,
    protected val setter: (C, IdRepository<E>) -> Unit
) : SynthesisStep<C> where E: Identifiable<E> {
    override fun execute(context: C) {
        setter(context, IdRepository.from(resource))
    }

}


class Update<C, E>(
    override val name: String,
    protected val transformation: (E) -> E?,
    protected val getter: (C) -> MutableRepository<E>
) : SynthesisStep<C> {
    override fun execute(context: C) {
        getter(context).execute(transformation)
    }

}

//TODO close for non builder repositories
class BuildRepository<C, B, E> (
    override val name: String,
    protected val getter: (C) -> MutableRepository<B>,
    protected val setter: (C, Repository<E>) -> Unit
) : SynthesisStep<C> where B: Builder<E> {
    override fun execute(context: C) {
        val finished = getter(context).build()
        setter(context, finished)
    }

}


class Synthesis<C> where C: Context {
    // Todo : think about executing steps immediately instead of collecting all steps and then executing them
    // Todo : pro collect: validation could be performed before execution
    private val steps: MutableList<SynthesisStep<C>> = mutableListOf()

    fun addStep(step: SynthesisStep<C>) = steps.add(step)

    fun execute(context: C) {
        steps.forEach{ s -> s.execute(context)}
    }

    operator fun invoke(lambda: Synthesis<C>.() -> Unit) {
        this.apply { lambda() }
    }

    fun <E> addResource(
        name: String,
        resource: Resource<E>,
        setter: (C, MutableRepository<E>) -> Unit
    ): Synthesis<C> {
        steps.add(InitResource(name, resource, setter))
        return this
    }

    fun <E> addFinalResource(
        name: String,
        resource: Resource<E>,
        setter: (C, Repository<E>) -> Unit
    ): Synthesis<C> {
        steps.add(FinalResource(name, resource, setter))
        return this
    }

    fun <E> addIdResource(
        name: String,
        resource: Resource<E>,
        setter: (C, MutableIdRepository<E>) -> Unit
    ): Synthesis<C> where E: Identifiable<E> {
        steps.add(InitIdResource(name, resource, setter))
        return this
    }

    fun <E> addFinalIdResource(
        name: String,
        resource: Resource<E>,
        setter: (C, IdRepository<E>) -> Unit
    ): Synthesis<C> where E: Identifiable<E> {
        steps.add(FinalIdResource(name, resource, setter))
        return this
    }

    fun <E> addUpdate(
        name: String,
        transformation: (E) -> E?,
        getter: (C) -> MutableRepository<E>?
    ): Synthesis<C> {
        steps.add(Update(name, transformation, wrapGetter(name, getter)))
        return this
    }

    fun <B, E> addFinishStep(
        name: String,
        getter: (C) -> MutableRepository<B>?,
        setter: (C, Repository<E>) -> Unit
    ): Synthesis<C> where B: Builder<E> {
        steps.add(BuildRepository(name, wrapGetter(name, getter), setter))
        return this
    }

    private fun <R> wrapGetter(
        step: String,
        getter: (C) -> R?
    ): (C) -> R = {
        context ->
        requireNotNull(getter(context)) {
            "Update step [$step] is not applicable since the required resource[$getter] has not been initialized."
        }
    }

}
interface Context {
    val name: String
}

fun <C> C.synthesis(lambda: Synthesis<C>.() -> Unit) where C: Context {

    val synth = Synthesis<C>()
    return synth.lambda()
}


interface BaseContext : Context {
    var zones: IdRepository<ZoneData>?
    var households: IdRepository<HouseholdData>?
    var cars: IdRepository<CarData>?
    var persons: IdRepository<PersonData>?
    var opportunities: IdRepository<OpportunityData>?
}

class ExampleContext(override val name: String) : BaseContext {
    override var zones: IdRepository<ZoneData>? = null
    override var households: IdRepository<HouseholdData>? = null
    override var cars: IdRepository<CarData>? = null
    override var persons: IdRepository<PersonData>? = null
    override var opportunities: IdRepository<OpportunityData>? = null

}

fun main() {
    val context = ExampleContext("test").synthesis {
        loadZoneCsv(
            zoneParser(
                reliefUnit = DistanceUnit.METERS
            ),
            File(
                "\\\\ifv-fs\\Forschung\\Projekte_intern\\" +
                        "mobitopp\\Output\\logiktram_rastatt_long-term-module\\rastatt\\zone-repository\\zones.csv"
            ),
            delimiter = ";"
        )

    }

}
