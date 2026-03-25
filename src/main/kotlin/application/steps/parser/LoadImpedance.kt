package application.steps.parser

import core.modelsteps.ModelStep
import core.modelsteps.Warning
import core.modelsteps.validateCondition
import core.modelsteps.validateFileReadAccess
import core.modelsteps.validateScope
import domain.shared.datastructure.matrix.KeyBasedMatrixCreation
import domain.shared.datastructure.matrix.MatrixMetrics
import domain.shared.datastructure.matrix.UnitConverter
import domain.shared.datastructure.matrix.ZoneMatrixCreation
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Metrics
import domain.shared.location.attributes.HasZoneID
import domain.simulation.config.DemandSimContext
import edu.kit.ifv.units.CurrencyUnit
import edu.kit.ifv.units.DistanceUnit
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.meters
import utils.units.Time
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Suppress("LongParameterList")
fun DemandSimContext.loadImpedance(
    costMatrixConfig: Path,
    durationMatrixConfig: Path,
    distanceMatrix: Path,
    distanceUnit: DistanceUnit? = null,
    currencyUnit: CurrencyUnit? = null,
    durationUnit: DurationUnit? = null,
    matrixCreator: ZoneMatrixCreation = KeyBasedMatrixCreation
) = runStep {
    LoadImpedanceStep(
        costMatrixConfig,
        durationMatrixConfig,
        distanceMatrix,
        distanceUnit,
        currencyUnit,
        durationUnit,
        matrixCreator,
        this,
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
    private val matrixCreator: ZoneMatrixCreation,
    private val context: DemandSimContext,
) : ModelStep {
    override val name: String = "Load matrix impedance from yaml"

    override fun execute() {
        val converter = UnitConverter.fromUnits(
            distanceUnit ?: DistanceUnit.KILOMETERS,
            currencyUnit ?: CurrencyUnit.EUROS,
            durationUnit ?: DurationUnit.MINUTES
        )
        val impedance = MatrixMetrics.loadFromPaths(
            travelTimeYamlPath = durationMatrixConfig,
            travelCostsYamlPath = costMatrixConfig,
            travelDistanceMatrixPath = distanceMatrix,
            decoder = context.modes,
            matrixFactory = matrixCreator,
            converter = converter,
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

fun DemandSimContext.loadTeleportation() = runStep {
    LoadTeleportation(this)
}

class LoadTeleportation(
    private val context: DemandSimContext,
) : ModelStep {
    override val name: String = "Teleportation as Transport"

    override fun execute() {
        context.impedance.value = Teleportation()
    }

    override fun verifyInput(): Warning? {
        return null
    }

    override fun mockBehavior(): Warning? = validateScope("Moeck impedance data") {
        context.impedance.value = dummyImpedance
    }
}

class Teleportation : Metrics {

    private val costMetric: CostMetric = CostMetric { _, _ ->
        0.euros
    }
    private val durationMetric: DurationMetric = DurationMetric { _, _ ->
        1.seconds
    }
    private val distancMetric: DistanceMetric = DistanceMetric { _, _ ->
        1.meters
    }
    override fun costMetric(mode: Mode, time: Time): CostMetric = costMetric

    override fun distanceMetric(mode: Mode): DistanceMetric = distancMetric

    override fun durationMetric(
        mode: Mode,
        time: Time
    ): DurationMetric = durationMetric
}

private const val SHOULD_NOT_BE_CALLED = "Should not be called!"
val dummyImpedance = object : Metrics {
    override fun duration(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time) = 5.minutes
    override fun cost(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time) = 5.euros
    override fun distance(from: HasZoneID, to: HasZoneID, mode: Mode) = 5.kilometers
    override fun costMetric(mode: Mode, time: Time): CostMetric = error(SHOULD_NOT_BE_CALLED)
    override fun distanceMetric(mode: Mode): DistanceMetric = error(SHOULD_NOT_BE_CALLED)
    override fun durationMetric(mode: Mode, time: Time): DurationMetric = error(SHOULD_NOT_BE_CALLED)
}
