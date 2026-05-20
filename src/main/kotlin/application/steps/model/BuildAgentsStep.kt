package application.steps.model

import core.modelsteps.Config
import application.steps.HasCarRepo
import application.steps.HasDrtProviderAgentRepo
import application.steps.HasDrtProviderRepo
import application.steps.HasHouseholdRepo
import application.steps.HasPersonAgentRepo
import application.steps.HasPersonBehavior
import application.steps.HasPersonRepo
import application.steps.HasSharingProviderAgentRepo
import application.steps.HasSharingProviderRepo
import application.steps.HasZoneRepo
import application.steps.SimulationConfig
import core.modelsteps.resources.LazyResource
import core.modelsteps.steps.addResourceStep
import core.modelsteps.steps.seal
import core.statemachine.StateMachineFactory
import domain.shared.location.Zone
import domain.simulation.agent.BuildAgents
import domain.simulation.agent.DrtAlgorithm
import domain.simulation.agent.DrtProviderAgent
import domain.simulation.agent.PersonAgent
import domain.simulation.agent.SharingProviderAgent
import domain.simulation.behavior.ActivityDurationRandomizer
import domain.simulation.behavior.GaussianActivityDurationRandomizer
import domain.simulation.behavior.NoDurationRandomizer
import domain.synthesis.data.DrtProvider
import domain.synthesis.data.Household
import domain.synthesis.data.Person
import domain.synthesis.data.PrivateCar
import domain.synthesis.data.SharingProvider

//TODO: remove distinction between agents and data object entities, then build agents is no longer needed

// TODO move to other file knowing about mobitopp when restructuring packages

/**
 * Creates a [GaussianActivityDurationRandomizer] based on the simulation duration.
 *
 * @param config The simulation configuration containing start and end times. Provided via context.
 * @return A [GaussianActivityDurationRandomizer] with a maximum duration equal to the simulation length.
 */
context(config: SimulationConfig)
fun gaussianDurationRandomizer() = GaussianActivityDurationRandomizer(
    max = config.simulationEnd.minus(config.simulationStart)
)

/**
 * Builds simulation agents for persons, DRT providers, and sharing providers.
 *
 * This step initializes the agents required for the simulation. It uses the provided state machine factories
 * and algorithms to create [PersonAgent], [DrtProviderAgent], and [SharingProviderAgent] instances from
 * the synthesized data.
 *
 * @receiver The simulation context [C].
 * @param C The context type. Must implement:
 *   - [HasPersonRepo] for [Person]
 *   - [HasZoneRepo] for [Zone]
 *   - [HasHouseholdRepo] for [Household]
 *   - [HasCarRepo] for [PrivateCar]
 *   - [HasSharingProviderRepo] for [SharingProvider]
 *   - [HasDrtProviderRepo] for [DrtProvider]
 *   - [HasPersonAgentRepo] for [PersonAgent]
 *   - [HasSharingProviderAgentRepo] for [SharingProviderAgent]
 *   - [HasDrtProviderAgentRepo] for [DrtProviderAgent]
 *   - [HasPersonBehavior]
 * @param config The general configuration. Provided via context. Must implement [Config].
 * @param personStateMachine Factory to create state machines for [PersonAgent]s.
 * @param durationRandomizer Randomizer for activity durations. Defaults to [NoDurationRandomizer].
 * @param drtStateMachine Optional factory to create state machines for [DrtProviderAgent]s.
 * @param drtAlgorithm Optional function to create [DrtAlgorithm]s for [DrtProvider]s.
 */
context(config: Config)
fun <C> C.buildSimulationAgents( //TODO refactor to Context requirements and onw model steps for setup
    personStateMachine: StateMachineFactory<PersonAgent>,
    durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
    drtStateMachine: StateMachineFactory<DrtProviderAgent>? = null,
    drtAlgorithm: ((DrtProvider) -> DrtAlgorithm)? = null
)
where C: HasPersonRepo<*, Person>,
      C: HasZoneRepo<*, Zone>,
      C: HasHouseholdRepo<*, Household>,
      C: HasCarRepo<*, PrivateCar>,
      C: HasSharingProviderRepo<*, SharingProvider>,
      C: HasDrtProviderRepo<*, DrtProvider>,
      C: HasPersonAgentRepo<PersonAgent, *>,
      C: HasSharingProviderAgentRepo<SharingProviderAgent, *>,
      C: HasDrtProviderAgentRepo<DrtProviderAgent, *>,
      C: HasPersonBehavior
{

    val builder by lazy {
        BuildAgents(
            seed = config.seed,
            personStateMachine,
            personBehavior,
            drtStateMachine,
            drtAlgorithm,
            durationRandomizer
        )
    }

    addResourceStep(
        name = "build sharing provider agents",
        resource = LazyResource("sharing provider agents by BuildAgents", "buildSimulationAgents") {
            builder.buildSharingProviderAgents(sharingProviderRepository.elements.toList()).asSequence()
        },
        repository = mutableSharingProviderAgentRepository,
        dependentRepositories = setOf(sharingProviderRepository, zoneRepository)
    )

    seal(mutableSharingProviderAgentRepository)

    addResourceStep(
        name = "build drt provider agents",
        resource = LazyResource("drt provider agents by BuildAgents", "buildSimulationAgents") {
            builder.buildDrtProviderAgents(drtProviderRepository.elements.toList()).asSequence()
        },
        repository = mutableDrtProviderAgentRepository,
        dependentRepositories = setOf(drtProviderRepository, zoneRepository)
    )

    seal(mutableDrtProviderAgentRepository)

    addResourceStep(
        name = "build person agents",
        resource = LazyResource("person agents by BuildAgents", "buildSimulationAgents") {
            builder.buildPersonAgents(householdRepository.elements.toList()).asSequence()
        },
        repository = mutablePersonAgentRepository,
        dependentRepositories = setOf(
            personRepository, householdRepository, carRepository, zoneRepository,
            sharingProviderRepository, drtProviderRepository
        )
    )

    seal(mutablePersonAgentRepository)


}

