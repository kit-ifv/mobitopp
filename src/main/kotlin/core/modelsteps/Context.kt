package core.modelsteps

import application.config.ShortTermConfig
import utils.report.ReportBuilder

/**
 * A Context holds all data required when executing mobiTopp.
 * This is the minimum interface that all project contexts must implement.
 * Think carefully about what you put in here!
 */
@Suppress("ComplexInterface")
interface Context {
    val execMode: ExecutionMode
    val scenarioName: String
    val report: ReportBuilder


//    val dataFolder: Path
//    val zoneFolder: Path
//        get() = Path("data/zone-repository")
//    val simulationSeed: Long
//
//    val resultDir: Path
//
//    val simulationStart: AbsoluteTime
//    val simulationEnd: AbsoluteTime
//    val timeStep: Duration
//
//    val timeUnit: DurationUnit
//    val costUnit: CurrencyUnit
//    val distanceUnit: DistanceUnit

//    fun runStep(createStep: () -> ModelStep) = runStepObject(createStep())
//
//    fun runMultipleSteps(createStep: () -> List<ModelStep>) = createStep().forEach {
//        runStepObject(it)
//    }
//
//    fun runStepObject(step: ModelStep) {
//        step.run(execMode)
//    }

}

interface ShortTermContext: Context {
    val config: ShortTermConfig<*>
}

fun Context.initReport() = ReportBuilder(
    if (execMode.isValidate) "Validation report for: $scenarioName"
    else "Execute report for: $scenarioName"
)

typealias Check<C> = C.() -> Boolean
typealias Validation<C> = List<Check<C>>


/**
 * Simple interface with a clone function.
 */
interface Cloneable<T : Cloneable<T>> {
    /**
     * @return a shallow copy of this.
     */
    fun clone(): T
}

class ExecutionMode { // Do not make class open!
    private var validateMode: Boolean = false

    val isValidate: Boolean
        get() = validateMode

    val isExecute: Boolean
        get() = !validateMode

    fun setValidate() {
        validateMode = true
    }

    fun setExecute() {
        validateMode = false
    }
}