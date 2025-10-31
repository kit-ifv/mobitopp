package core.modelsteps

import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import utils.units.AbsoluteTime
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.DurationUnit

/**
 * A Context holds all data required when executing mobiTopp.
 * This is the minimum interface that all project contexts must implement.
 * Think carefully about what you put in here!
 */
@Suppress("ComplexInterface")
interface Context {
    val execMode: ExecutionMode

    val scenarioName: String
    val dataFolder: Path
    val zoneFolder: Path
        get() = Path("data/zone-repository")
    val simulationSeed: Long

    val resultDir: Path

    val simulationStart: AbsoluteTime
    val simulationEnd: AbsoluteTime
    val timeStep: Duration

    val timeUnit: DurationUnit
    val costUnit: CurrencyUnit
    val distanceUnit: DistanceUnit

    fun runStep(createStep: () -> ModelStep) = runStepObject(createStep())

    fun runMultipleSteps(createStep: () -> List<ModelStep>) = createStep().forEach {
        runStepObject(it)
    }

    fun runStepObject(step: ModelStep) {
        step.run(execMode)
    }
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