//
//      interface BuildAgentsContext : DemandSimContext {
//    val zoneRepository: Repository<Zone, ZoneId>
//    val householdRepository: Repository<Household, HouseholdId>
//    val carRepository: Repository<PrivateCar, CarId>
//    val personRepository: Repository<Person, PersonId>
//    val plannedActivityRepository: MutableRepository<MutablePlannedActivity, ActivityId>
//    val sharingProviderRepository: Repository<SharingProvider, SharingProviderId>
//    val drtProviderRepository: Repository<DrtProvider, DrtProviderId>
//
//    val personAgents: MutableRepository<PersonAgent, PersonId>
//    val sharingProviderAgents: MutableRepository<SharingProviderAgent, SharingProviderId>
//    val drtProviderAgents: MutableRepository<DrtProviderAgent, DrtProviderId>
//}

//fun BuildAgentsContext.buildAgents(
//    personStateMachine: StateMachineFactory<PersonAgent>,
//    durationRandomizer: ActivityDurationRandomizer = NoDurationRandomizer,
//    drtStateMachine: StateMachineFactory<DrtProviderAgent>? = null,
//    drtAlgorithm: ((DrtProvider) -> DrtAlgorithm)? = null
//) = runMultipleSteps {
//    val builder = BuildAgents(
//        simulationSeed,
//        personStateMachine,
//        behavior.value,
//        drtStateMachine,
//        drtAlgorithm,
//        durationRandomizer
//    )
//    listOf(
//        BuildSharingProviderAgentsStep(this, builder),
//        BuildDrtProviderAgentsStep(this, builder),
//        BuildPersonAgentsStep(this, builder),
//        CleanUpDataStep(this, builder),
//    )
//}
//
//class BuildPersonAgentsStep(
//    context: BuildAgentsContext,
//    builder: BuildAgents,
//) : AbstractAddResourceStep<PersonAgent, PersonId>() {
//    override val name = "build person agents"
//
//    override val repository: MutableRepository<PersonAgent, PersonId> = context.personAgents
//    override val resource: Resource<PersonAgent> = LazyResource(name, "BuildPersonAgentsStep") {
//        builder.buildPersonAgents(
//            context.householdRepository.elements.toList()
//        ).asSequence()
//    }
//
//    override val dependentRepositories: Set<Repository<*, *>> = context.let {
//        setOf(
//            it.zoneRepository,
//            it.householdRepository,
//            it.carRepository,
//            it.sharingProviderRepository,
//        )
//    }
//
//    override fun verifyInput(): Warning? = null
//
//    override fun mockElementsForValidation(): List<PersonAgent> = emptyList()
//}
//
//class BuildSharingProviderAgentsStep(
//    context: BuildAgentsContext,
//    builder: BuildAgents,
//) : AbstractAddResourceStep<SharingProviderAgent, SharingProviderId>() {
//    override val name = "build sharing provider agents"
//
//    override val repository: MutableRepository<SharingProviderAgent, SharingProviderId> = context.sharingProviderAgents
//    override val resource: Resource<SharingProviderAgent> = LazyResource(name, "BuildSharingProviderAgentsStep") {
//        builder.buildSharingProviderAgents(
//            context.sharingProviderRepository.elements.toList(),
//        ).asSequence()
//    }
//
//    override val dependentRepositories: Set<Repository<*, *>> = context.let {
//        setOf(
//            it.zoneRepository,
//            it.sharingProviderRepository,
//        )
//    }
//
//    override fun verifyInput(): Warning? = null
//
//    override fun mockElementsForValidation(): List<SharingProviderAgent> = emptyList()
//}
//
//class BuildDrtProviderAgentsStep(
//    context: BuildAgentsContext,
//    builder: BuildAgents,
//) : AbstractAddResourceStep<DrtProviderAgent, DrtProviderId>() {
//    override val name = "build drt provider agents"
//
//    override val repository: MutableRepository<DrtProviderAgent, DrtProviderId> = context.drtProviderAgents
//    override val resource: Resource<DrtProviderAgent> = LazyResource(name, "BuildDrtProviderAgentsStep") {
//        builder.buildDrtProviderAgents(
//            context.drtProviderRepository.elements.toList(),
//        ).asSequence()
//    }
//
//    override val dependentRepositories: Set<Repository<*, *>> = context.let {
//        setOf(
//            it.zoneRepository,
//            it.drtProviderRepository,
//        )
//    }
//
//    override fun verifyInput(): Warning? = null
//
//    override fun mockElementsForValidation(): List<DrtProviderAgent> = emptyList()
//}
//
//class CleanUpDataStep(
//    private val context: BuildAgentsContext,
//    private val builder: BuildAgents,
//    private val cleanAllRepos: Boolean = false,
//) : ModelStep {
//
//    override val name = "clean data of builder" + if (cleanAllRepos) " and planned activity repository" else ""
//
//    override fun execute() {
//        builder.clear()
//
//        if (cleanAllRepos) {
//            context.plannedActivityRepository.clear()
//            // TODO other repos as well?
//        }
//    }
//
//    override fun verifyInput(): Warning? = null
//
//    override fun mockBehavior(): Warning? = null
//}
