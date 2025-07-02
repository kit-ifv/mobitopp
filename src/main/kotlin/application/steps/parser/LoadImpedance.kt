package application.steps.parser

import core.modelsteps.ModelStep
import core.modelsteps.Warning
import core.modelsteps.validateCondition
import core.modelsteps.validateFileReadAccess
import core.modelsteps.validateScope
import domain.shared.datastructure.matrix.InternalMatrixLookup
import domain.shared.datastructure.matrix.YamlMatrixLookupMetrics
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Location
import domain.shared.location.Metrics
import domain.simulation.config.DemandSimContext
import units.CurrencyUnit
import units.DistanceUnit
import units.euros
import units.kilometers
import utils.units.Time
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

@Suppress("LongParameterList")
fun DemandSimContext.loadImpedance(
    costMatrixConfig: Path,
    durationMatrixConfig: Path,
    distanceMatrix: Path,
    distanceUnit: DistanceUnit? = null,
    currencyUnit: CurrencyUnit? = null,
    durationUnit: DurationUnit? = null,
    betterFormatRoot: InternalMatrixLookup? = null
) = runStep {
    LoadImpedanceStep(
        costMatrixConfig,
        durationMatrixConfig,
        distanceMatrix,
        distanceUnit,
        currencyUnit,
        durationUnit,
        this,
        betterFormatRoot
    )
}

@Suppress("LongParameterList")
private class LoadImpedanceStep(
    private val costMatrixConfig: Path,
    private val durationMatrixConfig: Path,
    private val distanceMatrix: Path,
    private val distanceUnit: DistanceUnit? = null,
    private val currencyUnit: CurrencyUnit? = null,
    private val durationUnit: DurationUnit? = null,
    private val context: DemandSimContext,
    private val betterFormatRoot: InternalMatrixLookup?
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
            betterFormat = betterFormatRoot
        )

        context.impedance.value = (impedance)
    }

    override fun verifyInput(): Warning? = validateScope("Validate input data for impedance model:") {
        validateFileReadAccess(costMatrixConfig, fileDescription = "cost matrix config:")
        validateFileReadAccess(durationMatrixConfig, fileDescription = "travel time matrix config:")
        validateFileReadAccess(distanceMatrix, fileDescription = "distance matrix:")

        if (subWarnings.isEmpty()) {
            val costConfig = costMatrixConfig.readText()
            val durationConfig = durationMatrixConfig.readText()

            context.modes.values().forEach { mode ->
                val modeLabel = "$mode:"
                val errorMessage = { path: Path ->
                    "Matrix config ${path.fileName} does not specify mode $mode"
                }

                validateCondition(errorMessage(costMatrixConfig), true) {
                    modeLabel in costConfig
                }

                validateCondition(errorMessage(durationMatrixConfig), true) {
                    modeLabel in durationConfig
                }
            }
        }
    }

    override fun mockBehavior(): Warning? = validateScope("Mock impedance data") {
        context.impedance.value = dummyImpedance
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
