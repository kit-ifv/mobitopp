package edu.kit.ifv.domain.jackson
import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import edu.kit.ifv.application.config.DestinationChoiceFactory
import edu.kit.ifv.application.config.ModeChoiceFactory
import edu.kit.ifv.domain.shared.behavior.AttractivenessModel
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.Impedance
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.DestinationChoiceParameters
import edu.kit.ifv.domain.simulation.behavior.availability.ModeAvailabilityModel
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.modechoice.ModeChoiceParameters
import edu.kit.ifv.domain.simulation.behavior.destinationchoice.createLegacyDestinationChoiceBuilder
import edu.kit.ifv.domain.simulation.behavior.modechoice.createLegacyModeChoiceBuilder

/**
 * Getter for the `DestinationChoiceModelType` aka
 * `UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>`.
 *
 * (This is not a val because weird stuff happens when subprojects use that val. Serviceloader tries to access it
 * before it is initialized. Generally initialization dependencies between top level vals seem to not be well-defined
 * in kotlin.)
 */
fun getDestinationChoiceModelType(): JavaType = TypeFactory.defaultInstance().constructParametricType(
    DestinationChoiceFactory::class.java,
    StandardLocation::class.java,
    DestinationChoiceCharacteristics::class.java,
)

val legacyDestinationChoiceFactory = DestinationChoiceFactory<StandardLocation, DestinationChoiceCharacteristics> {
        impedance: Impedance,
        attractivenessModel: AttractivenessModel,
        availabilityModel: ModeAvailabilityModel,
    ->
    createLegacyDestinationChoiceBuilder(impedance, attractivenessModel, availabilityModel).build(
        DestinationChoiceParameters(),
    )
}

val DestinationChoiceModule = GenericKeyValueBuilder(
    getDestinationChoiceModelType(),
    mapOf(
        "legacyDestinationChoiceModel" to
            legacyDestinationChoiceFactory,
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
    ModeChoiceFactory::class.java,
    Mode::class.java,
    ModeChoiceCharacteristics::class.java,
)

val legacyModeChoiceFactory = ModeChoiceFactory<Mode, ModeChoiceCharacteristics> { impedance: Impedance ->
    createLegacyModeChoiceBuilder(impedance).build(ModeChoiceParameters())
}

val ModeChoiceModule = GenericKeyValueBuilder(
    getModeChoiceModelType(),
    mapOf(
        "legacyModeChoiceModel" to legacyModeChoiceFactory,
    ),
    loadFromSubmodules = true,
).getModule()
