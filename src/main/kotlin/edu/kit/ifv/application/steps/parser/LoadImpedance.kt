package edu.kit.ifv.application.steps.parser
import edu.kit.ifv.application.steps.HasModes
import edu.kit.ifv.application.steps.HasMutableImpedance
import edu.kit.ifv.application.steps.MatrixConfig
import edu.kit.ifv.application.steps.UnitConfig
import edu.kit.ifv.core.datastructure.matrix.DoubleMatrix
import edu.kit.ifv.core.modelsteps.steps.modelStep
import edu.kit.ifv.core.modelsteps.validation.validateCondition
import edu.kit.ifv.core.modelsteps.validation.validateFileReadAccess
import edu.kit.ifv.domain.shared.datastructure.matrix.MatrixImpedance
import edu.kit.ifv.domain.shared.datastructure.matrix.UnitConverter
import edu.kit.ifv.domain.shared.datastructure.matrix.ZoneMatrixCreation
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.CostMetric
import edu.kit.ifv.domain.shared.location.DistanceMetric
import edu.kit.ifv.domain.shared.location.DurationMetric
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.meters
import edu.kit.ifv.utils.codes.Decodable
import edu.kit.ifv.utils.units.Time
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.seconds

// private val IS_ERROR = false

/**
 * Loads impedance matrices (travel time, costs, distances) from configured paths.
 *
 * This step reads matrix configuration files and initializes a [MatrixImpedance] model,
 * which is then assigned to the context's [HasMutableImpedance.impedance].
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement [HasModes] and [HasMutableImpedance].
 * @param CFG The configuration type. Must implement [MatrixConfig] and [UnitConfig].
 * @param config The configuration. Provided via context.
 */
@Suppress("LongParameterList")
context(config: CFG)
fun <C, CFG> C.loadImpedance(
    travelTimeYaml: Path = config.durationMatrixConfig,
    travelCostsYaml: Path = config.costMatrixConfig,
    travelDistanceMatrix: Path = config.distanceMatrix,
    decoder: Decodable<Mode> = modes,
    matrixFactory: ZoneMatrixCreation = config.matrixCreation,
    converter: UnitConverter = UnitConverter.fromUnits(
        config.distanceUnit,
        config.currencyUnit,
        config.durationUnit,
    ),
    errorOnMissingMode: Boolean = false,
)
    where C : HasModes, C : HasMutableImpedance, CFG : MatrixConfig, CFG : UnitConfig = modelStep(
    "load impedance matrices",
    validation = listOf({ validateLoadImpedance(config, errorOnMissingMode) }),
) {
    val impedance = MatrixImpedance.loadFromPaths(
        travelTimeYamlPath = travelTimeYaml,
        travelCostsYamlPath = travelCostsYaml,
        travelDistanceMatrixPath = travelDistanceMatrix,
        decoder = decoder,
        matrixFactory = matrixFactory,
        converter = converter,
    )

    this.impedance = impedance
}

private fun HasModes.validateLoadImpedance(config: MatrixConfig, errorOnMissingMode: Boolean = false): Boolean =
    config.run {
        val fileAccess: Boolean =
            validateFileReadAccess(costMatrixConfig, fileDescription = "cost matrix config:") &&
                validateFileReadAccess(durationMatrixConfig, fileDescription = "travel time matrix config:") &&
                validateFileReadAccess(distanceMatrix, fileDescription = "distance matrix:")

        if (!fileAccess) {
            return false
        }

        val costConfig = costMatrixConfig.readText()
        val durationConfig = durationMatrixConfig.readText()

        var isValid = checkConfigKeysAreKnownModes(costConfig, path = costMatrixConfig)
        isValid = checkConfigKeysAreKnownModes(durationConfig, path = durationMatrixConfig) && isValid

        modes.values().forEach { mode ->
            val modeLabel = "$mode:"
            val errorMessage = { path: Path ->
                "Matrix config ${path.fileName} does not specify mode $mode"
            }

            isValid = validateCondition({ errorMessage(costMatrixConfig) }, errorOnMissingMode) {
                modeLabel in costConfig
            } && isValid

            isValid = validateCondition({ errorMessage(durationMatrixConfig) }, errorOnMissingMode) {
                modeLabel in durationConfig
            } && isValid
        }

        isValid || !errorOnMissingMode
    }

private fun HasModes.checkConfigKeysAreKnownModes(configText: String, path: Path): Boolean { // , modes: CodePlan<Mode>
    val unknownModeMessage = { label: String ->
        "Matrix config ${path.fileName} contains unknown mode $label. Known modes are ${modes.values()}"
    }
    val modeKeyRegex = Regex("^[A-Za-z_]+:[ \\t]*$")

    return configText.lineSequence().filter {
        it.matches(modeKeyRegex)
    }.map {
        it.trim().removeSuffix(":")
    }.map {
        validateCondition({ unknownModeMessage(it) }, true) {
            modes.decodeOrNull(it) != null
        }
    }.toList().all { it }
}

/**
 * Creates and assigns a [Teleportation] impedance model.
 *
 * This model provides zero costs and minimal fixed duration/distance for all trips.
 *
 * @receiver The simulation context which can store an impedance model.
 */
fun HasMutableImpedance.loadTeleportation() = modelStep(
    "create Teleportation impedance for Transport",
) {
    this.impedance = Teleportation()
}

class Teleportation : Impedance {

    private val costMetric: CostMetric = CostMetric { _, _ ->
        0.euros
    }
    private val durationMetric: DurationMetric = DurationMetric { _, _ ->
        1.seconds
    }
    private val distanceMetric: DistanceMetric = DistanceMetric { _, _ ->
        1.meters
    }
    override fun costMetric(mode: Mode, time: Time): CostMetric = costMetric

    override fun distanceMetric(mode: Mode): DistanceMetric = distanceMetric

    override fun durationMetric(mode: Mode, time: Time): DurationMetric = durationMetric

    override fun distanceArray(mode: Mode): DoubleMatrix {
        TODO("Not yet implemented")
    }

    override fun durationArray(mode: Mode): DoubleMatrix {
        TODO("Not yet implemented")
    }

    override fun costArray(mode: Mode): DoubleMatrix {
        TODO("Not yet implemented")
    }
}

private const val SHOULD_NOT_BE_CALLED = "Should not be called!"

// @Deprecated("dummy impedance should no longer be used, try using Teleportation")
// val dummyImpedance = object : Impedance {
//    override fun duration(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time) = 5.minutes
//    override fun cost(from: HasZoneId, to: HasZoneId, mode: Mode, time: Time) = 5.euros
//    override fun distance(from: HasZoneId, to: HasZoneId, mode: Mode) = 5.kilometers
//    override fun costMetric(mode: Mode, time: Time): CostMetric = error(SHOULD_NOT_BE_CALLED)
//    override fun distanceMetric(mode: Mode): DistanceMetric = error(SHOULD_NOT_BE_CALLED)
//    override fun durationMetric(mode: Mode, time: Time): DurationMetric = error(SHOULD_NOT_BE_CALLED)
// }
