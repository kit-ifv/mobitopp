package usecases.steps

import domain.agent.BuildAgents
import domain.agent.PersonAgent
import domain.agent.SharingProviderAgent
import domain.data.ActivityId
import domain.data.CarId
import domain.data.Household
import domain.data.HouseholdId
import domain.data.MutablePlannedActivity
import domain.data.Person
import domain.data.PersonId
import domain.data.PrivateCar
import domain.data.SharingProvider
import domain.data.SharingProviderId
import domain.data.Zone
import domain.data.ZoneId
import modeling.steps.AddResourceStep
import modeling.steps.Context
import modeling.steps.LazyResource
import modeling.steps.ModelStep
import modeling.steps.MutableRepository
import modeling.steps.Repository
import modeling.steps.Resource
import modeling.steps.SimulationContext
import modeling.validation.Warning
import usecases.models.ActivityDurationRandomizer
import usecases.models.NoDurationRandomizer

interface BuildAgentsContext : Context, SimulationContext {
    val zoneRepository: Repository<Zone, ZoneId>
    val householdRepository: Repository<Household, HouseholdId>
    val carRepository: Repository<PrivateCar, CarId>
    val personRepository: Repository<Person, PersonId>
    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>
    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>

    val personAgents: MutableRepository<PersonAgent, PersonId>
    val sharingProviderAgents: MutableRepository<SharingProviderAgent, SharingProviderId>
}

fun BuildAgentsContext.buildAgents(
    durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
) = runMultipleSteps {
    val builder = BuildAgents(simulationSeed, durationRandomizer)
    listOf(
        BuildProviderAgentsStep(this, builder),
        BuildPersonAgentsStep(this, builder),
        CleanUpDataStep(this, builder),
    )
}

class BuildPersonAgentsStep(
    context: BuildAgentsContext,
    builder: BuildAgents,
) : AddResourceStep<PersonAgent, PersonId>() {
    override val name = "build person agents"

    override val repository: MutableRepository<PersonAgent, PersonId> = context.personAgents
    override val resource: Resource<PersonAgent> = LazyResource(name, "BuildPersonAgentsStep") {
        builder.buildPersonAgents(
            context.householdRepository.elements.toList()
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

class BuildProviderAgentsStep(
    context: BuildAgentsContext,
    builder: BuildAgents,
) : AddResourceStep<SharingProviderAgent, SharingProviderId>() {
    override val name = "build provider agents"

    override val repository: MutableRepository<SharingProviderAgent, SharingProviderId> = context.sharingProviderAgents
    override val resource: Resource<SharingProviderAgent> = LazyResource(name, "BuildProviderAgentsStep") {
        builder.buildProviderAgents(
            context.sharingProviderRepository.elements.toList()
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
