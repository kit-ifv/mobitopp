package domain.jackson

import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceParameters
import domain.simulation.behavior.legacyDestinationChoiceBuilder
import domain.simulation.behavior.legacyModeChoiceBuilder
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel


val DestinationChoiceModule = GenericKeyValueBuilder(
    UtilityBasedChoiceModel::class.java,
    mapOf("default" to legacyDestinationChoiceBuilder.build(DestinationChoiceParameters())),
    loadFromSubmodules = true
).getModule()

val ModeChoiceModule = GenericKeyValueBuilder(
    FixedChoiceModel::class.java,
    mapOf("default" to legacyModeChoiceBuilder.build(ModeChoiceParameters())),
    loadFromSubmodules = true
).getModule()
