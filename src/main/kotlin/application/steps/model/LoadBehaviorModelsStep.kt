package application.steps.model

import application.steps.HasAttractivenessModel
import application.steps.HasDrtProviderRepo
import application.steps.HasImpedance
import application.steps.HasMutablePersonBehavior
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import core.modelsteps.steps.repositoryDependentStep
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import domain.shared.enums.Mode
import domain.shared.location.StandardLocation
import domain.shared.location.zone.MaximalZone
import domain.shared.location.zone.Zone
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceCharacteristics
import domain.simulation.events.GenerateDestinationCharacteristics
import domain.simulation.events.GenerateModeCharacteristics
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.synthesis.data.drt.DrtProvider
import domain.synthesis.data.SharingProvider
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel

// TODO split into model steps to define mode avail, mode choice, destination choice individually
/**
 * Loads and initializes the behavior models for person agents.
 *
 * This step sets up the [PersonBehavior] which includes destination choice, mode choice,
 * availability models, and replanning strategies. It integrates various sub-models and
 * repositories to create a comprehensive behavior model for simulation agents.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasMutablePersonBehavior]
 *   - [HasZoneRepo] for [Zone]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 *   - [HasImpedance]
 *   - [HasAttractivenessModel]
 * @param destinationChoiceModel The underlying utility-based model for destination choice.
 * @param modeChoiceModel The underlying fixed-choice model for mode choice.
 * @param modes Definitions of modes used in the choice models.
 * @param spawnDestinationChoiceCharacteristics Strategy for generating destination choice characteristics.
 *                                              Defaults to [StandardDestinationImplementation].
 * @param spawnModeChoiceCharacteristics Strategy for generating mode choice characteristics.
 *                                       Defaults to [StandardModeImplementation].
 * @param replanningStrategy The strategy to use when replanning schedules. Defaults to [ReplanningStrategy.SHIFT].
 */
@Suppress("LongParameterList")
fun <C> C.loadBehaviorModels(
    destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    modes: ChoiceModelModes, // TODO remove ChoiceModelModes and replace by Context/Config requirements
    spawnDestinationChoiceCharacteristics: NewDestinationCharacteristics = StandardDestinationImplementation,
    spawnModeChoiceCharacteristics: NewModeCharacteristics = StandardModeImplementation,
    replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
) where C : HasMutablePersonBehavior,
        C : HasZoneRepo<*, MaximalZone>,
        C : HasSharingProviderRepo<*, SharingProvider>,
        C : HasDrtProviderRepo<*, DrtProvider>,
        C : HasImpedance,
        C : HasAttractivenessModel =
    repositoryDependentStep(
        "load behavior models",
        dependentRepositories = setOf(zoneRepository, sharingProviderRepository, drtProviderRepository),
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
        val availability = AvailabilityModelWithSharing(
            modes,
            sharingProvidersByMode,
            drtProvidersByMode,
            impedance,
        )

        val modeChoice = modeChoiceModel // .addFilter(availability.asResourceAvailabilityFilter())

        val destinationChoice = destinationChoiceModel.fixed(

            zoneRepository.elements.filter { it.isDestination }.map {
                it.centroidLocation
            }.toSet(),
        )

        personBehavior = PersonBehavior(
            destinationChoice,
            modeChoice,
            modes,
            impedance,
            attractiveness,
            availability,
            availability,
            availability,
            spawnDestinationChoiceCharacteristics,
            spawnModeChoiceCharacteristics,
            replanningStrategy,
        )
    }

typealias NewDestinationCharacteristics = GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>
typealias NewModeCharacteristics = GenerateModeCharacteristics<ModeChoiceCharacteristics>
