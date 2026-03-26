package application.steps.model

import application.steps.parser.dummyImpedance
import core.modelsteps.LateInit
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.RepositoryDependentStep
import core.modelsteps.Warning
import core.modelsteps.validateCondition
import core.modelsteps.validateScope
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.datastructure.schedule.replanning.ReplanningStrategy
import domain.shared.enums.Mode
import domain.shared.enums.legacyChoiceModelModes
import domain.shared.location.LegacyZone
import domain.shared.location.StandardLocation
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.agent.DrtOffer
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.BikeSharingConnectionSelector
import domain.simulation.behavior.DestinationChoiceCharacteristics
import domain.simulation.behavior.DrtAvailabilitySelector
import domain.simulation.behavior.ModeAvailabilityModel
import domain.simulation.behavior.ModeChoiceCharacteristics
import domain.simulation.behavior.available
import domain.simulation.config.DemandSimContext
import domain.simulation.events.GenerateDestinationCharacteristics
import domain.simulation.events.GenerateModeCharacteristics
import domain.simulation.events.PersonBehavior
import domain.simulation.events.StandardDestinationImplementation
import domain.simulation.events.StandardModeImplementation
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.IPerson
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel
import utils.units.AbsoluteTime

fun LoadBehaviorModelsContext.loadBehaviorModels(
    destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    modes: ChoiceModelModes,
    replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
) = this.loadBehaviorModels(
    LoadBehaviorModelConfig(
        destinationChoiceModel,
        modeChoiceModel,
        modes,
        StandardDestinationImplementation,
        StandardModeImplementation,
        replanningStrategy,
    )
)

data class LoadBehaviorModelConfig(
    val destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    val modes: ChoiceModelModes,
    val spawnDestinationChoiceCharacteristics: NewDestinationCharacteristics = StandardDestinationImplementation,
    val spawnModeChoiceCharacteristics: NewModeCharacteristics = StandardModeImplementation,
    val replanningStrategy: ReplanningStrategy = ReplanningStrategy.SHIFT,
)

fun LoadBehaviorModelsContext.loadBehaviorModels(
    config: LoadBehaviorModelConfig,
) = runStep {
    loadBehaviorModelsStep(config)
}

fun LoadBehaviorModelsContext.loadBehaviorModelsStep(
    config: LoadBehaviorModelConfig,
): LoadBehaviorModelsStep {
    return config.run {
        LoadBehaviorModelsStep(
            this@loadBehaviorModelsStep,
            destinationChoiceModel,
            modeChoiceModel,
            modes,
            spawnDestinationChoiceCharacteristics,
            spawnModeChoiceCharacteristics,
            replanningStrategy,
        )
    }
}

interface LoadBehaviorModelsContext : DemandSimContext {
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
    val drtProviderRepository: Repository<DrtProvider, DrtProviderId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>
    val attractivenessModel: LateInit<AttractivenessModel>
}

typealias NewDestinationCharacteristics = GenerateDestinationCharacteristics<DestinationChoiceCharacteristics>
typealias NewModeCharacteristics = GenerateModeCharacteristics<ModeChoiceCharacteristics>

open class LoadBehaviorModelsStep(
    private val context: LoadBehaviorModelsContext,
    private val destinationChoiceModel: UtilityBasedChoiceModel<StandardLocation, DestinationChoiceCharacteristics>,
    protected open val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceCharacteristics>,
    private val modes: ChoiceModelModes,
    private val spawnDestinationChoiceCharacteristics: NewDestinationCharacteristics,
    private val spawnModeChoiceCharacteristics: NewModeCharacteristics,
    private val replanningStrategy: ReplanningStrategy,
) : RepositoryDependentStep {

    override val name: String = "Load behavior models!"
    override val repository: MutableRepository<*, *>? = null
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository, context.sharingProviderRepository, context.drtProviderRepository
    )

    override fun execute() {
        val impedance = context.impedance.value

        val sharingProviders = context.sharingProviderRepository.elements.associateBy { it.id }

        val sharingProvidersByMode = sharingProviders.values.groupBy {
            it.mode
        }.mapValues {
            it.value.map { p -> p.id }.toSet()
        }

        val drtProviders = context.drtProviderRepository.elements.associateBy { it.id }

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
            context.zoneRepository.elements.map { it.centroid }.toSet()
        )

        val behavior = PersonBehavior(
            destinationChoice,
            modeChoice,
            modes,
            impedance,
            context.attractivenessModel.value,
            availability,
            availability,
            availability,
            spawnDestinationChoiceCharacteristics,
            spawnModeChoiceCharacteristics,
            replanningStrategy
        )

        context.behavior.value = behavior
    }

    override fun verifyInput(): Warning? = validateScope("Validate impedance is initialized:") {
        validateCondition("Cannot access impedance, as it has not been loaded yet!") {
            context.impedance.isSet
        }
    }

    override fun mockBehavior(): Warning? = validateScope("Mock choice models for validation:") {
        val impedance = if (context.impedance.isSet) {
            context.impedance.value
        } else {
            dummyImpedance
        }

        context.behavior.value = PersonBehavior(
            destinationChoice = RandomChoiceModel("Dummy destination choice for validation", setOf()),
            modeChoice = RandomChoiceModel("Dummy mode choice for validation", context.modes.values()),
            modes = legacyChoiceModelModes,
            impedance = impedance,
            context.attractivenessModel.value,
            DummyAvailability,
            BikeSharingConnectionSelector { p, l -> null },
            DummyDrtAvailabilitySelector,
            StandardDestinationImplementation,
            StandardModeImplementation,
        )
    }

}

object DummyAvailability : ModeAvailabilityModel {

    context(person: IPerson) override fun staticAvailability(mode: Mode) = true

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun providerAvailability(mode: Mode) = mode.available()

    context(characteristics: ModeChoiceCharacteristics) override fun resourceAvailability(mode: Mode) = true
}

object DummyDrtAvailabilitySelector: DrtAvailabilitySelector {

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun getDrtProvidersCurrentlyOperating(): List<DrtProviderAgent> = emptyList()

    context(agent: PersonAgent, time: AbsoluteTime, destination: StandardLocation)
    override fun findDrtOffers(): List<DrtOffer> = emptyList()
}
