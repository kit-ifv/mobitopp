package usecases.steps

import domain.enums.Mode
import domain.location.CostMetric
import domain.location.DistanceMetric
import domain.location.DurationMetric
import domain.location.Location
import domain.location.Metrics
import modeling.steps.Context
import modeling.steps.ModelExecution
import modeling.steps.ModelStep
import modeling.steps.SimulationContext
import units.CurrencyUnit
import units.DistanceUnit
import units.euros
import units.kilometers
import usecases.YamlMatrixLookupMetrics
import utils.files.validateFileReadAccess
import utils.test
import utils.units.Time
import java.io.File
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Suppress("LongParameterList")
fun <S, C> S.loadImpedance(
    costMatrixConfig: File,
    durationMatrixConfig: File,
    distanceMatrix: File,
    distanceUnit: DistanceUnit? = null,
    currencyUnit: CurrencyUnit? = null,
    durationUnit: DurationUnit? = null,
) where S : ModelExecution<C>, C : Context, C : SimulationContext {
    addStep(
        LoadImpedanceStep(
            costMatrixConfig,
            durationMatrixConfig,
            distanceMatrix,
            distanceUnit,
            currencyUnit,
            durationUnit,
            context
        )
    )
}

@Suppress("LongParameterList")
private class LoadImpedanceStep(
    private val costMatrixConfig: File,
    private val durationMatrixConfig: File,
    private val distanceMatrix: File,
    private val distanceUnit: DistanceUnit? = null,
    private val currencyUnit: CurrencyUnit? = null,
    private val durationUnit: DurationUnit? = null,
    private val context: SimulationContext,
) : ModelStep {
    override val name: String = "Load matrix impedance from yaml"

    override fun execute() {
        val impedance = YamlMatrixLookupMetrics(
            travelCostMatrixConfig = costMatrixConfig,
            travelTimeMatrixConfig = durationMatrixConfig,
            distanceMatrix = distanceMatrix,
            distanceUnit = distanceUnit ?: context.distanceUnit,
            currencyUnit = currencyUnit ?: context.costUnit,
            durationUnit = durationUnit ?: context.timeUnit,
            modeCodes = context.modes,
            simulationStart = context.simulationStart,
            simulationEnd = context.simulationEnd,
        )

        context.impedance.value = (impedance)
    }

    override fun validate(): Boolean {
        var valid = validateFileReadAccess(costMatrixConfig, messagePrefix = "${this.name} - cost matrix config:")

        validateFileReadAccess(
            durationMatrixConfig,
            messagePrefix = "${this.name} - travel time matrix config:"
        ).also {
            valid = valid && it
        }

        validateFileReadAccess(distanceMatrix, messagePrefix = "${this.name} - distance matrix:").also {
            valid = valid && it
        }

        context.impedance.value = dummyImpedance

        if (valid) {
            val costConfig = costMatrixConfig.readText()
            val durationConfig = durationMatrixConfig.readText()
            context.modes.values().forEach { mode ->

                val errorMessage = { file: File ->
                    "Matrix config ${file.name} does not specify mode $mode"
                }

                val modeLabel = "$mode:"
                test(modeLabel in costConfig) {
                    errorMessage(costMatrixConfig)
                }.also { valid = valid && it }

                test(modeLabel in durationConfig) {
                    errorMessage(durationMatrixConfig)
                }.also { valid = valid && it }
            }
        }

        return valid
    }
}

private const val SHOULD_NOT_BE_CALLED = "Should not be called!"
val dummyImpedance = object : Metrics {
    override fun duration(from: Location, to: Location, mode: Mode, time: Time) = 5.minutes
    override fun cost(from: Location, to: Location, mode: Mode, time: Time) = 5.euros
    override fun distance(from: Location, to: Location, mode: Mode) = 5.kilometers
    override fun costMetric(mode: Mode, time: Time): CostMetric = error(SHOULD_NOT_BE_CALLED)
    override fun distanceMetric(mode: Mode): DistanceMetric = error(SHOULD_NOT_BE_CALLED)
    override fun durationMetric(mode: Mode, time: Time): DurationMetric = error(SHOULD_NOT_BE_CALLED)
}
