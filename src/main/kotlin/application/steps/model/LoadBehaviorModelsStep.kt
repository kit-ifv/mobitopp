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
import domain.shared.location.Zone
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.ModeChoiceCharacteristics
import domain.simulation.events.GenerateDestinationCharacteristics
import domain.simulation.events.GenerateModeCharacteristics
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.SharingProvider
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel

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
      C : HasZoneRepo<*, Zone>,
      C : HasSharingProviderRepo<*, SharingProvider>,
      C : HasDrtProviderRepo<*, DrtProvider>,
      C : HasImpedance,
      C : HasAttractivenessModel =
    repositoryDependentStep(
        "load behavior models",
        dependentRepositories = setOf(zoneRepository, sharingProviderRepository, drtProviderRepository)
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
            impedance
        )

        val modeChoice = modeChoiceModel.addFilter(availability.asResourceAvailabilityFilter())

        val destinationChoice = destinationChoiceModel.fixed(
            zoneRepository.elements.filter { it.isDestination }.map {
                it.centroid
            }.toSet()
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
            replanningStrategy
        )
    }

typealias NewDestinationCharacteristics = GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>
typealias NewModeCharacteristics = GenerateModeCharacteristics<ModeChoiceCharacteristics>
// TODO maybe use config object and modify scope instead of 6 function args
// data class LoadBehaviorModelConfig(
//    val destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
//    val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
//    val modes: ChoiceModelModes,
//    var spawnDestinationChoiceCharacteristics: NewDestinationCharacteristics = StandardDestinationImplementation,
//    var spawnModeChoiceCharacteristics: NewModeCharacteristics = StandardModeImplementation,
//    var replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
// )

//
//
// fun LoadBehaviorModelsContext.loadBehaviorModels(
//    destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
//    modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
//    modes: ChoiceModelModes,
//    replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
// ) = this.loadBehaviorModels(
//    LoadBehaviorModelConfig(
//        destinationChoiceModel,
//        modeChoiceModel,
//        modes,
//        StandardDestinationImplementation,
//        StandardModeImplementation,
//        replanningStrategy,
//    )
// )
//
// fun LoadBehaviorModelsContext.loadBehaviorModels(
//    config: LoadBehaviorModelConfig,
// ) = runStep {
//    loadBehaviorModelsStep(config)
// }
//
// fun LoadBehaviorModelsContext.loadBehaviorModelsStep(
//    config: LoadBehaviorModelConfig,
// ): LoadBehaviorModelsStep {
//    return config.run {
//        LoadBehaviorModelsStep(
//            this@loadBehaviorModelsStep,
//            destinationChoiceModel,
//            modeChoiceModel,
//            modes,
//            spawnDestinationChoiceCharacteristics,
//            spawnModeChoiceCharacteristics,
//            replanningStrategy,
//        )
//    }
// }
//
// interface LoadBehaviorModelsContext : DemandSimContext {
//    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
//    val drtProviderRepository: Repository<DrtProvider, DrtProviderId>
//    val zoneRepository: Repository<Zone, ZoneId>
//    val zoneColumnIndex: Map<Int, LegacyZone>
//    val attractivenessModel: LateInit<AttractivenessModel>
// }
//
//
// open class LoadBehaviorModelsStep(
//    private val context: LoadBehaviorModelsContext,
//    private val destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
//    protected open val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
//    private val modes: ChoiceModelModes,
//    private val spawnDestinationChoiceCharacteristics: NewDestinationCharacteristics,
//    private val spawnModeChoiceCharacteristics: NewModeCharacteristics,
//    private val replanningStrategy: ReplanningStrategy,
// ) : RepositoryDependentStep {
//
//    override val name: String = "Load behavior models!"
//    override val repository: MutableRepository<*, *>? = null
//    override val dependentRepositories: Set<Repository<*, *>> = setOf(
//        context.zoneRepository, context.sharingProviderRepository, context.drtProviderRepository
//    )
//
//    override fun execute() {
//        val impedance = context.impedance.value
//
//        val sharingProviders = context.sharingProviderRepository.elements.associateBy { it.id }
//
//        val sharingProvidersByMode = sharingProviders.values.groupBy {
//            it.mode
//        }.mapValues {
//            it.value.map { p -> p.id }.toSet()
//        }
//
//        val drtProviders = context.drtProviderRepository.elements.associateBy { it.id }
//
//        val drtProvidersByMode = drtProviders.values.groupBy {
//            it.mode
//        }.mapValues {
//            it.value.map { p -> p.id }.toSet()
//        }
//
//        // TODO refactor availability model, as composite of availability rules
//
//        val availability = AvailabilityModelWithSharing(
//            modes,
//            sharingProvidersByMode,
//            drtProvidersByMode,
//            impedance
//        )
//
//        val modeChoice = modeChoiceModel.addFilter(availability.asResourceAvailabilityFilter())
//
//        val destinationChoice = destinationChoiceModel.fixed(
//            context.zoneRepository.elements.filter { it.isDestination }.map {
//                it.centroid
//            }.toSet()
//        )
//
//        val behavior = PersonBehavior(
//            destinationChoice,
//            modeChoice,
//            modes,
//            impedance,
//            context.attractivenessModel.value,
//            availability,
//            availability,
//            availability,
//            spawnDestinationChoiceCharacteristics,
//            spawnModeChoiceCharacteristics,
//            replanningStrategy
//        )
//
//        context.behavior.value = behavior
//    }
//
//    override fun verifyInput(): Warning? = validateScope("Validate impedance is initialized:") {
//        validateCondition("Cannot access impedance, as it has not been loaded yet!") {
//            context.impedance.isSet
//        }
//    }
//
//    override fun mockBehavior(): Warning? = validateScope("Mock choice models for validation:") {
//        val impedance = if (context.impedance.isSet) {
//            context.impedance.value
//        } else {
//            dummyImpedance
//        }
//
//        context.behavior.value = PersonBehavior(
//            destinationChoice = RandomChoiceModel("Dummy destination choice for validation", setOf()),
//            modeChoice = RandomChoiceModel("Dummy mode choice for validation", context.modes.values()),
//            modes = legacyChoiceModelModes,
//            impedance = impedance,
//            context.attractivenessModel.value,
//            DummyAvailability,
//            BikeSharingConnectionSelector { p, l -> null },
//            DummyDrtAvailabilitySelector,
//            StandardDestinationImplementation,
//            StandardModeImplementation,
//        )
//    }
//
// }
//
// object DummyAvailability : ModeAvailabilityModel {
//
//    context(person: IPerson) override fun staticAvailability(mode: Mode) = true
//
//    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun providerAvailability(mode: Mode) = mode.available()
//
//    context(characteristics: ModeChoiceCharacteristics) override fun resourceAvailability(mode: Mode) = true
// }
//
// object DummyDrtAvailabilitySelector: DrtAvailabilitySelector {
//
//    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent> = emptyList()
//
//    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
//    override fun findDrtOffers(): List<DrtOffer> = emptyList()
// }
