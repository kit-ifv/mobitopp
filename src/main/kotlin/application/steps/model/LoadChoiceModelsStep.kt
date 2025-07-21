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
import domain.shared.enums.Mode
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.FixedModesFilter
import domain.simulation.behavior.ModeAvailabilityFilter
import domain.simulation.behavior.ModeChoiceAlternative
import domain.simulation.behavior.ModeChoiceSituation
import domain.simulation.behavior.SharingAvailabilityFilter
import domain.simulation.behavior.TripChoiceSituation
import domain.simulation.config.DemandSimContext
import domain.simulation.events.CarSelector
import domain.simulation.events.ModeScopeDispatcher
import domain.simulation.events.PersonBehavior
import domain.simulation.events.SharingVehicleSelector
import domain.synthesis.data.SharingProviderId
import edu.kit.ifv.mobitopp.discretechoice.models.FixedChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.RandomChoiceModel
import edu.kit.ifv.mobitopp.discretechoice.models.UtilityBasedChoiceModel


fun LoadChoiceModelsContext.loadChoiceModels(
    destinationChoiceModel: UtilityBasedChoiceModel<Location, TripChoiceSituation >,
    modeChoiceModel: FixedChoiceModel< Mode, ModeChoiceSituation>,
    modes: ChoiceModelModes,
) = runStep {
    LoadChoiceModelsStep(this, destinationChoiceModel, modeChoiceModel, modes)
}

interface LoadChoiceModelsContext : DemandSimContext {
    val sharingProviderAgents: Repository<SharingProviderAgent, SharingProviderId>
    val zoneRepository: Repository<Zone, ZoneId>
    val zoneColumnIndex: Map<Int, LegacyZone>
    val attractivenessModel: LateInit<AttractivenessModel>
}

class LoadChoiceModelsStep(
    private val context: LoadChoiceModelsContext,
    private val destinationChoiceModel: UtilityBasedChoiceModel< Location, TripChoiceSituation>,
    private val modeChoiceModel: FixedChoiceModel<Mode, ModeChoiceSituation>,
    private val modes: ChoiceModelModes,
) : RepositoryDependentStep {

    override val name: String = "Load transmove legacy mode and destination choice!"
    override val repository: MutableRepository<*, *>? = null
    override val dependentRepositories: Set<Repository<*, *>> = setOf(
        context.zoneRepository,
        context.sharingProviderAgents
    )

    override fun execute() {
        val impedance = context.impedance.value

        val providers = context.sharingProviderAgents.elements
        val stations = providers.flatMap {
            it.stations
        }

        val availability =
            SharingAvailabilityFilter( // TODO refactor availability model, as composite of availability rules
                modes,
                stations.toSet(),
                providers.groupBy {
                    it.mode
                }.mapValues { it.value.toSet() },
                impedance
            )

        val modeChoice = modeChoiceModel.addFilter(
            availability
        ).addFilter(FixedModesFilter)

        val destinationChoice = destinationChoiceModel.fixed(
            context.zoneRepository.elements.map { it.centroid }.toSet()
        )

        val behavior = PersonBehavior(
            destinationChoice,
            modeChoice,
            impedance,
            ModeScopeDispatcher(
                modes.car to CarSelector(modes.car),
                modes.let {
                    it.bikeSharing to SharingVehicleSelector(it.bikeSharing, availability, impedance, it.pedestrian)
                }
            ),
            context.attractivenessModel.value,
            availability,
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
            impedance = impedance,
            scopeDispatcher = ModeScopeDispatcher(mapOf()),
            context.attractivenessModel.value,
            DummyAvailability,
        )
    }
}

object DummyAvailability : ModeAvailabilityFilter {
    context(_: ModeChoiceSituation)
    override fun filter(choices: Set<Mode>) = choices
}
