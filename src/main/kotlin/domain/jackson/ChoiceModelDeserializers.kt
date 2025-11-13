package domain.jackson

import com.fasterxml.jackson.databind.JavaType
import com.fasterxml.jackson.databind.type.TypeFactory
import domain.shared.enums.Mode
import domain.shared.location.Location
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.DestinationChoiceParameters
import domain.simulation.behavior.ModeChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceParameters
import domain.simulation.behavior.legacyDestinationChoiceBuilder
import domain.simulation.behavior.legacyModeChoiceBuilder
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel


val destinationChoiceModelType: JavaType = TypeFactory.defaultInstance().constructParametricType(UtilityBasedChoiceModel::class.java, Location::class.java, DestinationChoiceCharacteristics::class.java)

val DestinationChoiceModule = GenericKeyValueBuilder(
    destinationChoiceModelType,
    mapOf("default" to legacyDestinationChoiceBuilder.build(DestinationChoiceParameters())),
    loadFromSubmodules = true
).getModule()

val modeChoiceModelType: JavaType = TypeFactory.defaultInstance().constructParametricType(FixedChoiceModel::class.java, Mode::class.java,
    ModeChoiceCharacteristics::class.java)


val ModeChoiceModule = GenericKeyValueBuilder(
    modeChoiceModelType,
    mapOf("default" to legacyModeChoiceBuilder.build(ModeChoiceParameters())),
    loadFromSubmodules = true
).getModule()


