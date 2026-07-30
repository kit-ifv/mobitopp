package edu.kit.ifv.application.steps.model

import edu.kit.ifv.application.steps.HasAttractivenessModel
import edu.kit.ifv.application.steps.HasDrtProviderRepo
import edu.kit.ifv.application.steps.HasImpedance
import edu.kit.ifv.application.steps.HasMutablePersonBehavior
import edu.kit.ifv.application.steps.HasSharingProviderRepo
import edu.kit.ifv.application.steps.HasZoneRepo
import edu.kit.ifv.core.modelsteps.steps.repositoryDependentStep
import edu.kit.ifv.domain.shared.behavior.ChoiceModelModes
import edu.kit.ifv.domain.shared.datastructure.matrix.LargeBuddy
import edu.kit.ifv.domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import edu.kit.ifv.domain.shared.enums.Mode
import edu.kit.ifv.domain.shared.location.StandardLocation
import edu.kit.ifv.domain.shared.location.zone.MaximalZone
import edu.kit.ifv.domain.shared.location.zone.Zone
import edu.kit.ifv.domain.simulation.behavior.AvailabilityModelWithSharing
import edu.kit.ifv.domain.simulation.behavior.DestinationChoiceCharacteristics
import edu.kit.ifv.domain.simulation.behavior.ModeChoiceCharacteristics
import edu.kit.ifv.domain.simulation.data.drt.DrtProvider
import edu.kit.ifv.domain.simulation.data.sharing.SharingProvider
import edu.kit.ifv.domain.simulation.events.GenerateDestinationCharacteristics
import edu.kit.ifv.domain.simulation.events.GenerateModeCharacteristics
import edu.kit.ifv.domain.simulation.events.PersonBehavior
import edu.kit.ifv.domain.simulation.events.StandardDestinationImplementation
import edu.kit.ifv.domain.simulation.events.StandardModeImplementation
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

        val availableLocations: Set<StandardLocation> = zoneRepository.elements.filter { it.isDestination }.map {
            it.centroidLocation
        }.toSet()
        val destinationChoice = destinationChoiceModel.fixed(
            availableLocations, // todo probably needs to be WRAPPED CM here for internal BatchUtilModel
        )
        val zoneIds = zoneRepository.elements.toList().map {it.zoneId.value.toInt()}.sorted().withIndex()
        val mapping = zoneIds.associate { it.value to it.index }
        val max = zoneIds.last().value
        val lookupArray = (0..max).map { mapping[it] ?: -1 }.toIntArray()
        LargeBuddy.set(lookupArray)

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
