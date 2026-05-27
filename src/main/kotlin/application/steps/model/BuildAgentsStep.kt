package application.steps.model

import core.modelsteps.AbstractAddResourceStep
import core.modelsteps.LazyResource
import core.modelsteps.ModelStep
import core.modelsteps.MutableRepository
import core.modelsteps.Repository
import core.modelsteps.Resource
import core.modelsteps.Warning
import core.statemachine.StateMachineFactory
import domain.shared.location.ZoneId
import domain.shared.location.zone.StandardZone
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.NoDurationRandomizer
import domain.simulation.config.DemandSimContext
import domain.synthesis.data.ActivityId
import domain.synthesis.data.CarId
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.DrtProviderId
import domain.synthesis.data.Household
import domain.synthesis.data.HouseholdId
import domain.synthesis.data.MutablePlannedActivity
import domain.synthesis.data.Person
import domain.synthesis.data.PersonId
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider
import domain.synthesis.data.SharingProviderId

interface BuildAgentsContext : DemandSimContext {
    val zoneRepository: Repository<StandardZone, ZoneId>
    val householdRepository: Repository<Household, HouseholdId>
    val carRepository: Repository<PrivateCar, CarId>
    val personRepository: Repository<Person, PersonId>
    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
    val drtProviderRepository: Repository<DrtProvider, DrtProviderId>

    val personAgents: MutableRepository<PersonAgent, PersonId>
    val sharingProviderAgents: MutableRepository<SharingProviderAgent, SharingProviderId>
    val drtProviderAgents: MutableRepository<DrtProviderAgent, DrtProviderId>
}

fun BuildAgentsContext.buildAgents(
    personStateMachine: StateMachineFactory<PersonAgent>,
    durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
    drtStateMachine: StateMachineFactory<DrtProviderAgent>? = null,
    drtAlgorithm: DrtAlgorithm? = null,
) = runMultipleSteps {
    val builder = BuildAgents(
        simulationSeed,
        personStateMachine,
        behavior.value,
        drtStateMachine,
        drtAlgorithm,
        durationRandomizer,
    )

    listOf(
        BuildProviderAgentsStep(this, builder),
        BuildPersonAgentsStep(this, builder),
        CleanUpDataStep(this, builder),
    )
}

class BuildPersonAgentsStep(context: BuildAgentsContext, builder: BuildAgents) :
    AbstractAddResourceStep<PersonAgent, PersonId>() {
    override val name = "build person agents"

    override val repository: MutableRepository<PersonAgent, PersonId> = context.personAgents
    override val resource: Resource<PersonAgent> = LazyResource(name, "BuildPersonAgentsStep") {
        builder.buildPersonAgents(
            context.householdRepository.elements.toList(),
        ).asSequence()
    }

    override val dependentRepositories: Set<Repository<*, *>> = context.let {
        setOf(
            it.zoneRepository,
            it.householdRepository,
            it.carRepository,
            it.sharingProviderRepository,
        )
    }

    override fun verifyInput(): Warning? = null

    override fun mockElementsForValidation(): List<PersonAgent> = emptyList()
}

class BuildProviderAgentsStep(context: BuildAgentsContext, builder: BuildAgents) :
    AbstractAddResourceStep<SharingProviderAgent, SharingProviderId>() {
    override val name = "build provider agents"

    override val repository: MutableRepository<SharingProviderAgent, SharingProviderId> = context.sharingProviderAgents
    override val resource: Resource<SharingProviderAgent> = LazyResource(name, "BuildProviderAgentsStep") {
        builder.buildProviderAgents(
            context.sharingProviderRepository.elements.toList(),
            context.drtProviderRepository.elements.toList(),
        ).asSequence()
    }

    override val dependentRepositories: Set<Repository<*, *>> = context.let {
        setOf(
            it.zoneRepository,
            it.sharingProviderRepository,
        )
    }

    override fun verifyInput(): Warning? = null

    override fun mockElementsForValidation(): List<SharingProviderAgent> = emptyList()
}

class CleanUpDataStep(
    private val context: BuildAgentsContext,
    private val builder: BuildAgents,
    private val cleanAllRepos: Boolean = false,
) : ModelStep {

    override val name = "clean data of builder" + if (cleanAllRepos) " and planned activity repository" else ""

    override fun execute() {
        builder.clear()

        if (cleanAllRepos) {
            context.plannedActivityRepository.clear()
            // TODO other repos as well?
        }
    }

    override fun verifyInput(): Warning? = null

    override fun mockBehavior(): Warning? = null
}
