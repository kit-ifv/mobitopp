package application.steps.parser

import application.steps.HasModes
import application.steps.HasMutableImpedance
import application.steps.MatrixConfig
import application.steps.UnitConfig
import core.modelsteps.steps.modelStep
import core.modelsteps.validation.validateCondition
import core.modelsteps.validation.validateFileReadAccess
import domain.shared.datastructure.matrix.MatrixImpedance
import domain.shared.datastructure.matrix.UnitConverter
import domain.shared.enums.Mode
import domain.shared.location.CostMetric
import domain.shared.location.DistanceMetric
import domain.shared.location.DurationMetric
import domain.shared.location.Impedance
import domain.shared.location.attributes.HasZoneID
import edu.kit.ifv.units.euros
import edu.kit.ifv.units.kilometers
import edu.kit.ifv.units.meters
import utils.units.Time
import java.nio.file.Path
import kotlin.io.path.readText
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


@Suppress("LongParameterList")
context(config: CFG)
fun <C, CFG> C.loadImpedance()
where C: HasModes, C: HasMutableImpedance, CFG: MatrixConfig, CFG: UnitConfig = modelStep(
    "load impedance matrices",
    validation = listOf({ validateLoadImpedance(config) }),
) {
    val impedance = config.run {
        val converter = UnitConverter.fromUnits(distanceUnit, currencyUnit, durationUnit)

        MatrixImpedance.loadFromPaths(
            travelTimeYamlPath = durationMatrixConfig,
            travelCostsYamlPath = costMatrixConfig,
            travelDistanceMatrixPath = distanceMatrix,
            decoder = modes,
            matrixFactory = matrixCreation,
            converter = converter,
        )
    }

    this.impedance = impedance
}

private fun HasModes.validateLoadImpedance(config: MatrixConfig): Boolean = config.run {
    val fileAccess: Boolean =
        validateFileReadAccess(costMatrixConfig, fileDescription = "cost matrix config:")
            && validateFileReadAccess(durationMatrixConfig, fileDescription = "travel time matrix config:")
            && validateFileReadAccess(distanceMatrix, fileDescription = "distance matrix:")

    if (!fileAccess) {
        return false
    }

    val costConfig = costMatrixConfig.readText()
    val durationConfig = durationMatrixConfig.readText()

    var isValid = checkConfigKeysAreKnownModes(costConfig, path = costMatrixConfig)
            && checkConfigKeysAreKnownModes(durationConfig, path = durationMatrixConfig)

    modes.values().forEach { mode ->
        val modeLabel = "$mode:"
        val errorMessage = { path: Path ->
            "Matrix config ${path.fileName} does not specify mode $mode"
        }

        isValid = isValid && validateCondition({ errorMessage(costMatrixConfig) }, true) {
            modeLabel in costConfig
        } &&  validateCondition({ errorMessage(durationMatrixConfig) }, true) {
            modeLabel in durationConfig
        }
    }

    isValid
}

private fun HasModes.checkConfigKeysAreKnownModes(configText: String, path: Path): Boolean { //, modes: CodePlan<Mode>
    val unknownModeMessage = { label: String ->
        "Matrix config ${path.fileName} contains unknown mode $label. Known modes are ${modes.values()}"
    }
    val modeKeyRegex = Regex("^[A-Za-z_]+:[ \\t]*$")

    return configText.lineSequence().filter {
        it.matches(modeKeyRegex)
    }.map {
        it.trim().removeSuffix(":")
    }.all {
        validateCondition({ unknownModeMessage(it) }, true) {
            modes.decodeOrNull(it) != null
        }
    }
}

fun HasMutableImpedance.loadTeleportation() = modelStep(
    "create Teleportation impedance for Transport"
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

    override fun durationMetric(
        mode: Mode,
        time: Time
    ): DurationMetric = durationMetric
}

private const val SHOULD_NOT_BE_CALLED = "Should not be called!"
@Deprecated("dummy impedance should no longer be used, try using Teleportation")
val dummyImpedance = object : Impedance {
    override fun duration(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time) = 5.minutes
    override fun cost(from: HasZoneID, to: HasZoneID, mode: Mode, time: Time) = 5.euros
    override fun distance(from: HasZoneID, to: HasZoneID, mode: Mode) = 5.kilometers
    override fun costMetric(mode: Mode, time: Time): CostMetric = error(SHOULD_NOT_BE_CALLED)
    override fun distanceMetric(mode: Mode): DistanceMetric = error(SHOULD_NOT_BE_CALLED)
    override fun durationMetric(mode: Mode, time: Time): DurationMetric = error(SHOULD_NOT_BE_CALLED)
}
