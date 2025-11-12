package domain.jackson

import domain.shared.location.Location
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceParameters
import domain.simulation.behavior.legacyDestinationChoiceBuilder
import domain.simulation.behavior.legacyModeChoiceBuilder
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel

class DestinationChoiceModelProvider(model: UtilityBasedChoiceModel<Location, DestinationChoiceCharacteristics>)

val DestinationChoiceModule = GenericKeyValueBuilder(
    DestinationChoiceModelProvider::class.java,
    mapOf("default" to legacyDestinationChoiceBuilder.build(DestinationChoiceParameters())),
    loadFromSubmodules = true
).getModule()

val ModeChoiceModule = GenericKeyValueBuilder(
    FixedChoiceModel::class.java,
    mapOf("default" to legacyModeChoiceBuilder.build(ModeChoiceParameters())),
    loadFromSubmodules = true
).getModule()
