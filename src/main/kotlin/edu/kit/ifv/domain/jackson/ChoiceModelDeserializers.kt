package edu.kit.ifv.domain.jackson
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceParameters
import edu.kit.ifv.domain.simulation.behavior.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceParameters
import edu.kit.ifv.domain.simulation.behavior.legacyDestinationChoiceBuilder
import edu.kit.ifv.domain.simulation.behavior.legacyModeChoiceBuilder
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel

/**
 * Getter for the `DestinationChoiceModelType` aka
 * `UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>`.
 *
 * (This is not a val because weird stuff happens when subprojects use that val. Serviceloader tries to access it
 * before it is initialized. Generally initialization dependencies between top level vals seem to not be well-defined
 * in kotlin.)
 */
fun getDestinationChoiceModelType(): JavaType = TypeFactory.defaultInstance().constructParametricType(
    UtilityBasedChoiceModel::class.java,
    StandardLocation::class.java,
    DestinationChoiceCharacteristics::class.java,
)

val DestinationChoiceModule = GenericKeyValueBuilder(
    getDestinationChoiceModelType(),
    mapOf(
        "legacyDestinationChoiceModel" to
            {
                    impedance: Impedance,
                    attractivenessModel: AttractivenessModel,
                    availabilityModel: ModeAvailabilityModel,
                ->
                context(
                    impedance,
                    attractivenessModel,
                    availabilityModel,
                ) { legacyDestinationChoiceBuilder.build(DestinationChoiceParameters()) }
            },
    ),
    loadFromSubmodules = true,
).getModule()

/**
 * Getter for the `ModeChoiceModelType` aka `FixedChoiceModel<Mode, ModeChoiceCharacteristics>`.
 *
 * (This is not a val because weird stuff happens when subprojects use that val. Serviceloader tries to access it
 * before it is initialized. Generally initialization dependencies between top level vals seem to not be well-defined
 * in kotlin.)
 */
fun getModeChoiceModelType(): JavaType = TypeFactory.defaultInstance().constructParametricType(
    FixedChoiceModel::class.java,
    Mode::class.java,
    ModeChoiceCharacteristics::class.java,
)

val ModeChoiceModule = GenericKeyValueBuilder(
    getModeChoiceModelType(),
    mapOf(
        "legacyModeChoiceModel" to { impedance: Impedance ->
            context(impedance) {
                legacyModeChoiceBuilder.build(ModeChoiceParameters())
            }
        },
    ),
    loadFromSubmodules = true,
).getModule()
