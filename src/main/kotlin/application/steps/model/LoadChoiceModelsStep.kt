package application.steps.model

import application.steps.parser.dummyImpedance
import core.modelsteps.LateInit
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.RepositoryDependentStep
import core.modelsteps.Warning
import core.modelsteps.validateCondition
import core.modelsteps.validateScope
import discreteChoice.models.ChoiceModel
import discreteChoice.models.FixedChoicesModel
import discreteChoice.models.RandomChoiceModel
import discreteChoice.models.addFilter
import discreteChoice.models.fixed
import domain.shared.behavior.AttractivenessModel
import domain.shared.behavior.ChoiceModelModes
import domain.shared.enums.Mode
import domain.shared.location.LegacyZone
import domain.shared.location.Location
import domain.shared.location.Zone
import domain.shared.location.ZoneId
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.AvailabilityModelWithSharing
import domain.simulation.behavior.DestinationAlternative
import domain.simulation.behavior.FixedModesFilter
import domain.simulation.behavior.ModeAvailabilityFilter
import domain.simulation.behavior.ModeChoiceAlternative
import domain.simulation.behavior.SituativeAvailability
import domain.simulation.config.DemandSimContext
import domain.simulation.events.PersonBehavior
import domain.synthesis.data.IPerson
import domain.synthesis.data.SharingProviderId

fun LoadChoiceModelsContext.loadChoiceModels(
    destinationChoiceModel: ChoiceModel<DestinationAlternative, Location>,
    modeChoiceModel: FixedChoicesModel<ModeChoiceAlternative, Mode>,
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
    private val destinationChoiceModel: ChoiceModel<DestinationAlternative, Location>,
    private val modeChoiceModel: FixedChoicesModel<ModeChoiceAlternative, Mode>,
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

        val providers = context.sharingProviderAgents.elements.associateBy { it.id }

        val availability =
            AvailabilityModelWithSharing( // TODO refactor availability model, as composite of availability rules
                modes,
                providers.values.groupBy {
                    it.mode
                }.mapValues { it.value.map { p -> p.id }.toSet() },
                impedance
            )

        val modeChoice = modeChoiceModel.addFilter(
            availability
        ).addFilter(FixedModesFilter)

        val destinationChoice = destinationChoiceModel.fixed(
            context.zoneRepository.elements.map { it.centroid }.toSet()
        )

        val behavior = PersonBehavior.from(
            impedance,
            destinationChoice,
            modeChoice,
            context.attractivenessModel.value,
            availability,
            modes,
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
            context.attractivenessModel.value,
            DummyAvailability(modes.options),
            { null },
            choiceModelModes = modes
        )
    }
}

data class DummyAvailability(val modes: Set<Mode>) : ModeAvailabilityFilter {
    override fun filter(choices: Set<ModeChoiceAlternative>) = choices
    override fun staticAvailability(person: IPerson) = modes
    override fun situativeAvailability(person: PersonAgent) = SituativeAvailability(modes, emptySet())
    override fun alternativeDependentAvailability(alternative: ModeChoiceAlternative) = true
}
