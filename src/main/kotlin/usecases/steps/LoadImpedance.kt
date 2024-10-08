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
import modeling.validation.subValidateFileReadAccess
import modeling.validation.subWarning
import modeling.validation.validateScope
import units.CurrencyUnit
import units.DistanceUnit
import units.euros
import units.kilometers
import usecases.YamlMatrixLookupMetrics
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

    override fun validate() = validateScope(
        "Validate $name produced warnings:"
    ) {
        subValidateFileReadAccess(costMatrixConfig, fileDescription = "cost matrix config:")
        subValidateFileReadAccess(durationMatrixConfig, fileDescription = "travel time matrix config:")
        subValidateFileReadAccess(distanceMatrix, fileDescription = "distance matrix:")

        context.impedance.value = dummyImpedance

        if (subWarnings.isEmpty()) {
            val costConfig = costMatrixConfig.readText()
            val durationConfig = durationMatrixConfig.readText()

            context.modes.values().forEach { mode ->
                val modeLabel = "$mode:"
                val errorMessage = { file: File ->
                    "Matrix config ${file.name} does not specify mode $mode"
                }

                subWarning {
                    require(modeLabel in costConfig) {
                        errorMessage(costMatrixConfig)
                    }
                }

                subWarning {
                    require(modeLabel in durationConfig) {
                        errorMessage(durationMatrixConfig)
                    }
                }
            }
        }
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
