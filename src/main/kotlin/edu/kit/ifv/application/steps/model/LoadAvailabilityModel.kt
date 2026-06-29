package edu.kit.ifv.application.steps.model

import edu.kit.ifv.application.steps.HasChoiceModelModes
import edu.kit.ifv.application.steps.HasDrtProviderRepo
import edu.kit.ifv.application.steps.HasMutableModeAvailabilityModel
import edu.kit.ifv.application.steps.HasSharingProviderRepo
import edu.kit.ifv.core.modelsteps.steps.repositoryDependentStep
import edu.kit.ifv.domain.simulation.behavior.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProvider

fun <C> C.loadAvailabilityModel(
) where C : HasSharingProviderRepo<*, SharingProvider>,
        C : HasDrtProviderRepo<*, DrtProvider>,
        C : HasChoiceModelModes,
        C : HasMutableModeAvailabilityModel
        =
    repositoryDependentStep(
        "load availability model",
        dependentRepositories = setOf(sharingProviderRepository, drtProviderRepository),
    ) {
        val sharingProviders = sharingProviderRepository.elements.associateBy { it.id }
        val sharingProvidersByMode = sharingProviders.values.groupBy {
            it.mode
        }.mapValues {
            it.value.map { p -> p.id }.toSet()
        }

        val drtProviders = drtProviderRepository.elements.associateBy { it.id }
        val drtProvidersByMode = drtProviders.values.groupBy {
            it.mode
        }.mapValues {
            it.value.map { p -> p.id }.toSet()
        }

        // TODO refactor availability model, as composite of availability rules
        modeAvailability = AvailabilityModelWithSharing(
            choiceModelModes,
            sharingProvidersByMode,
            drtProvidersByMode,
        )
    }