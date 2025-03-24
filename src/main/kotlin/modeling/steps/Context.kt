package modeling.steps

import domain.enums.Mode
import domain.events.PersonBehavior
import domain.location.Metrics
import units.CurrencyUnit
import units.DistanceUnit
import utils.CodePlan
import utils.units.AbsoluteTime
import java.io.File
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * A Context holds all data required when executing mobiTopp.
 * This is the minimum interface that all project contexts must implement.
 * Think carefully about what you put in here!
 */
interface Context {
    val execMode: ExecutionMode

    val scenarioName: String

    // TODO question: Is demand folder actually part of the minimal context?
    val demandFolder: File
    val simulationSeed: Long
    val modes: CodePlan<Mode>

    // TODO question: Is LateInit actually the minimal context, what if I already have an impedance at initialization?
    val impedance: LateInit<Metrics>

    val timeUnit: DurationUnit
    val costUnit: CurrencyUnit
    val distanceUnit: DistanceUnit

    fun runStep(createStep: () -> ModelStep) = runStepObject(createStep())

    fun runStepObject(step: ModelStep) {
        step.run(execMode)
    }
}

interface SimulationContext : Context {
    val simulationStart: AbsoluteTime
    val simulationEnd: AbsoluteTime
    val timeStep: Duration

    val behavior: LateInit<PersonBehavior>
}

class LateInit<T>( // TODO can we get rid of lateinit? after validation execMode refactoring?
    val name: String,
) {
    private var _value: T? = null
    var value: T
        get() = checkNotNull(_value) {
            "$name has not yet been initialized!"
        }
        set(value) {
            if (_value != null) {
                println("Warning: late init filed '$name' already exists and is replaced!")
            }
            this._value = value
        }

    val isSet: Boolean
        get() = _value != null
}
