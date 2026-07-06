package edu.kit.ifv.application.steps.model

import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasMutableDestinationChoiceModel
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.core.modelsteps.steps.repositoryDependentStep
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceParameters
import edu.kit.ifv.domain.simulation.behavior.legacyDestinationChoice
import edu.kit.ifv.mobitopp.discretechoice.models.DiscreteChoiceModel

fun <C> C.loadDestinationChoiceModel(
    discreteChoiceModel:
    DiscreteChoiceModel<StandardLocation, DestinationChoiceCharacteristics, DestinationChoiceParameters> =
        context(impedance, attractiveness, modeAvailability) { legacyDestinationChoice },
) where C : HasZoneRepo<*, MaximalZone>, C : HasMutableDestinationChoiceModel,
        C : HasImpedance, C : HasAttractivenessModel, C : HasMutableModeAvailabilityModel =
    repositoryDependentStep(
        "load destination choice model",
        dependentRepositories = setOf(zoneRepository),
    ) {
        destinationChoiceModel = discreteChoiceModel.fixed(
            zoneRepository.elements.filter { it.isDestination }.map {
                it.centroidLocation
            }.toSet(),
        )
    }
